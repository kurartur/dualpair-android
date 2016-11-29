package lt.dualpair.android.ui.accounts;

import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.dualpair.android.R;
import lt.dualpair.android.ui.AboutActivity;

public class LoginActivity extends AccountAuthenticatorActivity {

    private static final String TAG = "LoginActivity";

    @Bind(R.id.login_webview) WebView login_webview;
    @Bind(R.id.login_progress) ProgressBar login_progress;
    @Bind(R.id.error_layout) View error_layout;
    @Bind(R.id.error_text) TextView error_text;

    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);
        loginPresenter = new LoginPresenter(this);
        loginPresenter.prepareWebView(login_webview);
    }

    public void showLoading() {
        login_webview.setVisibility(View.GONE);
        error_layout.setVisibility(View.GONE);
        login_progress.setVisibility(View.VISIBLE);
    }

    public void showWebView() {
        login_webview.setVisibility(View.VISIBLE);
        error_layout.setVisibility(View.GONE);
        login_progress.setVisibility(View.GONE);
    }

    public void showConnectionTimeoutError(String description) {
        showError(description);
    }

    public void showError(String description) {
        login_webview.setVisibility(View.GONE);
        error_layout.setVisibility(View.VISIBLE);
        login_progress.setVisibility(View.GONE);
        error_text.setText(description);
    }

    @OnClick(R.id.retry_button)
    public void onRetryClick(View v) {
        loginPresenter.retry(login_webview);
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
