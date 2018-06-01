package lt.dualpair.android.ui.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.accounts.AccountConstants;
import lt.dualpair.android.data.remote.client.TokenProvider;
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
        new GetUserPrincipalClient(token.getAccessToken()).observable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) {

                        final String accountName = user.getName();
                        final String accountType = AccountConstants.ACCOUNT_TYPE;
                        final String authToken = token.getAccessToken();
                        final String password = token.getRefreshToken();

                        final Intent res = new Intent();
                        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
                        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                        res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);

                        Account account = new Account(accountName, accountType);

                        Bundle userData = new Bundle();
                        userData.putString(AccountConstants.ARG_USER_ID, user.getId().toString());
                        userData.putString(AccountConstants.ARG_USER_NAME, user.getName());

                        accountManager.addAccountExplicitly(account, password, userData);
                        accountManager.setAuthToken(account, accountType, authToken);

                        loginActivity.setAccountAuthenticatorResult(res.getExtras());
                        loginActivity.setResult(Activity.RESULT_OK, res);
                        loginActivity.finish();

                        TokenProvider.getInstance().publishToken();

                        disposable.dispose();
                    }
                });
    }
}
