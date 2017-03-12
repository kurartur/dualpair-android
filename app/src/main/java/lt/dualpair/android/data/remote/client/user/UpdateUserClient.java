package lt.dualpair.android.data.remote.client.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.PurposeOfBeing;
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
        data.put("name", user.getName());
        data.put("dateOfBirth", user.getDateOfBirth());
        data.put("description", user.getDescription());
        data.put("relationshipStatus", user.getRelationshipStatus().getCode());
        data.put("purposesOfBeing", purposesToCodes(user.getPurposesOfBeing()));
        return retrofit.create(UserService.class).updateUser(user.getId(), data);
    }

    private Set<String> purposesToCodes(Set<PurposeOfBeing> purposesOfBeing) {
        Set<String> codes = new HashSet<>();
        for (PurposeOfBeing purposeOfBeing : purposesOfBeing) {
            codes.add(purposeOfBeing.getCode());
        }
        return codes;
    }
}
