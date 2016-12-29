package lt.dualpair.android.data.remote.client.user;

import java.util.List;

import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.Photo;
import retrofit2.Retrofit;
import rx.Observable;

public class SetPhotosClient extends BaseClient<Void> {

    private Long userId;
    private List<Photo> photos;

    public SetPhotosClient(Long userId, List<Photo> photos) {
        this.userId = userId;
        this.photos = photos;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setPhotos(userId, photos);
    }
}
