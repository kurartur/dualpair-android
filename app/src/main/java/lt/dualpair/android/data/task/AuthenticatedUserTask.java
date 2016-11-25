package lt.dualpair.android.data.task;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.remote.client.ServiceException;
import rx.Observable;

public abstract class AuthenticatedUserTask<Result> extends Task<Result> {

    private String token;

    public AuthenticatedUserTask(String token) {
        this.token = token;
    }

    @Override
    public Observable<Result> execute(Context context) {
        getAuthToken(context); // TODO this is temporarily here to ensure token is set
        try {
            return run(context); // TODO doesn't throw these exception, instead attach doOnException or something
        } catch (ServiceException e) {
            if (isUnauthorizedException(e)) {
                invalidateToken(context);
                return run(context);
            } else {
                throw e;
            }
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ServiceException
                    && isUnauthorizedException((ServiceException)e.getCause())) {
                invalidateToken(context);
                return run(context);
            } else {
                throw e;
            }
        }
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

    private boolean isUnauthorizedException(ServiceException e) {
        return e.getResponse() != null && e.getResponse().code() == 401;
    }

    private void invalidateToken(Context context) {
        AccountManager manager = AccountManager.get(context);
        Account account = AccountUtils.getAccount(manager);
        manager.invalidateAuthToken(account.type, manager.getUserData(account, AccountManager.KEY_AUTHTOKEN));
    }

    protected abstract Observable<Result> run(Context context);
}
