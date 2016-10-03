package lt.dualpair.android.data.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Set;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.remote.task.user.GetUserPrincipalTask;
import lt.dualpair.android.data.remote.task.user.SetUserSociotypesTask;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import rx.Observable;
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

    public Observable<User> getUser() {
        User user = userRepository.get(AccountUtils.getUserId(context));
        PublishSubject<User> subject = PublishSubject.create();
        userSubject.subscribe(subject);
        if (user != null) {
            subject.onNext(user);
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
        return subject.asObservable();
    }

    public Observable<Void> setSociotypes(final Set<Sociotype> sociotypes) {
        final PublishSubject<Void> subject = PublishSubject.create();
        enqueueTask(new QueuedTask<>("setSociotypes", new SetUserSociotypesTask(context, sociotypes), new EmptySubscriber<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to set sociotypes", e);
                subject.onError(e);
            }

            @Override
            public void onNext(Void v) {
                User user = userRepository.get(AccountUtils.getUserId(context));
                user.setSociotypes(sociotypes);
                userRepository.save(user);
                subject.onNext(null);
                userSubject.onNext(user);
            }
        }));
        return subject.asObservable();
    }

}
