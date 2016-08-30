package lt.dualpair.android.services.user;

import lt.dualpair.android.resource.User;
import lt.dualpair.android.services.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class GetUserPrincipalClient extends BaseClient<User> {

    @Override
    protected Observable<User> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).getUser();
    }
}
