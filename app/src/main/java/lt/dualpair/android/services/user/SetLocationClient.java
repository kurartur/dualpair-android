package lt.dualpair.android.services.user;

import lt.dualpair.android.resource.Location;
import lt.dualpair.android.services.BaseClient;
import retrofit2.Retrofit;
import rx.Observable;

public class SetLocationClient extends BaseClient<Void> {

    private Long userId;
    private Location location;

    public SetLocationClient(Long userId, Location location) {
        this.userId = userId;
        this.location = location;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setLocation(userId, location);
    }
}
