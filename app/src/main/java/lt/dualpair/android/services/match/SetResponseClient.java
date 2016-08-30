package lt.dualpair.android.services.match;

import lt.dualpair.android.resource.Match;
import lt.dualpair.android.resource.Response;
import lt.dualpair.android.services.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class SetResponseClient extends BaseClient<Match> {

    private Long matchId;
    private Response response;

    public SetResponseClient(Long matchId, Response response) {
        this.matchId = matchId;
        this.response = response;
    }

    @Override
    protected Observable<Match> getApiObserable(Retrofit retrofit) {
        return retrofit.create(MatchService.class).setResponse(matchId, response.name());
    }
}
