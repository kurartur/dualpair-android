package lt.dualpair.android.ui.accounts;

import android.accounts.AccountManager;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

public class LoginPresenter {

    private static final String TAG = "LoginPresenter";

    private LoginActivity loginActivity;
    private AccountManager accountManager;

    private CallbackManager callbackManager;

    private boolean isError = false;

    public LoginPresenter(LoginActivity loginActivity, CallbackManager callbackManager, LoginButton facebookLoginButton) {
        this.loginActivity = loginActivity;
        this.accountManager = AccountManager.get(loginActivity);

        facebookLoginButton.setReadPermissions("public_profile,email,user_hometown,user_location,user_birthday,user_photos");
        facebookLoginButton.registerCallback(callbackManager, new FacebookLoginCallback(loginActivity));
    }

}
