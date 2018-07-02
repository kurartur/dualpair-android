package lt.dualpair.android.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.io.IOException;

public class AccountUtils {

    private static final String TAG = "AccountUtils";

    public static Account getAccount(final AccountManager am) {
        Account[] accounts = am.getAccountsByType(AccountConstants.ACCOUNT_TYPE);
        if (accounts.length == 0) {
            return null;
        }
        return accounts[0];
    }

    public static String peekAuthToken(final AccountManager am, final Account account) {
        return account != null ? am.peekAuthToken(account, AccountConstants.ACCOUNT_TYPE) : null;
    }

    public static void invalidateCurrentAuthToken(final AccountManager am) {
        Account account = getAccount(am);
        if (account != null) {
            am.invalidateAuthToken(account.type, peekAuthToken(am, account));
        }
    }

    public static Long getUserId(final Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = AccountUtils.getAccount(accountManager);
        if (account == null) {
            return null;
        }
        return Long.valueOf(accountManager.getUserData(account, AccountConstants.ARG_USER_ID));
    }

    public static Bundle addAccount(final AccountManager am, final Activity activity) throws OperationCanceledException,
            AuthenticatorException, IOException {
        return am.addAccount(AccountConstants.ACCOUNT_TYPE, null, null, null, activity, null, null).getResult();
    }

    public static AccountManagerFuture<Bundle> getAuthToken(final AccountManager am, final Account account, AccountManagerCallback<Bundle> callback) {
        return am.getAuthToken(account, AccountConstants.ACCOUNT_TYPE, null, false, callback, null);
    }

    public static void removeAccount(AccountManager am) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            am.removeAccount(getAccount(am), null, null);
        } else {
            am.removeAccount(getAccount(am), null, null, null);
        }
    }
}
