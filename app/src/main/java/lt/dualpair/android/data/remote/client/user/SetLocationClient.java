package lt.dualpair.android.data.remote.client.user;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.CompletableClient;
import lt.dualpair.android.data.remote.resource.Location;
import retrofit2.Retrofit;

public class SetLocationClient extends CompletableClient {

    private Long userId;
    private Location location;

    public SetLocationClient(Long userId, Location location) {
        this.userId = userId;
        this.location = location;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setLocation(userId, location);
    }
}
