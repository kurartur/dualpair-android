package lt.dualpair.android.data.task.user;

import android.content.Context;

import java.util.List;

import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.PhotoRepository;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class SavePhotosTask extends AuthenticatedUserTask<List<Photo>> {

    private List<Photo> photos;

    public SavePhotosTask(String authToken, List<Photo> photos) {
        super(authToken);
        this.photos = photos;
    }

    @Override
    protected Observable<List<Photo>> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<List<Photo>>() {
            @Override
            public void call(Subscriber<? super List<Photo>> subscriber) {
                // TODO call to service
                PhotoRepository photoRepository = new PhotoRepository(DatabaseHelper.getInstance(context).getWritableDatabase());
                List<Photo> oldPhotos = photoRepository.fetch(getUserId(context));
                for (Photo oldPhoto: oldPhotos) {
                    photoRepository.delete(oldPhoto);
                }
                for (Photo photo: photos) {
                    photoRepository.save(photo, getUserId(context));
                }
                subscriber.onNext(photos);
                subscriber.onCompleted();
            }
        });
    }

}
