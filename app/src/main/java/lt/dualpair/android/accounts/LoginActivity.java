package lt.dualpair.android.accounts;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.BuildConfig;
import lt.dualpair.android.R;
import lt.dualpair.android.TokenProvider;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.remote.client.authentication.RequestTokenClient;
import lt.dualpair.android.data.remote.client.user.GetUserPrincipalClient;
import lt.dualpair.android.data.resource.Token;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.AboutActivity;
import lt.dualpair.android.ui.SplashActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AccountAuthenticatorActivity {

    private static final String TAG = "LoginActivity";

    public static final String ARG_USER_ID = "userId";
    public static final String ARG_USER_NAME = "userName";

    public static final String CLIENT_ID = "dualpairandroid";
    public static final String CLIENT_SERCET = "secret";
    public static final String OAUTH_SCOPE = "trust";
    public static final String GRANT_TYPE = "authorization_code";
    public static final String OAUTH_URL = "/oauth/authorize";
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
        webView.clearCache(true);
        webView.clearHistory();
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
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
        new GetUserPrincipalClient().observable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<User>() {
                    @Override
                    public void onNext(User user) {
                        Account account = new Account(user.getName(), AccountConstants.ACCOUNT_TYPE);
                        Bundle userData = new Bundle();
                        userData.putString(ARG_USER_ID, user.getId().toString());
                        userData.putString(ARG_USER_NAME, user.getName());
                        userData.putString(AccountManager.KEY_AUTHTOKEN, token.getAccessToken());

                        // TODO userData doesnt get overrided when account already exists.
                        accountManager.removeAccount(account, null, null);
                        accountManager.addAccountExplicitly(account, token.getRefreshToken(), userData);
                        accountManager.setAuthToken(account, AccountConstants.ACCOUNT_TYPE, token.getAccessToken());

                        Bundle result = new Bundle();
                        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                        result.putString(AccountManager.KEY_AUTHTOKEN, token.getAccessToken());

                        TokenProvider.getInstance().storeToken(token.getAccessToken());

                        AccountUtils.setAccount(accountManager, account, LoginActivity.this);

                        setAccountAuthenticatorResult(result);

                        startActivity(SplashActivity.createIntent(LoginActivity.this));
                        LoginActivity.this.setResult(Activity.RESULT_OK);
                        finish();
                    }
                });
    }

    private String buildOAuthUrl() {
        return new StringBuilder()
                .append(BuildConfig.SERVER_HOST)
                .append(OAUTH_URL)
                .append("?redirect_uri=").append(REDIRECT_URI)
                .append("&response_type=").append("code")
                .append("&client_id=").append(CLIENT_ID)
                .append("&scope=").append(OAUTH_SCOPE)
                .toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_menu_item:
                startActivity(AboutActivity.createIntent(this));
                break;
        }
        return false;
    }
}
