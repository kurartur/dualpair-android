package lt.dualpair.android.data.manager;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.ResultReceiver;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.repo.DbHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.service.DataService;
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
            Intent intent = new Intent(context, DataService.class);
            intent.putExtra("RECEIVER", new ResultReceiver(null) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    userSubject.onNext((User)resultData.getSerializable("USER"));
                }
            });
            context.startService(intent);
        }
        return subscription;
    }

}
