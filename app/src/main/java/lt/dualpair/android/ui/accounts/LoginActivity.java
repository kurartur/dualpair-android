package lt.dualpair.android.ui.accounts;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.dualpair.android.R;
import lt.dualpair.android.ui.AboutActivity;

public class LoginActivity extends AccountAuthenticatorActivity {

    private static final String TAG = "LoginActivity";

    @Bind(R.id.fb_login_button) LoginButton facebookLoginButton;

    private LoginPresenter loginPresenter;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);
        callbackManager = CallbackManager.Factory.create();
        loginPresenter = new LoginPresenter(this, callbackManager, facebookLoginButton);
    }

    @OnClick(R.id.vk_login_button) void onVkLoginClick(View v) {
        VKSdk.login(this, "photos");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // TODO get token and create account.
            }
            @Override
            public void onError(VKError error) {
                // TODO show error;
            }
        });
    }
}
