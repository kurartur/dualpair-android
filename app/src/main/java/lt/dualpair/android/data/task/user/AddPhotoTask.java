package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.remote.client.user.AddPhotoClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;

public class AddPhotoTask extends AuthenticatedUserTask<User> {

    private Photo photo;
    private UserRepository userRepository;

    public AddPhotoTask(Photo photo) {
        this.photo = photo;
    }

    @Override
    protected Observable<User> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                userRepository = new UserRepository(db);

                Photo photo = new AddPhotoClient(getUserId(context), AddPhotoTask.this.photo).observable().toBlocking().first();
                User user = userRepository.get(getUserId(context));
                user.getPhotos().add(photo);
                userRepository.save(user);
                subscriber.onNext(user);
                subscriber.onCompleted();
            }
        });
    }

}
