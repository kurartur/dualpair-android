package lt.dualpair.android.data.task;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;

import com.trello.rxlifecycle.ActivityLifecycleProvider;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.remote.client.ServiceException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class AuthenticatedUserTask<Result> extends Task<Result> {

    protected Context context;

    public AuthenticatedUserTask(Context context) {
        this.context = context;
    }

    public void execute(Subscriber<Result> subscriber) {
        Observable.fromCallable(this)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void execute(Subscriber<Result> subscriber, ActivityLifecycleProvider lifecycleProvider) {
        Observable.fromCallable(this)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(lifecycleProvider.<Result>bindToLifecycle())
                .subscribe(subscriber);
    }

    protected Long getUserId() {
        Long userId = AccountUtils.getUserId(context);
        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }
        return userId;
    }

    protected String getAuthToken() {
        AccountManager am = AccountManager.get(context);
        Account account = AccountUtils.getAccount(am);
        if (context instanceof Activity) {
            return AccountUtils.getAuthToken(am, account, (Activity)context);
        } else {
            return AccountUtils.getAuthToken(am, account);
        }
    }

    @Override
    public Result call() throws Exception {
        getAuthToken(); // TODO this is temporarily here to ensure token is set
        try {
            return run();
        } catch (ServiceException e) {
            if (isUnauthorizedException(e)) {
                invalidateToken();
                return run();
            } else {
                throw e;
            }
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ServiceException
                    && isUnauthorizedException((ServiceException)e.getCause())) {
                invalidateToken();
                return run();
            } else {
                throw e;
            }
        }
    }

    private boolean isUnauthorizedException(ServiceException e) {
        return e.getResponse() != null && e.getResponse().code() == 401;
    }

    private void invalidateToken() {
        AccountManager manager = AccountManager.get(context);
        Account account = AccountUtils.getAccount(manager);
        manager.invalidateAuthToken(account.type, manager.getUserData(account, AccountManager.KEY_AUTHTOKEN));
    }

    protected abstract Result run() throws Exception;
}
