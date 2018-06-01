package lt.dualpair.android.data.remote.client.user;

import io.reactivex.Observable;
import lt.dualpair.android.data.remote.client.ObservableClient;
import lt.dualpair.android.data.resource.User;
import retrofit2.Retrofit;

public class GetUserPrincipalClient extends ObservableClient<User> {

    private String authToken;

    public GetUserPrincipalClient() {}

    // Provide auth token when creating account, because token is not stored yet
    public GetUserPrincipalClient(String authToken) {
        this.authToken = authToken;
    }

    @Override
    protected Observable<User> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).getUser();
    }

    @Override
    protected String getAuthToken() {
        if (authToken != null) {
            return authToken;
        }
        return super.getAuthToken();
    }
}
