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
import lt.dualpair.android.data.local.entity.Match;
import lt.dualpair.android.data.local.entity.UserListItem;
import lt.dualpair.android.data.remote.client.match.GetUserMatchesClient;
import lt.dualpair.android.data.remote.resource.ResourceCollection;

public class MatchRepository {

    private Long userPrincipalId;
    private DualPairRoomDatabase database;
    private MatchDao matchDao;

    public MatchRepository(Application application) {
        userPrincipalId = AccountUtils.getUserId(application);
        database = DualPairRoomDatabase.getDatabase(application);
        matchDao = database.matchDao();
    }

    public Flowable<List<UserListItem>> getMatches() {
        return matchDao.getMatchesFlowable()
                .map(new Function<List<Match>, List<UserListItem>>() {
                    @Override
                    public List<UserListItem> apply(List<Match> matches) throws Exception {
                        List<UserListItem> items = new ArrayList<>();
                        for (Match match : matches) {
                            UserListItem item = new UserListItem(match.getOpponentId(), match.getName(), match.getPhotoSource());
                            items.add(item);
                        }
                        return items;
                    }
                });
    }

    public Completable loadMatchesFromApi() {
        return new GetUserMatchesClient(userPrincipalId).observable()
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
        Match match = new Match();
        match.setOpponentId(matchResource.getUser().getId());
        match.setDate(matchResource.getDate());
        match.setName(matchResource.getUser().getName());
        match.setPhotoSource(matchResource.getUser().getPhotos().get(0).getSourceUrl());
        matchDao.saveMatch(match);
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
