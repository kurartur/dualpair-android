package lt.dualpair.android.data.task;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountsException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;

import com.trello.rxlifecycle.ActivityLifecycleProvider;

import java.io.IOException;

import lt.dualpair.android.accounts.AccountConstants;
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

    @Override
    public Result call() throws Exception {
        Account account = AccountUtils.getAccount(context);

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
        AccountManager manager = AccountManager.get(context);
        try {
            manager.invalidateAuthToken(account.type, manager.getUserData(account, AccountManager.KEY_AUTHTOKEN));
            Bundle result = manager.updateCredentials(account, AccountConstants.ACCOUNT_TYPE, null, null, null, null).getResult();
            return false;
        } catch (OperationCanceledException oce) {
            return false;
        } catch (AccountsException ae) {
            return false;
        } catch (IOException ioe) {
            return false;
        }
    }

    protected abstract Result run() throws Exception;
}
