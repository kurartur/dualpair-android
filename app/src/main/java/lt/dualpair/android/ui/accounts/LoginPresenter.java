package lt.dualpair.android.ui.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import lt.dualpair.android.BuildConfig;
import lt.dualpair.android.TokenProvider;
import lt.dualpair.android.accounts.AccountConstants;
import lt.dualpair.android.accounts.OAuthConstants;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.remote.client.authentication.RequestTokenClient;
import lt.dualpair.android.data.remote.client.user.GetUserPrincipalClient;
import lt.dualpair.android.data.resource.Token;
import lt.dualpair.android.data.resource.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static lt.dualpair.android.accounts.AccountConstants.ARG_USER_ID;
import static lt.dualpair.android.accounts.AccountConstants.ARG_USER_NAME;
import static lt.dualpair.android.accounts.OAuthConstants.REDIRECT_URI;

public class LoginPresenter {

    private static final String TAG = "LoginPresenter";

    private LoginActivity loginActivity;
    private AccountManager accountManager;

    private boolean isError = false;

    public LoginPresenter(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        this.accountManager = AccountManager.get(loginActivity);
    }

    public void prepareWebView(WebView webView) {
        //webView.clearCache(true);
        //webView.clearHistory();
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(buildOAuthUrl());
        webView.setWebViewClient(new WebViewClient() {

            private boolean isCodePage(String url) {
                return url.contains("?code=") && url.contains(REDIRECT_URI);
            }

            private boolean isErrorPage(String url) {
                return url.contains("error") && url.contains(REDIRECT_URI);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "onPageStarted " + url);
                if (!isError) {
                    loginActivity.showLoading();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "onPageFinished " + url);
                if (!isError) {
                    loginActivity.showWebView();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d(TAG, "onReceivedError deprecated " + errorCode + ", " + description + ", " + failingUrl);
                isError = true;
                handleError(errorCode, description);
                view.stopLoading();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.d(TAG, "onReceivedError" + request + ", " + error);
                isError = true;
                handleError(error.getErrorCode(), error.getDescription().toString());
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.d(TAG, "onReceivedHttpError " + request + ", " + errorResponse);
                isError = true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isCodePage(url)) {
                    Log.d(TAG, "shouldOverrideUrlLoading isCodePage " + url);
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter("code");
                    getToken(authCode);
                    return true;
                } else if (isErrorPage(url)) {
                    Log.d(TAG, "shouldOverrideUrlLoading isErrorPage " + url);
                    // TODO display error
                    loginActivity.setResult(Activity.RESULT_CANCELED);
                    return true;
                }
                return false;
            }

            private void handleError(int code, String description) {
                switch (code) {
                    case ERROR_TIMEOUT:
                        loginActivity.showConnectionTimeoutError(description);
                        break;
                    default:
                        loginActivity.showError(description);
                        break;
                }
            }

        });
    }

    private void getToken(String code) {
        new RequestTokenClient(code, OAuthConstants.CLIENT_ID, OAuthConstants.CLIENT_SERCET, REDIRECT_URI).observable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<Token>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Did not get token", e);
                        loginActivity.showError("Error while logging in");
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

                        Log.d(TAG, "START endAuth");

                        String accountName = user.getName() + " (" + user.getId() + ")";
                        Account account = new Account(accountName, AccountConstants.ACCOUNT_TYPE);
                        Bundle userData = new Bundle();
                        userData.putString(ARG_USER_ID, user.getId().toString());
                        userData.putString(ARG_USER_NAME, user.getName());

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

    public void retry(WebView webView) {
        isError = false;
        webView.loadUrl(buildOAuthUrl());
    }

    private String buildOAuthUrl() {
        return new StringBuilder()
                .append(BuildConfig.SERVER_HOST)
                .append(OAuthConstants.OAUTH_URL)
                .append("?redirect_uri=").append(REDIRECT_URI)
                .append("&response_type=").append("code")
                .append("&client_id=").append(OAuthConstants.CLIENT_ID)
                .append("&scope=").append(OAuthConstants.OAUTH_SCOPE)
                .toString();
    }


}
