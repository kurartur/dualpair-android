package lt.dualpair.android.data.remote.client;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.user.UserService;
import retrofit2.Retrofit;

public class DisconnectAccountClient extends CompletableClient {

    private String providerId;

    public DisconnectAccountClient(String providerId) {
        this.providerId = providerId;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(UserService.class).disconnect(providerId);
    }
}
