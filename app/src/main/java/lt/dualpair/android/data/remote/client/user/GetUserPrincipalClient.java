package lt.dualpair.android.data.remote.client.user;

import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.User;
import retrofit2.Retrofit;
import rx.Observable;

public class GetUserPrincipalClient extends BaseClient<User> {

    @Override
    protected Observable<User> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).getUser();
    }
}
