package lt.dualpair.android.ui.accounts;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.dualpair.android.R;
import lt.dualpair.android.SocialConstants;
import lt.dualpair.android.ui.AboutActivity;

public class LoginActivity extends AccountAuthenticatorActivity {

    private static final String TAG = "LoginActivity";

    private CallbackManager callbackManager;

    @Bind(R.id.terms_and_conditions_disclaimer_text)
    TextView termAndConditionsDisclaimerText;

    @Bind(R.id.main_layout)
    View mainLayout;

    @Bind(R.id.progress_layout)
    View progressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);
        termAndConditionsDisclaimerText.setMovementMethod(LinkMovementMethod.getInstance());
        callbackManager = CallbackManager.Factory.create();
    }

    @OnClick(R.id.vk_login_button) void onVkLoginClick(View v) {
        loginWithVkontakte();
    }

    @OnClick(R.id.fb_login_button) void onFbLoginClick(View v) {
        loginWithFacebook();
    }

    public void showMain() {
        mainLayout.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);
    }

    public void showProgress() {
        mainLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
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
        VKSdk.onActivityResult(requestCode, resultCode, data, new VKLoginCallback(this));
    }

    public void loginWithFacebook() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookLoginCallback(this));
        loginManager.logInWithReadPermissions(this, new ArrayList<>(Arrays.asList(SocialConstants.FACEBOOK_SCOPE.split(","))));
    }

    public void loginWithVkontakte() {
        VKSdk.login(this, SocialConstants.VKONTAKTE_SCOPE);
    }
}
