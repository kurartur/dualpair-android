package lt.dualpair.android.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountsException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;

import com.trello.rxlifecycle.ActivityLifecycleProvider;

import java.io.IOException;
import java.util.concurrent.Callable;

import lt.dualpair.android.services.ServiceException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class AuthenticatedUserTask<Result> implements Callable<Result> {

    protected Activity activity;

    public AuthenticatedUserTask(Activity activity) {
        this.activity = activity;
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
        AccountManager accountManager = AccountManager.get(activity);
        Account account = AccountUtils.getAccount(accountManager, activity);
        return Long.valueOf(accountManager.getUserData(account, LoginActivity.ARG_USER_ID));
    }

    @Override
    public Result call() throws Exception {
        AccountManager accountManager = AccountManager.get(activity);
        Account account = AccountUtils.getAccount(accountManager, activity);

        try {
            return run();
        } catch (ServiceException e) {
            if (isUnauthorizedException(e) && handleUnauthorizedException(account, e))
                return run();
            else
                throw e;
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ServiceException
                    && isUnauthorizedException((ServiceException)e.getCause())
                    && handleUnauthorizedException(account, (ServiceException) e.getCause()))
                return run();
            else
                throw e;
        }
    }

    private boolean isUnauthorizedException(ServiceException e) {
        return e.getResponse() != null && e.getResponse().code() == 401;
    }

    private boolean handleUnauthorizedException(Account account, ServiceException e) {
        AccountManager manager = AccountManager.get(activity);
        try {
            manager.invalidateAuthToken(account.type, manager.getUserData(account, AccountManager.KEY_AUTHTOKEN));
            Bundle result = manager.updateCredentials(account, AccountConstants.ACCOUNT_TYPE, null, activity, null, null).getResult();
            return false;
        } catch (OperationCanceledException oce) {
            activity.finish();
            return false;
        } catch (AccountsException ae) {
            return false;
        } catch (IOException ioe) {
            return false;
        }
    }

    protected abstract Result run() throws Exception;
}
