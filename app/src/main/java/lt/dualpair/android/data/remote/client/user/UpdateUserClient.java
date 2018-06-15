package lt.dualpair.android.data.remote.client.user;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.CompletableClient;
import lt.dualpair.android.data.remote.resource.User;
import retrofit2.Retrofit;

public class UpdateUserClient extends CompletableClient {

    private User user;

    public UpdateUserClient(User user) {
        this.user = user;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("dateOfBirth", user.getDateOfBirth());
        data.put("description", user.getDescription());
        data.put("relationshipStatus", user.getRelationshipStatus());
        data.put("purposesOfBeing", user.getPurposesOfBeing());
        return retrofit.create(UserService.class).updateUser(user.getId(), data);
    }
}
