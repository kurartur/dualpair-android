package lt.dualpair.android.ui.accounts;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.Arrays;

import lt.dualpair.android.SocialConstants;

public class LoginPresenter {

    private static final String TAG = "LoginPresenter";

    private LoginActivity loginActivity;

    private CallbackManager callbackManager;

    public LoginPresenter(LoginActivity loginActivity, CallbackManager callbackManager) {
        this.loginActivity = loginActivity;
        this.callbackManager = callbackManager;
    }

    public void loginWithFacebook() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookLoginCallback(loginActivity));
        loginManager.logInWithReadPermissions(loginActivity, new ArrayList<>(Arrays.asList(SocialConstants.FACEBOOK_SCOPE.split(","))));
    }
}
