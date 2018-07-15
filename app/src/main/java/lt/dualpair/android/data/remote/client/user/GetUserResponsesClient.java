package lt.dualpair.android.data.remote.client.user;

import java.util.Date;

import io.reactivex.Observable;
import lt.dualpair.android.data.remote.client.ObservableClient;
import lt.dualpair.android.data.remote.resource.ResourceCollection;
import lt.dualpair.android.data.remote.resource.UserResponse;
import retrofit2.Retrofit;

public class GetUserResponsesClient extends ObservableClient<ResourceCollection<UserResponse>> {

    private Long userId;

    public GetUserResponsesClient(Long userId) {
        this.userId = userId;
    }

    @Override
    protected Observable<ResourceCollection<UserResponse>> getApiObserable(Retrofit retrofit) {
        UserService userService = retrofit.create(UserService.class);
        return userService.getResponses(userId, new Date().getTime()); // TODO timezone
    }
}
