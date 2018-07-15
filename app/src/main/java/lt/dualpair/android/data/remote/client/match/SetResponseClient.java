package lt.dualpair.android.data.remote.client.match;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.CompletableClient;
import lt.dualpair.android.data.remote.client.user.UserService;
import lt.dualpair.android.data.remote.resource.Response;
import retrofit2.Retrofit;

public class SetResponseClient extends CompletableClient {

    private Long userId;
    private Long toUserId;
    private Response response;

    public SetResponseClient(Long userId, Long toUserId, Response response) {
        this.userId = userId;
        this.toUserId = toUserId;
        this.response = response;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(UserService.class).respond(userId, toUserId, response.name());
    }
}
