package lt.dualpair.android.data.repository;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.ConnectivityMonitor;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.DualPairRoomDatabase;
import lt.dualpair.android.data.local.dao.MatchDao;
import lt.dualpair.android.data.local.entity.Match;
import lt.dualpair.android.data.local.entity.UserListItem;
import lt.dualpair.android.data.remote.client.ServiceException;
import lt.dualpair.android.data.remote.client.match.GetUserMatchesClient;
import lt.dualpair.android.data.remote.resource.ResourceCollection;

public class MatchRepository {

    private Long userPrincipalId;
    private MatchDao matchDao;

    public MatchRepository(Application application) {
        userPrincipalId = AccountUtils.getUserId(application);
        DualPairRoomDatabase database = DualPairRoomDatabase.getDatabase(application);
        matchDao = database.matchDao();
    }

    public Flowable<List<UserListItem>> getMatches() {
        return matchDao.getMatchesFlowable()
                .doOnNext(new Consumer<List<Match>>() {
                    @Override
                    public void accept(List<Match> matches) throws Exception {
                        if (matches.isEmpty()) {
                            loadMatchesFromApi()
                                    .subscribeOn(Schedulers.io())
                                    .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                                        @Override
                                        public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                                            return throwableObservable.flatMap(new Function<Throwable, Observable<?>>() {
                                                @Override
                                                public Observable<?> apply(Throwable throwable) throws Exception {
                                                    if (throwable instanceof ServiceException) {
                                                        ServiceException se = (ServiceException) throwable;
                                                        if (se.getKind() == ServiceException.Kind.NETWORK) {
                                                            return ConnectivityMonitor.getInstance().getConnectivityInfo()
                                                                    .filter(ConnectivityMonitor.ConnectivityInfo::isNetworkAvailable);
                                                        }
                                                    }
                                                    return Observable.error(throwable);
                                                }
                                            });
                                        }
                                    }).ignoreElements()
                                    .subscribe();
                        }
                    }
                }).map(matches -> {
                    List<UserListItem> items = new ArrayList<>();
                    for (Match match : matches) {
                        UserListItem item = new UserListItem(match.getOpponentId(), match.getName(), match.getPhotoSource());
                        items.add(item);
                    }
                    return items;
                });
    }

    public Observable<ResourceCollection<lt.dualpair.android.data.remote.resource.Match>> loadMatchesFromApi() {
        return new GetUserMatchesClient(userPrincipalId).observable()
                .doOnNext(new Consumer<ResourceCollection<lt.dualpair.android.data.remote.resource.Match>>() {
                    @Override
                    public void accept(ResourceCollection<lt.dualpair.android.data.remote.resource.Match> matchResourceCollection) throws Exception {
                        deleteDeprecatedMatches(matchResourceCollection.getContent());
                        for (lt.dualpair.android.data.remote.resource.Match matchResource : matchResourceCollection.getContent()) {
                            saveMatchResource(matchResource);
                        }
                    }
                });
    }

    private void saveMatchResource(lt.dualpair.android.data.remote.resource.Match matchResource) {
        matchDao.saveMatch(mapMatchResource(matchResource));
    }

    @NonNull
    private Match mapMatchResource(lt.dualpair.android.data.remote.resource.Match matchResource) {
        Match match = new Match();
        match.setMatchId(matchResource.getId());
        match.setOpponentId(matchResource.getUser().getId());
        match.setDate(matchResource.getDate());
        match.setName(matchResource.getUser().getName());
        match.setPhotoSource(matchResource.getUser().getPhotos().get(0).getSource());
        return match;
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
            result += prefix + match.getUser().getId();
            prefix = " ,";
        }
        return result;
    }

}
