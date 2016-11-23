package lt.dualpair.android.ui.accounts;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;

import lt.dualpair.android.R;
import lt.dualpair.android.TokenProvider;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LinkVKAccountFragment extends Fragment {

    private static final String TAG = "LinkVKAccFrag";

    private WebView webView;
    private LinkAccountCallback linkAccountCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        webView = (WebView)inflater.inflate(R.layout.link_user_account_webview, null);
        webView.clearCache(true);
        webView.clearHistory();
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            private boolean isInitial(String url) {
                return url.contains("dualpair.lt:8080") && !url.contains("code=");
            }

            private boolean isCode(String url) {
                return url.contains("dualpair.lt:8080/connect/vkontakte") && url.contains("code=");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isInitial(url)) {
                    webView.loadUrl("file:///android_asset/loading.html");
                    OkHttpClient client = new OkHttpClient.Builder().followRedirects(false).build();
                    Request okRequest = new Request.Builder()
                            .addHeader("Authorization", "Bearer " + TokenProvider.getInstance().getToken())
                            .url("http://dualpair.lt:8080/connect/vkontakte")
                            .method("POST", RequestBody.create(MediaType.parse("text/plain"), ""))
                            .build();
                    client.newCall(okRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "Failed to link vk account", e);
                            linkAccountCallback.onFailure();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String location = response.header("location");
                            webView.post(new Runnable() {
                                @Override
                                public void run() {
                                    webView.loadUrl(location);
                                }
                            });
                        }
                    });
                    return true;
                } else if (isCode(url)) {
                    webView.loadUrl("file:///android_asset/loading.html");
                    OkHttpClient client = new OkHttpClient();
                    Request okRequest = new Request.Builder()
                            .addHeader("Authorization", "Bearer " + TokenProvider.getInstance().getToken())
                            .url(url)
                            .method("GET", null)
                            .build();
                    client.newCall(okRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "Failed to link vk account", e);
                            linkAccountCallback.onFailure();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // TODO parse response;
                            linkAccountCallback.onSuccess();
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        webView.loadUrl("http://dualpair.lt:8080/");
        return webView;
    }

    public void setLinkAccountCallback(LinkAccountCallback linkAccountCallback) {
        this.linkAccountCallback = linkAccountCallback;
    }

    public static LinkVKAccountFragment getInstance(LinkAccountCallback linkAccountCallback) {
        LinkVKAccountFragment fragment = new LinkVKAccountFragment();
        fragment.setLinkAccountCallback(linkAccountCallback);
        return fragment;
    }
}
