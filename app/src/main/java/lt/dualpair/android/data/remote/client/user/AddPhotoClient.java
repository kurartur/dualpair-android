package lt.dualpair.android.data.remote.client.user;

import io.reactivex.Observable;
import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.Photo;
import retrofit2.Retrofit;

public class AddPhotoClient extends BaseClient<Photo> {

    private Long userId;
    private Photo photo;

    public AddPhotoClient(Long userId, Photo photo) {
        this.userId = userId;
        this.photo = photo;
    }

    @Override
    protected Observable<Photo> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).addPhoto(userId, photo);
    }
}
