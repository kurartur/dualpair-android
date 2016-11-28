package lt.dualpair.android.data.task;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.remote.client.ServiceException;
import rx.Observable;
import rx.functions.Func1;

public abstract class AuthenticatedUserTask<Result> extends Task<Result> {

    private String token;

    public AuthenticatedUserTask(String token) {
        this.token = token;
    }

    @Override
    public Observable<Result> execute(final Context context) {
        return run(context)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Result>>() {
                    @Override
                    public Observable<? extends Result> call(Throwable throwable) {
                        if (isUnauthorizedException(throwable)) {
                            invalidateToken(context);
                            getAuthToken(context);
                            return run(context);
                        }
                        return Observable.error(throwable);
                    }
                });
    }

    protected String getToken() {
        return token;
    }

    protected Long getUserId(Context context) {
        Long userId = AccountUtils.getUserId(context);
        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }
        return userId;
    }

    protected String getAuthToken(Context context) {
        AccountManager am = AccountManager.get(context);
        Account account = AccountUtils.getAccount(am);
        if (context instanceof Activity) {
            return AccountUtils.getAuthToken(am, account, (Activity)context);
        } else {
            return AccountUtils.getAuthToken(am, account);
        }
    }

    private boolean isUnauthorizedException(Throwable throwable) {
        ServiceException se = null;
        if (throwable instanceof ServiceException) {
            se = (ServiceException) throwable;
        } else if (throwable.getCause() instanceof ServiceException) {
            se = (ServiceException) throwable.getCause();
        }
        if (se != null) {
            return se.getResponse() != null && se.getResponse().code() == 401;
        }
        return false;
    }

    private void invalidateToken(Context context) {
        AccountManager manager = AccountManager.get(context);
        Account account = AccountUtils.getAccount(manager);
        manager.invalidateAuthToken(account.type, getAuthToken(context));
    }

    protected abstract Observable<Result> run(Context context);
}
