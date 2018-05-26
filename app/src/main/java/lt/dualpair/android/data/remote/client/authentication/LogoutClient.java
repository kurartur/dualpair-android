package lt.dualpair.android.data.remote.client.authentication;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.BaseClient;
import retrofit2.Retrofit;

public class LogoutClient extends BaseClient<Void> {

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(OAuthService.class).logout();
    }
}
