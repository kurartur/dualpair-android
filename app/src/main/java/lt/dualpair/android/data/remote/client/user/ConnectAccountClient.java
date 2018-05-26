package lt.dualpair.android.data.remote.client.user;


import io.reactivex.Completable;
import io.reactivex.Observable;
import lt.dualpair.android.data.remote.client.BaseClient;
import retrofit2.Retrofit;

public class ConnectAccountClient extends BaseClient<Void> {

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
    protected Observable getApiObserable(Retrofit retrofit) {
        return null;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(UserService.class).connect(providerId, accessToken, expiresIn, scope);
    }
}
