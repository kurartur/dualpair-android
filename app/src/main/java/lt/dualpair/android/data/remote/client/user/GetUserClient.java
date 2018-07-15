package lt.dualpair.android.data.remote.client.user;

import io.reactivex.Observable;
import lt.dualpair.android.data.remote.client.ObservableClient;
import lt.dualpair.android.data.remote.resource.User;
import retrofit2.Retrofit;

public class GetUserClient extends ObservableClient<User> {

    private Long userId;

    public GetUserClient(Long userId) {
        this.userId = userId;
    }

    @Override
    protected Observable<User> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).getUser(userId);
    }
}
