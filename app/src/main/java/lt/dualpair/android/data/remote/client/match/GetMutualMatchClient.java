package lt.dualpair.android.data.remote.client.match;

import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.Match;
import retrofit2.Retrofit;
import rx.Observable;

public class GetMutualMatchClient extends BaseClient<Match> {

    private Long userId;
    private Long matchId;

    public GetMutualMatchClient(Long userId, Long matchId) {
        this.userId = userId;
        this.matchId = matchId;
    }

    @Override
    protected Observable<Match> getApiObserable(Retrofit retrofit) {
        return retrofit.create(MatchService.class).getUserMatch(userId, matchId);
    }
}
