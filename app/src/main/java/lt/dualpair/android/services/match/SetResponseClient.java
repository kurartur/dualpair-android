package lt.dualpair.android.services.match;

import lt.dualpair.android.resource.Response;
import lt.dualpair.android.services.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class SetResponseClient extends BaseClient<Void> {

    private Long matchPartyId;
    private Response response;

    public SetResponseClient(Long matchPartyId, Response response) {
        this.matchPartyId = matchPartyId;
        this.response = response;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(MatchService.class).setResponse(matchPartyId, response.name());
    }
}
