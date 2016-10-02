package lt.dualpair.android.data.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.remote.task.user.GetUserPrincipalTask;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.User;
import rx.Subscriber;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class UserDataManager extends DataManager {

    private static final String TAG = "UserDataManager";

    private static Subject<User, User> userSubject = PublishSubject.create();

    private UserRepository userRepository;

    public UserDataManager(Context context) {
        super(context);
        SQLiteDatabase db = DbHelper.forCurrentUser(context).getWritableDatabase();
        userRepository = new UserRepository(db);
    }

    public Subscription getUser(Subscriber<User> subscriber) {
        Subscription subscription = userSubject.subscribe(subscriber);
        User user = userRepository.get(AccountUtils.getUserId(context));
        if (user != null) {
            userSubject.onNext(user);
        } else {
            enqueueTask(new QueuedTask<>("getUser", new GetUserPrincipalTask(context), new EmptySubscriber<User>() {
                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "Error while loading user", e);
                }

                @Override
                public void onNext(User u) {
                    userRepository.save(u);
                    userSubject.onNext(u);
                }
            }));
        }
        return subscription;
    }

}
