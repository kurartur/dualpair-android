package lt.dualpair.android.ui.splash;

import android.content.Context;

import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.User;
import rx.Observable;
import rx.Subscriber;

public class DateOfBirthChecker {

    private Context context;

    public DateOfBirthChecker(Context context) {
        this.context = context;
    }

    public Observable<Boolean> userHasDateOfBirth() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                User user = fetchUser();
                if (user.getDateOfBirth() == null) {
                    subscriber.onNext(false);
                } else {
                    subscriber.onNext(true);
                }
                subscriber.onCompleted();
            }
        });
    }

    private User fetchUser() {
        return new UserDataManager(context).getUser(true).toBlocking().first();
    }

}
