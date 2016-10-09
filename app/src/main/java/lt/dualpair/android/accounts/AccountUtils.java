package lt.dualpair.android.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.io.IOException;

public class AccountUtils {

    private static final String CURRENT_ACCOUNT_NAME = "CURRENT_ACCOUNT_NAME";

    public static Account getAccount(final Context context) {
        AccountManager am = AccountManager.get(context);
        return getAccount(am, context);
    }

    public static Account getAccount(final AccountManager accountManager, final Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String currentAccountName = preferences.getString(CURRENT_ACCOUNT_NAME, null);
        if (currentAccountName == null) {
            return null;
        }
        Account[] accounts = accountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE);
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].name.equals(currentAccountName)) {
                return accounts[i];
            }
        }
        return null;
    }

    public static void setAccount(final Account account, final Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_ACCOUNT_NAME, account.name);
        editor.commit();
    }

    public static Long getUserId(final Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = AccountUtils.getAccount(accountManager, context);
        return Long.valueOf(accountManager.getUserData(account, LoginActivity.ARG_USER_ID));
    }

    public static Bundle addAccount(final AccountManager accountManager, final Activity activity) {
        try {
            return accountManager.addAccount(AccountConstants.ACCOUNT_TYPE, null, null, null, activity, null, null).getResult();
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
