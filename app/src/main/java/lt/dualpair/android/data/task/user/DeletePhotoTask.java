package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.Iterator;

import lt.dualpair.android.data.remote.client.user.DeletePhotoClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class DeletePhotoTask extends AuthenticatedUserTask<User> {

    private Photo photo;

    public DeletePhotoTask(String authToken, Photo photo) {
        super(authToken);
        this.photo = photo;
    }

    @Override
    protected Observable<User> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                UserRepository userRepository = new UserRepository(db);
                User user = userRepository.get(getUserId(context));
                new DeletePhotoClient(user, photo).observable().toBlocking().first();
                Iterator<Photo> iter = user.getPhotos().iterator();
                while (iter.hasNext()) {
                    if (iter.next().getId().equals(photo.getId())) {
                        iter.remove();
                    }
                }
                userRepository.save(user);
                subscriber.onNext(user);
                subscriber.onCompleted();
            }
        });
    }

}
