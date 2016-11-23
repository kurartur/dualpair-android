package lt.dualpair.android.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.io.IOException;

public class AccountUtils {

    @Deprecated
    public static Account getAccount(AccountManager am, Activity activity) {
        return getAccount(activity);
    }

    public static Account getAccount(final Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(AccountConstants.ACCOUNT_TYPE);
        if (accounts.length == 0) {
            return null;
        }
        return accounts[0];
    }

    public static Long getUserId(final Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = AccountUtils.getAccount(context);
        if (account == null) {
            return null;
        }
        return Long.valueOf(accountManager.getUserData(account, LoginActivity.ARG_USER_ID));
    }

    @Deprecated
    public static Bundle addAccount(AccountManager am, Activity activity) {
        return addAccount(activity);
    }

    public static Bundle addAccount(final Activity activity) {
        AccountManager am = AccountManager.get(activity);
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

    public static String getAuthToken(final Context context) {
        AccountManager am = AccountManager.get(context);
        try {
            Bundle result = am.getAuthToken(getAccount(context), AccountConstants.ACCOUNT_TYPE, null, null, null, null).getResult();
            return (String)result.get(AccountManager.KEY_AUTHTOKEN);
        } catch (OperationCanceledException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (AuthenticatorException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
