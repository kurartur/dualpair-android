package lt.dualpair.android.ui.accounts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseFragment;

public class LinkAccountFragment extends BaseFragment {

    private static final String TAG = "LinkAccFrag";

    @Bind(R.id.webview) WebView webview;
    @Bind(R.id.progress_layout) View progress_layout;
    @Bind(R.id.error_layout) View error_layout;
    @Bind(R.id.error_text) TextView error_text;

    protected LinkAccountPresenter linkAccountPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.link_user_account_fragment_layout, null);
        ButterKnife.bind(this, view);
        linkAccountPresenter.setupWebView(webview);
        return view;
    }

    public void showLoading() {
        webview.setVisibility(View.GONE);
        error_layout.setVisibility(View.GONE);
        progress_layout.setVisibility(View.VISIBLE);
    }

    public void showWebView() {
        webview.setVisibility(View.VISIBLE);
        error_layout.setVisibility(View.GONE);
        progress_layout.setVisibility(View.GONE);
    }

    public void showError(String description) {
        webview.setVisibility(View.GONE);
        error_layout.setVisibility(View.VISIBLE);
        progress_layout.setVisibility(View.GONE);
        error_text.setText(description);
    }

    @OnClick(R.id.retry_button)
    public void onRetryClick(View v) {
        linkAccountPresenter.retry(webview);
    }

    public void setLinkAccountPresenter(LinkAccountPresenter linkAccountPresenter) {
        this.linkAccountPresenter = linkAccountPresenter;
    }

    public static LinkAccountFragment getInstance(AccountType accountType, LinkAccountCallback linkAccountCallback) {
        LinkAccountFragment fragment = new LinkAccountFragment();
        fragment.setLinkAccountPresenter(new LinkAccountPresenter(fragment, accountType, linkAccountCallback));
        return fragment;
    }

}
