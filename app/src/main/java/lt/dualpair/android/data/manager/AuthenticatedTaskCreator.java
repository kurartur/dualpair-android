package lt.dualpair.android.data.manager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.task.Task;
import rx.Observable;
import rx.Subscriber;

public abstract class AuthenticatedTaskCreator<T> implements TaskCreator<T> {

    protected abstract Task<T> doCreateTask(String authToken);

    @Override
    public Observable<Task<T>> createTask(final Context context) {
        return Observable.create(new Observable.OnSubscribe<Task<T>>() {
            @Override
            public void call(Subscriber<? super Task<T>> subscriber) {
                AccountManager am = AccountManager.get(context);
                Account account = AccountUtils.getAccount(am);
                String token;
                if (context instanceof Activity) {
                    token = AccountUtils.getAuthToken(am, account, (Activity)context);
                } else {
                    token = AccountUtils.getAuthToken(am, account);
                }
                subscriber.onNext(doCreateTask(token));
                subscriber.onCompleted();
            }
        });
    }
}
