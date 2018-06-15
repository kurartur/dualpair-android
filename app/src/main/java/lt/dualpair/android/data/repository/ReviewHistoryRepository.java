package lt.dualpair.android.data.repository;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.DualPairRoomDatabase;
import lt.dualpair.android.data.local.dao.MatchDao;
import lt.dualpair.android.data.local.dao.SwipeDao;
import lt.dualpair.android.data.local.dao.UserDao;
import lt.dualpair.android.data.local.entity.Swipe;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserListItem;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.mapper.MatchResourceMapper;
import lt.dualpair.android.data.mapper.UserResourceMapper;
import lt.dualpair.android.data.remote.client.match.GetUserMatchListClient;
import lt.dualpair.android.data.remote.resource.Match;
import lt.dualpair.android.data.remote.resource.ResourceCollection;

public class ReviewHistoryRepository {

    private SwipeDao swipeDao;
    private UserDao userDao;
    private MatchDao matchDao;
    private Long userPrincipalId;
    private MatchResourceMapper matchResourceMapper;
    private DualPairRoomDatabase database;

    public ReviewHistoryRepository(Application application) {
        userPrincipalId = AccountUtils.getUserId(application);
        database = DualPairRoomDatabase.getDatabase(application);
        swipeDao = database.swipeDao();
        userDao = database.userDao();
        matchDao = database.matchDao();
        matchResourceMapper = new MatchResourceMapper(userPrincipalId, new UserResourceMapper(database.sociotypeDao()));
    }

    public Flowable<List<UserListItem>> getReviewedUsers() {
        return swipeDao.getSwipesFlowable()
                .map(new Function<List<Swipe>, List<UserListItem>>() {
                    @Override
                    public List<UserListItem> apply(List<Swipe> swipes) throws Exception {
                        List<UserListItem> items = new ArrayList<>();
                        for (Swipe swipe : swipes) {
                            Long userId = swipe.getWho();
                            Long reference = swipe.getId();
                            User user = userDao.getUser(userId);
                            List<UserAccount> accounts = userDao.getUserAccounts(userId);
                            UserPhoto photo = userDao.getUserPhotos(userId).get(0);
                            items.add(new UserListItem(reference, user, accounts, photo));
                        }
                        return items;
                    }
                });
    }

    public Completable loadFromApi() {
        return new GetUserMatchListClient(userPrincipalId, GetUserMatchListClient.REVIEWED).observable()
                .doOnNext(new Consumer<ResourceCollection<Match>>() {
                    @Override
                    public void accept(ResourceCollection<Match> matchResourceCollection) throws Exception {
                        for (Match matchResource : matchResourceCollection.getContent()) {
                            saveResource(matchResource);
                        }
                    }
                }).ignoreElements();
    }

    private void saveResource(lt.dualpair.android.data.remote.resource.Match matchResource) {
        MatchResourceMapper.Result mappingResult = matchResourceMapper.map(matchResource);
        UserResourceMapper.Result userMappingResult = mappingResult.getUserMappingResult();
        Long opponentUserId = userMappingResult.getUser().getId();
        database.runInTransaction(new Runnable() {
            @Override
            public void run() {
                userDao.saveUser(userMappingResult.getUser());
                userDao.replaceUserAccounts(opponentUserId, userMappingResult.getUserAccounts());
                userDao.replaceUserPhotos(opponentUserId, userMappingResult.getUserPhotos());
                userDao.replaceUserSociotypes(opponentUserId, userMappingResult.getUserSociotypes());
                userDao.replaceUserLocations(opponentUserId, userMappingResult.getUserLocations());
                userDao.replaceUserPurposesOfBeing(opponentUserId, userMappingResult.getUserPurposesOfBeing());
                swipeDao.save(mappingResult.getSwipe());
                if (mappingResult.getMatch() != null) {
                    matchDao.saveMatch(mappingResult.getMatch());
                }
            }
        });
    }

}
