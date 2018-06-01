package lt.dualpair.android.data.remote.client.match;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.CompletableClient;
import lt.dualpair.android.data.remote.client.user.UserService;
import lt.dualpair.android.data.resource.Response;
import retrofit2.Retrofit;

public class SetResponseClient extends CompletableClient {

    private Long matchPartyId;
    private Response response;

    public SetResponseClient(Long matchPartyId, Response response) {
        this.matchPartyId = matchPartyId;
        this.response = response;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setResponse(matchPartyId, response.name());
    }
}
