package lt.dualpair.android.data.remote.client.user;

import java.util.List;

import io.reactivex.Completable;
import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.Photo;
import retrofit2.Retrofit;

public class SetPhotosClient extends BaseClient<Void> {

    private Long userId;
    private List<Photo> photos;

    public SetPhotosClient(Long userId, List<Photo> photos) {
        this.userId = userId;
        this.photos = photos;
    }

    @Override
    protected Completable getApiCompletable(Retrofit retrofit) {
        return retrofit.create(UserService.class).setPhotos(userId, photos);
    }
}
