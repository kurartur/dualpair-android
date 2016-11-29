package lt.dualpair.android.ui.accounts;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;

import lt.dualpair.android.TokenProvider;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static lt.dualpair.android.BuildConfig.SERVER_HOST;

public class LinkAccountPresenter {

    private static final String TAG = "LinkAccountPres";

    private LinkAccountFragment linkAccountFragment;
    private AccountType accountType;
    private LinkAccountCallback linkAccountCallback;

    private boolean isError = false;

    public LinkAccountPresenter(LinkAccountFragment linkAccountFragment, AccountType accountType, LinkAccountCallback linkAccountCallback) {
        this.linkAccountFragment = linkAccountFragment;
        this.accountType = accountType;
        this.linkAccountCallback = linkAccountCallback;
    }

    public void setupWebView(WebView webView) {
        webView.clearCache(true);
        webView.clearHistory();
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            private boolean isInitial(String url) {
                return url.contains(SERVER_HOST.replace("http://", "")) && !url.contains("code=");
            }

            private boolean isCode(String url) {
                return url.contains(SERVER_HOST.replace("http://", "")) && url.contains("code=");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "onPageStarted " + url);
                if (!isError) {
                    linkAccountFragment.showLoading();
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
            public void onPageFinished(final WebView view, String url) {
                if (isInitial(url)) {
                    OkHttpClient client = new OkHttpClient.Builder().followRedirects(false).build();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("scope", getScope(accountType))
                            .build();
                    Request okRequest = new Request.Builder()
                            .addHeader("Authorization", "Bearer " + TokenProvider.getInstance().getToken())
                            .url(getConnectUrl(accountType))
                            .post(requestBody)
                            .build();
                    client.newCall(okRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "Failed to link account " + accountType, e);
                            linkAccountCallback.onFailure();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String location = response.header("location");
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl(location);
                                }
                            });
                        }
                    });
                } else if (isCode(url)) {
                    OkHttpClient client = new OkHttpClient();
                    Request okRequest = new Request.Builder()
                            .addHeader("Authorization", "Bearer " + TokenProvider.getInstance().getToken())
                            .url(url)
                            .method("GET", null)
                            .build();
                    client.newCall(okRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "Failed to link account " + accountType, e);
                            linkAccountCallback.onFailure();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // TODO parse response;
                            linkAccountCallback.onSuccess();
                        }
                    });
                }
            }

            private void handleError(int code, String description) {
                linkAccountFragment.showError(description);
            }
        });

        webView.loadUrl(SERVER_HOST);
    }

    private String getConnectUrl(AccountType accountType) {
        String suffix = "";
        switch (accountType) {
            case VK:
                suffix = "vkontakte";
                break;
            case FB:
                suffix = "facebook";
                break;
            default:
                throw new IllegalArgumentException("Unknown account type");
        }
        return SERVER_HOST + "/connect/" + suffix;
    }

    private String getScope(AccountType accountType) {
        switch (accountType) {
            case VK:
                return "photos";
            case FB:
                return "public_profile,email,user_hometown,user_location,user_birthday,user_photos";
            default:
                throw new IllegalArgumentException("Unknown account type");
        }
    }

    public void retry(WebView webview) {
        isError = false;
        webview.loadUrl(SERVER_HOST);
    }
}
