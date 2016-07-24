package com.artur.dualpair.android.accounts;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.artur.dualpair.android.R;
import com.artur.dualpair.android.TokenProvider;
import com.artur.dualpair.android.dto.Token;
import com.artur.dualpair.android.dto.User;
import com.artur.dualpair.android.rx.EmptySubscriber;
import com.artur.dualpair.android.services.authentication.RequestTokenClient;
import com.artur.dualpair.android.services.user.GetUserPrincipal;
import com.artur.dualpair.android.ui.SplashActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AccountAuthenticatorActivity {

    private static final String TAG = "LoginActivity";

    public static final String ARG_USER_NAME = "userName";

    public static final String CLIENT_ID = "dualpairandroid";
    public static final String CLIENT_SERCET = "secret";
    public static final String OAUTH_SCOPE = "trust";
    public static final String GRANT_TYPE = "authorization_code";
    public static final String OAUTH_URL = "http://10.0.2.2:8080/oauth/authorize";
    public static final String REDIRECT_URI = "http://localhost";

    @Bind(R.id.login_webview)
    WebView webView;

    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        ButterKnife.bind(this);
        accountManager = AccountManager.get(this);
        prepareWebView();
    }

    private void prepareWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(buildOAuthUrl());
        webView.setWebViewClient(new WebViewClient() {

            private boolean isCodePage(String url) {
                return url.contains("?code=") && url.contains("localhost");
            }

            private boolean isErrorPage(String url) {
                return url.contains("error") && url.contains("localhost");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isCodePage(url)) {
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter("code");
                    getToken(authCode);
                    return true;
                } else if (isErrorPage(url)) {
                    // TODO display error
                    LoginActivity.this.setResult(Activity.RESULT_CANCELED);
                    return true;
                }
                return false;
            }

        });
    }

    private void getToken(String code) {
        new RequestTokenClient(code, CLIENT_ID, CLIENT_SERCET, REDIRECT_URI, GRANT_TYPE).observable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<Token>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Did not get token", e);
                    }

                    @Override
                    public void onNext(Token token) {
                        TokenProvider.getInstance().storeToken(token.getAccessToken());
                        endAuth(token);
                    }
                });
    }

    private void endAuth(final Token token) {
        new GetUserPrincipal().observable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<User>() {
                    @Override
                    public void onNext(User user) {
                        Account account = new Account(user.getName(), AccountConstants.ACCOUNT_TYPE);
                        Bundle userData = new Bundle();
                        userData.putString(ARG_USER_NAME, user.getName());
                        userData.putString(AccountManager.KEY_AUTHTOKEN, token.getAccessToken());

                        accountManager.addAccountExplicitly(account, token.getRefreshToken(), userData);
                        accountManager.setAuthToken(account, AccountConstants.ACCOUNT_TYPE, token.getAccessToken());

                        Bundle result = new Bundle();
                        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                        result.putString(AccountManager.KEY_AUTHTOKEN, token.getAccessToken());

                        TokenProvider.getInstance().storeToken(token.getAccessToken());

                        setAccountAuthenticatorResult(result);

                        startActivity(SplashActivity.createIntent(LoginActivity.this));
                        LoginActivity.this.setResult(Activity.RESULT_OK);
                        finish();
                    }
                });
    }

    private String buildOAuthUrl() {
        return new StringBuilder()
                .append(OAUTH_URL)
                .append("?redirect_uri=").append(REDIRECT_URI)
                .append("&response_type=").append("code")
                .append("&client_id=").append(CLIENT_ID)
                .append("&scope=").append(OAUTH_SCOPE)
                .toString();
    }
}
