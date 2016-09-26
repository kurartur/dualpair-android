package lt.dualpair.android.data.remote.services.authentication;

import lt.dualpair.android.data.remote.services.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class LogoutClient extends BaseClient<Void> {

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(OAuthService.class).logout();
    }
}
