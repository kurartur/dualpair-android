package lt.dualpair.android.ui.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import lt.dualpair.android.TokenProvider;
import lt.dualpair.android.accounts.AccountConstants;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.remote.client.user.GetUserPrincipalClient;
import lt.dualpair.android.data.resource.Token;
import lt.dualpair.android.data.resource.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TokenRequestSubscriber extends EmptySubscriber<Token> {

    private static final String TAG = "TokenRequestSubsc";

    private LoginActivity loginActivity;
    private AccountManager accountManager;

    public TokenRequestSubscriber(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        accountManager = AccountManager.get(loginActivity);
    }

    @Override
    public void onCompleted() {
        Log.d(TAG, "Token request completed");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "Token request error", e);
    }

    @Override
    public void onNext(final Token token) {
        TokenProvider.getInstance().storeToken(token.getAccessToken());
        new GetUserPrincipalClient().observable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<User>() {
                    @Override
                    public void onNext(User user) {

                        Log.d(TAG, "START endAuth");

                        String accountName = user.getName() + " (" + user.getId() + ")";
                        Account account = new Account(accountName, AccountConstants.ACCOUNT_TYPE);
                        Bundle userData = new Bundle();
                        userData.putString(AccountConstants.ARG_USER_ID, user.getId().toString());
                        userData.putString(AccountConstants.ARG_USER_NAME, user.getName());

                        accountManager.addAccountExplicitly(account, token.getRefreshToken(), userData);
                        accountManager.setAuthToken(account, AccountConstants.ACCOUNT_TYPE, token.getAccessToken());

                        Bundle result = new Bundle();
                        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                        result.putString(AccountManager.KEY_AUTHTOKEN, token.getAccessToken());

                        accountManager.setPassword(account, token.getRefreshToken());

                        TokenProvider.getInstance().storeToken(token.getAccessToken());

                        loginActivity.setAccountAuthenticatorResult(result);
                        loginActivity.setResult(Activity.RESULT_OK);

                        Log.d(TAG, "END endAuth");

                        loginActivity.finish();
                    }
                });
    }
}