package lt.dualpair.android.data.repository;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.DualPairRoomDatabase;
import lt.dualpair.android.data.local.dao.MatchDao;
import lt.dualpair.android.data.local.dao.SociotypeDao;
import lt.dualpair.android.data.local.dao.UserDao;
import lt.dualpair.android.data.local.dao.UserResponseDao;
import lt.dualpair.android.data.local.entity.FullUserSociotype;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.local.entity.UserSearchParameters;
import lt.dualpair.android.data.local.entity.UserSociotype;
import lt.dualpair.android.data.mapper.MatchResourceMapper;
import lt.dualpair.android.data.mapper.UserResourceMapper;
import lt.dualpair.android.data.remote.client.match.SetResponseClient;
import lt.dualpair.android.data.remote.client.user.FindUserClient;
import lt.dualpair.android.data.remote.client.user.GetUserClient;
import lt.dualpair.android.data.remote.client.user.ReportUserClient;
import lt.dualpair.android.data.remote.resource.Response;

public class UserRepository {

    private final DualPairRoomDatabase database;
    private final SociotypeDao sociotypeDao;
    private final UserDao userDao;
    private final MatchDao matchDao;
    private final UserResponseDao userResponseDao;
    private final MatchResourceMapper matchResourceMapper;
    private final UserResourceMapper userResourceMapper;
    private final Long userPrincipalId;

    public UserRepository(Application application) {
        database = DualPairRoomDatabase.getDatabase(application);
        sociotypeDao = database.sociotypeDao();
        userDao = database.userDao();
        matchDao = database.matchDao();
        userResponseDao = database.swipeDao();
        userPrincipalId = AccountUtils.getUserId(application);
        userResourceMapper = new UserResourceMapper(sociotypeDao);
        matchResourceMapper = new MatchResourceMapper(userResourceMapper);
    }

    public Maybe<UserForView> find(UserSearchParameters usp) {
        return new FindUserClient(usp.getMinAge(), usp.getMaxAge(), usp.getSearchFemale(), usp.getSearchMale()).observable()
                .map(new Function<lt.dualpair.android.data.remote.resource.User, UserForView>() {
                    @Override
                    public UserForView apply(lt.dualpair.android.data.remote.resource.User userResource) {
                        UserResourceMapper.Result userMappingResult = userResourceMapper.map(userResource);
                        return new UserForView(
                                userMappingResult.getUser(),
                                userMappingResult.getUserPhotos(),
                                loadSociotypes(userMappingResult.getUserSociotypes()),
                                userMappingResult.getUserPurposesOfBeing(),
                                userMappingResult.getUserLocations() != null ? userMappingResult.getUserLocations().iterator().next() : null,
                                userMappingResult.getUserAccounts(),
                                null,
                                null);
                    }
                }).firstElement();
    }

    public Single<UserForView> getUser(Long userId) {
        Maybe<User> localUser = userDao.getUserMaybe(userId);
        Single<User> remoteUser = userFromApiObservable(userId)
                .map(user -> userResourceMapper.map(user).getUser()).singleOrError();
        return Maybe.concat(localUser, remoteUser.toMaybe()).firstElement().toSingle()
                .map(new Function<User, UserForView>() {
                    @Override
                    public UserForView apply(User user) throws Exception {
                        Long userId = user.getId();
                        return new UserForView(
                                user,
                                userDao.getUserPhotos(userId),
                                userDao.getFullUserSociotypes(userId),
                                userDao.getUserPurposesOfBeing(userId),
                                userDao.getLastLocation(userId),
                                userDao.getUserAccounts(userId),
                                matchDao.getMatchByOpponent(userId),
                                userResponseDao.getResponse(userId)
                        );
                    }
                });
    }

    private Observable<lt.dualpair.android.data.remote.resource.User> userFromApiObservable(Long userId) {
        return new GetUserClient(userId).observable()
                .subscribeOn(Schedulers.io())
                .doOnNext(userResource -> saveUserResource(userResource));
    }

    private void saveUserResource(lt.dualpair.android.data.remote.resource.User userResource) {
        UserResourceMapper.Result userMappingResult = userResourceMapper.map(userResource);
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
            }
        });
    }

    private List<FullUserSociotype> loadSociotypes(List<UserSociotype> userSociotypes) {
        List<FullUserSociotype> fullSociotypes = new ArrayList<>();
        for (UserSociotype userSociotype : userSociotypes) {
            fullSociotypes.add(new FullUserSociotype(userSociotype, sociotypeDao.getSociotypeById(userSociotype.getSociotypeId())));
        }
        return fullSociotypes;
    }

    public Completable respondWithYes(Long toUserId) {
        return new SetResponseClient(userPrincipalId, toUserId, Response.YES).completable();
    }

    public Completable respondWithNo(Long toUserId) {
        return new SetResponseClient(userPrincipalId, toUserId, Response.NO).completable();
    }

    public Completable unmatch(Long userId) {
        return respondWithNo(userId);
    }

    public Completable report(Long userId) {
        return new ReportUserClient(userId).completable();
    }
}
