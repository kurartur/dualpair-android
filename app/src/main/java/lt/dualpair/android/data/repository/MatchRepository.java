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
import lt.dualpair.android.data.local.dao.SociotypeDao;
import lt.dualpair.android.data.local.dao.SwipeDao;
import lt.dualpair.android.data.local.dao.UserDao;
import lt.dualpair.android.data.local.entity.Match;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserListItem;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.mapper.MatchResourceMapper;
import lt.dualpair.android.data.mapper.UserResourceMapper;
import lt.dualpair.android.data.remote.client.match.GetUserMatchListClient;
import lt.dualpair.android.data.remote.resource.ResourceCollection;

public class MatchRepository {

    private Long userId;
    private DualPairRoomDatabase database;
    private UserDao userDao;
    private SociotypeDao sociotypeDao;
    private MatchDao matchDao;
    private SwipeDao swipeDao;
    private MatchResourceMapper matchResourceMapper;

    public MatchRepository(Application application) {
        userId = AccountUtils.getUserId(application);
        database = DualPairRoomDatabase.getDatabase(application);
        sociotypeDao = database.sociotypeDao();
        userDao = database.userDao();
        matchDao = database.matchDao();
        swipeDao = database.swipeDao();
        matchResourceMapper = new MatchResourceMapper(userId, new UserResourceMapper(sociotypeDao));
    }

    public Flowable<List<UserListItem>> getMatches() {
        return matchDao.getMatchesFlowable()
                .map(new Function<List<Match>, List<UserListItem>>() {
                    @Override
                    public List<UserListItem> apply(List<Match> matches) throws Exception {
                        List<UserListItem> items = new ArrayList<>();
                        for (Match match : matches) {
                            Long userId = match.getOpponentId();
                            User user = userDao.getUser(userId);
                            List<UserAccount> accounts = userDao.getUserAccounts(userId);
                            UserPhoto photo = userDao.getUserPhotos(userId).get(0);
                            UserListItem item = new UserListItem(match.getId(), user, accounts, photo);
                            items.add(item);
                        }
                        return items;
                    }
                });
    }

    public Completable loadMatchesFromApi() {
        return new GetUserMatchListClient(userId, GetUserMatchListClient.MUTUAL).observable()
                .doOnNext(new Consumer<ResourceCollection<lt.dualpair.android.data.remote.resource.Match>>() {
                    @Override
                    public void accept(ResourceCollection<lt.dualpair.android.data.remote.resource.Match> matchResourceCollection) throws Exception {
                        deleteDeprecatedMatches(matchResourceCollection.getContent());
                        for (lt.dualpair.android.data.remote.resource.Match matchResource : matchResourceCollection.getContent()) {
                            saveMatchResource(matchResource);
                        }
                    }
                }).ignoreElements();
    }

    private void saveMatchResource(lt.dualpair.android.data.remote.resource.Match matchResource) {
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

    private void deleteDeprecatedMatches(List<lt.dualpair.android.data.remote.resource.Match> matches) {
        if (matches.isEmpty()) {
            matchDao.deleteAll();
        } else {
            matchDao.deleteNotIn(buildInString(matches));
        }
    }

    private String buildInString(List<lt.dualpair.android.data.remote.resource.Match> matches) {
        String result = "";
        String prefix = "";
        for (lt.dualpair.android.data.remote.resource.Match match : matches) {
            result += prefix + match.getId().toString();
            prefix = " ,";
        }
        return result;
    }

}
