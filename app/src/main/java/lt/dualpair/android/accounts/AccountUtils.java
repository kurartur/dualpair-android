package lt.dualpair.android.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

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

    public static Long getUserId(final Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = AccountUtils.getAccount(accountManager);
        if (account == null) {
            return null;
        }
        return Long.valueOf(accountManager.getUserData(account, AccountConstants.ARG_USER_ID));
    }

    public static Bundle addAccount(final AccountManager am, final Activity activity) {
        try {
            return am.addAccount(AccountConstants.ACCOUNT_TYPE, null, null, null, activity, null, null).getResult();
        } catch (AuthenticatorException ae) {
            throw new RuntimeException(ae);
        } catch (OperationCanceledException oce) {
            activity.finish();
            throw new RuntimeException(oce);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Get authentication token.
     * To be called from service.
     * @param am - Account manager
     * @param account - Account
     * @return Authentication token
     */
    public static String getAuthToken(final AccountManager am, final Account account) {
        try {
            Bundle result = am.getAuthToken(account, AccountConstants.ACCOUNT_TYPE, null, true, null, null).getResult();
            return (String)result.get(AccountManager.KEY_AUTHTOKEN);
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            Log.e(TAG, "Unable to get token in service", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Get authentication token.
     * To be called from foreground.
     * @param am- Account manager
     * @param account - Account
     * @param activity - Activity from which login activity should be called
     * @return
     */
    public static String getAuthToken(final AccountManager am, final Account account, final Activity activity) {
        try {
            Bundle result = am.getAuthToken(account, AccountConstants.ACCOUNT_TYPE, null, activity, null, null).getResult();
            return (String)result.get(AccountManager.KEY_AUTHTOKEN);
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            Log.e(TAG, "Unable to get token", e);
            throw new RuntimeException(e);
        }
    }

    public static void removeAccount(AccountManager am) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            am.removeAccount(getAccount(am), null, null);
        } else {
            am.removeAccount(getAccount(am), null, null, null);
        }
    }
}
