package lt.dualpair.android.data.repository;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.DualPairRoomDatabase;
import lt.dualpair.android.data.local.dao.SociotypeDao;
import lt.dualpair.android.data.local.entity.FullUserSociotype;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.local.entity.UserSearchParameters;
import lt.dualpair.android.data.local.entity.UserSociotype;
import lt.dualpair.android.data.mapper.UserResourceMapper;
import lt.dualpair.android.data.remote.client.match.GetMutualMatchClient;
import lt.dualpair.android.data.remote.client.match.GetNextMatchClient;
import lt.dualpair.android.data.remote.client.match.SetResponseClient;
import lt.dualpair.android.data.remote.client.user.ReportUserClient;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Response;

public class UserRepository {

    private final DualPairRoomDatabase database;
    private final SociotypeDao sociotypeDao;
    private final Long userPrincipalId;

    public UserRepository(Application application) {
        database = DualPairRoomDatabase.getDatabase(application);
        sociotypeDao = database.sociotypeDao();
        userPrincipalId = AccountUtils.getUserId(application);
    }

    public Maybe<UserForView> next(UserSearchParameters usp) {
        return new GetNextMatchClient(usp.getMinAge(), usp.getMaxAge(), usp.getSearchFemale(), usp.getSearchMale()).observable()
                .map(new Function<Match, UserForView>() {
                    @Override
                    public UserForView apply(Match match) {
                        UserResourceMapper.Result mappingResult = new UserResourceMapper(database.sociotypeDao()).map(match.getOpponent().getUser());
                        return new UserForView(
                                match.getUser().getId(),
                                mappingResult.getUser(),
                                mappingResult.getUserPhotos(),
                                loadSociotypes(mappingResult.getUserSociotypes()),
                                mappingResult.getUserPurposesOfBeing(),
                                mappingResult.getUserLocations() != null ? mappingResult.getUserLocations().iterator().next() : null,
                                match.isMutual() ? match.getId() : null,
                                mappingResult.getUserAccounts());
                    }
                }).firstElement();
    }

    public Single<UserForView> getUser(Long userId) {
        return new GetMutualMatchClient(userPrincipalId, userId).observable()
                .map(new Function<Match, UserForView>() {
                    @Override
                    public UserForView apply(Match match) {
                        UserResourceMapper.Result mappingResult = new UserResourceMapper(database.sociotypeDao()).map(match.getOpponent().getUser());
                        return new UserForView(
                                match.getUser().getId(),
                                mappingResult.getUser(),
                                mappingResult.getUserPhotos(),
                                loadSociotypes(mappingResult.getUserSociotypes()),
                                mappingResult.getUserPurposesOfBeing(),
                                mappingResult.getUserLocations() != null ? mappingResult.getUserLocations().iterator().next() : null,
                                match.isMutual() ? match.getId() : null,
                                mappingResult.getUserAccounts());
                    }
                }).singleOrError();
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
                        return new SetResponseClient(userForView.getReference(), Response.NO).completable();
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
