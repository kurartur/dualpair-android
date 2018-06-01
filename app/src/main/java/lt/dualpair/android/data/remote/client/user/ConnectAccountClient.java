package lt.dualpair.android.data.remote.client.user;


import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.CompletableClient;
import retrofit2.Retrofit;

public class ConnectAccountClient extends CompletableClient {

    private String providerId;
    private String accessToken;
    private Long expiresIn;
    private String scope;

    public ConnectAccountClient(String providerId, String accessToken, Long expiresIn, String scope) {
        this.providerId = providerId;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.scope = scope;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(UserService.class).connect(providerId, accessToken, expiresIn, scope);
    }
}
