package lt.dualpair.android.data.repository;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.DualPairRoomDatabase;
import lt.dualpair.android.data.local.dao.MatchDao;
import lt.dualpair.android.data.local.dao.SociotypeDao;
import lt.dualpair.android.data.local.dao.SwipeDao;
import lt.dualpair.android.data.local.dao.UserDao;
import lt.dualpair.android.data.local.entity.FullUserSociotype;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.local.entity.UserSearchParameters;
import lt.dualpair.android.data.local.entity.UserSociotype;
import lt.dualpair.android.data.mapper.MatchResourceMapper;
import lt.dualpair.android.data.mapper.UserResourceMapper;
import lt.dualpair.android.data.remote.client.match.GetMutualMatchClient;
import lt.dualpair.android.data.remote.client.match.GetNextMatchClient;
import lt.dualpair.android.data.remote.client.match.SetResponseClient;
import lt.dualpair.android.data.remote.client.user.ReportUserClient;
import lt.dualpair.android.data.remote.resource.Response;

public class UserRepository {

    private final DualPairRoomDatabase database;
    private final SociotypeDao sociotypeDao;
    private final UserDao userDao;
    private final MatchDao matchDao;
    private final SwipeDao swipeDao;
    private final MatchResourceMapper matchResourceMapper;
    private final UserResourceMapper userResourceMapper;
    private final Long userPrincipalId;

    public UserRepository(Application application) {
        database = DualPairRoomDatabase.getDatabase(application);
        sociotypeDao = database.sociotypeDao();
        userDao = database.userDao();
        matchDao = database.matchDao();
        swipeDao = database.swipeDao();
        userPrincipalId = AccountUtils.getUserId(application);
        userResourceMapper = new UserResourceMapper(sociotypeDao);
        matchResourceMapper = new MatchResourceMapper(userPrincipalId, userResourceMapper);
    }

    public Maybe<UserForView> next(UserSearchParameters usp) {
        return new GetNextMatchClient(usp.getMinAge(), usp.getMaxAge(), usp.getSearchFemale(), usp.getSearchMale()).observable()
                .map(new Function<lt.dualpair.android.data.remote.resource.Match, UserForView>() {
                    @Override
                    public UserForView apply(lt.dualpair.android.data.remote.resource.Match matchResource) {
                        MatchResourceMapper.Result mappingResult = matchResourceMapper.map(matchResource);
                        UserResourceMapper.Result userMappingResult = mappingResult.getUserMappingResult();
                        return new UserForView(
                                userMappingResult.getUser(),
                                userMappingResult.getUserPhotos(),
                                loadSociotypes(userMappingResult.getUserSociotypes()),
                                userMappingResult.getUserPurposesOfBeing(),
                                userMappingResult.getUserLocations() != null ? userMappingResult.getUserLocations().iterator().next() : null,
                                userMappingResult.getUserAccounts(),
                                mappingResult.getMatch(),
                                mappingResult.getSwipe());
                    }
                }).firstElement();
    }

    public Single<UserForView> getUser(Long userId) {
        Maybe<User> localUser = userDao.getUserMaybe(userId);
        Single<User> remoteUser = Single.fromCallable(() -> matchDao.getMatchByOpponent(userId).getId())
                .flatMap((Function<Long, Single<User>>) aLong -> {
                    return userFromApiObservable(userId)
                            .map(match -> userResourceMapper.map(match.getOpponent().getUser()).getUser()).singleOrError();
                });
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
                                swipeDao.getSwipeByOpponent(userId)
                        );
                    }
                });
    }

    private Observable<lt.dualpair.android.data.remote.resource.Match> userFromApiObservable(Long matchId) {
        return new GetMutualMatchClient(userPrincipalId, matchId).observable()
                .subscribeOn(Schedulers.io())
                .doOnNext(userResource -> saveMatchResource(userResource));
    }

    public Single<UserForView> getUserByMatchId(Long matchId) {
        return userFromApiObservable(matchId)
                .map(match -> userResourceMapper.map(match.getOpponent().getUser()).getUser())
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
                                swipeDao.getSwipeByOpponent(userId)
                        );
                    }
                }).singleOrError();
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

    private List<FullUserSociotype> loadSociotypes(List<UserSociotype> userSociotypes) {
        List<FullUserSociotype> fullSociotypes = new ArrayList<>();
        for (UserSociotype userSociotype : userSociotypes) {
            fullSociotypes.add(new FullUserSociotype(userSociotype, sociotypeDao.getSociotypeById(userSociotype.getSociotypeId())));
        }
        return fullSociotypes;
    }

    public Completable respondWithYes(Long reference) {
        return new SetResponseClient(reference, Response.YES).completable();
    }

    public Completable respondWithNo(Long reference) {
        return new SetResponseClient(reference, Response.NO).completable();
    }

    public Completable unmatch(Long userId) {
        return getUser(userId)
                .flatMapCompletable(new Function<UserForView, CompletableSource>() {
                    @Override
                    public CompletableSource apply(UserForView userForView) throws Exception {
                        return new SetResponseClient(userForView.getSwipe().getId(), Response.NO).completable();
                    }
                });
    }

    public Completable report(Long userId) {
        return getUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable(new Function<UserForView, Completable>() {
                    @Override
                    public Completable apply(UserForView userForView) {
                        return new ReportUserClient(userForView.getUser().getId()).completable();
                    }
                });
    }
}
