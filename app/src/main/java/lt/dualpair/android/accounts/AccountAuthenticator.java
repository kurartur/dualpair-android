package lt.dualpair.android.accounts;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import static android.accounts.AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private Context context;

    public AccountAuthenticator(Context ctx) {
        super(ctx);
        context = ctx;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        final Bundle bundle = new Bundle();

        if (!AccountConstants.ACCOUNT_TYPE.equals(authTokenType)) {
            return bundle;
        }

        AccountManager accountManager = AccountManager.get(context);
        String password = accountManager.getPassword(account);

        if (TextUtils.isEmpty(password)) {
            bundle.putParcelable(AccountManager.KEY_INTENT, createLoginIntent(response));
            return bundle;
        } else {
            // refresh token
            return bundle;
        }
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AccountConstants.ACCOUNT_TYPE.equals(authTokenType))
            return authTokenType;
        else
            return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        // TODO try refreshing token
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    private Intent createLoginIntent(final AccountAuthenticatorResponse response) {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        return intent;
    }
}
