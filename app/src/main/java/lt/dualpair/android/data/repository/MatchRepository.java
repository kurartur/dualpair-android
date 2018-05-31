package lt.dualpair.android.data.repository;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.DualPairRoomDatabase;
import lt.dualpair.android.data.local.dao.MatchDao;
import lt.dualpair.android.data.local.dao.SociotypeDao;
import lt.dualpair.android.data.local.dao.UserDao;
import lt.dualpair.android.data.local.entity.Match;
import lt.dualpair.android.data.local.entity.MatchForListView;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.mapper.UserResourceMapper;
import lt.dualpair.android.data.remote.client.match.GetUserMatchListClient;
import lt.dualpair.android.data.resource.ResourceCollection;

public class MatchRepository {

    private Long userId;
    private DualPairRoomDatabase database;
    private UserDao userDao;
    private SociotypeDao sociotypeDao;
    private MatchDao matchDao;

    public MatchRepository(Application application) {
        userId = AccountUtils.getUserId(application);
        database = DualPairRoomDatabase.getDatabase(application);
        sociotypeDao = database.sociotypeDao();
        userDao = database.userDao();
        matchDao = database.matchDao();
    }

    public Flowable<List<MatchForListView>> getMatches() {
        return matchDao.getMatchesFlowable()
                .map(new Function<List<Match>, List<MatchForListView>>() {
                    @Override
                    public List<MatchForListView> apply(List<Match> matches) throws Exception {
                        List<MatchForListView> matchesForListView = new ArrayList<>();
                        for (Match match : matches) {
                            User user = userDao.getUser(match.getOpponentId());
                            List<UserAccount> userAccounts = userDao.getUserAccounts(match.getOpponentId());
                            List<UserPhoto> userPhotos = userDao.getUserPhotos(match.getOpponentId());
                            MatchForListView matchForListView = new MatchForListView(match, user, userAccounts, userPhotos);
                            matchesForListView.add(matchForListView);
                        }
                        return matchesForListView;
                    }
                });
    }

    public Observable<List<Match>> loadMatchesFromApi() {
        return new GetUserMatchListClient(userId, GetUserMatchListClient.MUTUAL).observable()
                .map(new Function<ResourceCollection<lt.dualpair.android.data.resource.Match>, List<Match>>() {
                    @Override
                    public List<Match> apply(ResourceCollection<lt.dualpair.android.data.resource.Match> matchResourceCollection) throws Exception {
                        List<Match> matches = new ArrayList<>();
                        UserResourceMapper mapper = new UserResourceMapper(sociotypeDao);
                        deleteDeprecatedMatches(matchResourceCollection.getContent());
                        for (lt.dualpair.android.data.resource.Match matchResource : matchResourceCollection.getContent()) {
                            UserResourceMapper.Result mappingResult = mapper.map(matchResource.getOpponent().getUser());
                            Match match = new Match();
                            match.setId(matchResource.getId());
                            match.setOpponentId(mappingResult.getUser().getId());
                            database.runInTransaction(new Runnable() {
                                @Override
                                public void run() {
                                    userDao.saveUser(mappingResult.getUser());
                                    userDao.saveUserAccounts(mappingResult.getUserAccounts());
                                    userDao.saveUserPhotos(mappingResult.getUserPhotos());
                                    userDao.saveUserSociotypes(mappingResult.getUserSociotypes());
                                    matchDao.saveMatch(match);
                                }
                            });
                            matches.add(match);
                        }
                        return matches;
                    }
                });
    }

    private void deleteDeprecatedMatches(List<lt.dualpair.android.data.resource.Match> matches) {
        if (matches.isEmpty()) {
            matchDao.deleteAll();
        } else {
            matchDao.deleteNotIn(buildInString(matches));
        }
    }

    private String buildInString(List<lt.dualpair.android.data.resource.Match> matches) {
        String result = "";
        String prefix = "";
        List<String> ids = new ArrayList<>();
        for (lt.dualpair.android.data.resource.Match match : matches) {
            result += prefix + match.getId().toString();
            prefix = " ,";
        }
        return result;
    }
}
