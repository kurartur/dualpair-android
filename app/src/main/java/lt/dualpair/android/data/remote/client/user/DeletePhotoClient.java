package lt.dualpair.android.data.remote.client.user;


import lt.dualpair.android.data.remote.client.BaseClient;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.User;
import retrofit2.Retrofit;
import rx.Observable;

public class DeletePhotoClient extends BaseClient<Void> {

    private User user;
    private Photo photo;

    public DeletePhotoClient(User user, Photo photo) {
        this.user = user;
        this.photo = photo;
    }

    @Override
    protected Observable<Void> getApiObserable(Retrofit retrofit) {
        return retrofit.create(UserService.class).deletePhoto(user.getId(), photo.getId());
    }
}
