package lt.dualpair.android.data.remote.client.authentication;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.CompletableClient;
import retrofit2.Retrofit;

public class LogoutClient extends CompletableClient {

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(OAuthService.class).logout();
    }
}
