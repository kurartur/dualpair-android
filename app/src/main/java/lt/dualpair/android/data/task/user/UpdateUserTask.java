package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.remote.client.user.UpdateUserClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;
import rx.Subscriber;


public class UpdateUserTask extends AuthenticatedUserTask<User> {

    private User user;

    public UpdateUserTask(String authToken, User user) {
        super(authToken);
        this.user = user;
    }

    @Override
    protected Observable<User> run(final Context context) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(Subscriber<? super User> subscriber) {
                SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
                UserRepository userRepository = new UserRepository(db);
                new UpdateUserClient(user).observable().toBlocking().first();
                userRepository.save(user);
                subscriber.onNext(user);
                subscriber.onCompleted();
            }
        });
    }
}
