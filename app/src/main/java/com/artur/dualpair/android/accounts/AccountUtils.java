package com.artur.dualpair.android.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;

import java.io.IOException;

public class AccountUtils {

    public static Account getAccount(final AccountManager accountManager, final Activity activity) {
        Account[] accounts;
        try {
            while ((accounts = accountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE)).length == 0) {
                accountManager.addAccount(AccountConstants.ACCOUNT_TYPE, null, null, null, activity, null, null).getResult();
            }
        } catch (AuthenticatorException ae) {
            throw new RuntimeException(ae);
        } catch (OperationCanceledException oce) {
            activity.finish();
            throw new RuntimeException(oce);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return accounts[0];
    }

}
