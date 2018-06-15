package lt.dualpair.android.accounts;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import lt.dualpair.android.data.remote.client.ServiceException;
import lt.dualpair.android.data.remote.client.authentication.RequestTokenClient;
import lt.dualpair.android.data.remote.resource.Token;
import lt.dualpair.android.ui.accounts.LoginActivity;

import static android.accounts.AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private Context context;

    public AccountAuthenticator(Context ctx) {
        super(ctx);
        context = ctx;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, createLoginIntent(response));
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(context);

        String authToken = am.peekAuthToken(account, authTokenType);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                try {
                    Token token = new RequestTokenClient(password, OAuthConstants.CLIENT_ID, OAuthConstants.CLIENT_SERCET).observable().blockingFirst();
                    am.setPassword(account, token.getRefreshToken());
                    authToken = token.getAccessToken();
                    // bundle.putString(KEY_CUSTOM_TOKEN_EXPIRY, somevalue); // TODO Set token expiration date
                } catch (ServiceException se) {
                    // Continue without token
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password or we got error - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our LoginActivity.
        final Intent intent = createLoginIntent(response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AccountConstants.ACCOUNT_TYPE.equals(authTokenType))
            return authTokenType;
        else
            return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, createLoginIntent(response));
        return bundle;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) {
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
