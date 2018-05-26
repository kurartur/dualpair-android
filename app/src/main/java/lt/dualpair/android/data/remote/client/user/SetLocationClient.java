package lt.dualpair.android.data.remote.client.user;

import io.reactivex.Completable;
import io.reactivex.Observable;
import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.Location;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SetLocationClient extends BaseClient<Response<Void>> {

    private Long userId;
    private Location location;

    public SetLocationClient(Long userId, Location location) {
        this.userId = userId;
        this.location = location;
    }

    @Override
    protected Observable<Response<Void>> getApiObserable(Retrofit retrofit) {
        return null;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setLocation(userId, location);
    }
}
