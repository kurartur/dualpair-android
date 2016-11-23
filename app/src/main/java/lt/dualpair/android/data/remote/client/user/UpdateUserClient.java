package lt.dualpair.android.data.remote.client.user;

import java.util.HashMap;
import java.util.Map;

import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.User;
import retrofit2.Retrofit;
import rx.Observable;

public class UpdateUserClient extends BaseClient<Void> {

    private User user;

    public UpdateUserClient(User user) {
        this.user = user;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        Map<String, Object> data = new HashMap<>();
        data.put("dateOfBirth", user.getDateOfBirth());
        data.put("description", user.getDescription());
        return retrofit.create(UserService.class).updateUser(user.getId(), data);
    }
}
