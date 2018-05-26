package lt.dualpair.android.ui.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.TokenProvider;
import lt.dualpair.android.accounts.AccountConstants;
import lt.dualpair.android.data.remote.client.user.GetUserPrincipalClient;
import lt.dualpair.android.data.resource.Token;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.utils.ToastUtils;

public class TokenRequestObserver implements Observer<Token> {

    private static final String TAG = "TokenRequestSubsc";

    private LoginActivity loginActivity;
    private AccountManager accountManager;

    private Disposable disposable;

    public TokenRequestObserver(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        accountManager = AccountManager.get(loginActivity);
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "Token request completed");
        disposable.dispose();
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "Token request error", e);
        if (e instanceof SocketTimeoutException) {
            ToastUtils.show(loginActivity, loginActivity.getString(R.string.no_server_connection));
            loginActivity.finish();
        }
        disposable.dispose();
    }

    @Override
    public void onNext(final Token token) {
        TokenProvider.getInstance().storeToken(token.getAccessToken());
        new GetUserPrincipalClient().observable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) {

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

                        disposable.dispose();
                        loginActivity.finish();
                    }
                });
    }
}
