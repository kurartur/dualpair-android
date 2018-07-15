package lt.dualpair.android.data.remote.client.match;

import java.util.Date;

import io.reactivex.Observable;
import lt.dualpair.android.data.remote.client.ObservableClient;
import lt.dualpair.android.data.remote.resource.Match;
import lt.dualpair.android.data.remote.resource.ResourceCollection;
import retrofit2.Retrofit;

public class GetUserMatchesClient extends ObservableClient<ResourceCollection<Match>> {

    private Long userId;

    public GetUserMatchesClient(Long userId) {
        this.userId = userId;
    }

    @Override
    protected Observable<ResourceCollection<Match>> getApiObserable(Retrofit retrofit) {
        MatchService matchService = retrofit.create(MatchService.class);
        return matchService.getUserMutualMatches(userId, new Date().getTime());
    }
}
