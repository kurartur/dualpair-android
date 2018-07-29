package lt.dualpair.android.data.remote.client;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.match.MatchService;
import retrofit2.Retrofit;

public class UnmatchClient extends CompletableClient {

    private Long userId;
    private Long matchId;

    public UnmatchClient(Long userId, Long matchId) {
        this.userId = userId;
        this.matchId = matchId;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(MatchService.class).unmatch(userId, matchId);
    }
}
