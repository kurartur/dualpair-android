package lt.dualpair.android.data.repository;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.DualPairRoomDatabase;
import lt.dualpair.android.data.local.dao.SociotypeDao;
import lt.dualpair.android.data.local.entity.Match;
import lt.dualpair.android.data.mapper.UserResourceMapper;
import lt.dualpair.android.data.remote.client.match.GetUserMatchListClient;
import lt.dualpair.android.data.resource.ResourceCollection;

public class MatchRepository {

    private Long userId;
    private DualPairRoomDatabase database;
    private SociotypeDao sociotypeDao;

    public MatchRepository(Application application) {
        userId = AccountUtils.getUserId(application);
        database = DualPairRoomDatabase.getDatabase(application);
        sociotypeDao = database.sociotypeDao();
    }

    public Single<List<Match>> getMatches() {
        return new GetUserMatchListClient(userId, GetUserMatchListClient.MUTUAL).observable()
                .singleOrError()
                .map(new Function<ResourceCollection<lt.dualpair.android.data.resource.Match>, List<Match>>() {
                    @Override
                    public List<Match> apply(ResourceCollection<lt.dualpair.android.data.resource.Match> matchResourceCollection) {
                        List<Match> matches = new ArrayList<>();
                        UserResourceMapper mapper = new UserResourceMapper(sociotypeDao);
                        for (lt.dualpair.android.data.resource.Match matchResource : matchResourceCollection.getContent()) {
                            UserResourceMapper.Result mappingResult = mapper.map(matchResource.getOpponent().getUser());
                            matches.add(new Match(matchResource.getId(), mappingResult.getUser(), mappingResult.getUserAccounts(), mappingResult.getUserPhotos()));
                        }
                        return matches;
                    }
                });
    }

}
