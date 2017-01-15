package lt.dualpair.android.ui.accounts;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

import lt.dualpair.android.accounts.OAuthConstants;
import lt.dualpair.android.data.remote.client.authentication.RequestTokenClient;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FacebookLoginCallback implements FacebookCallback<LoginResult> {

    private static final String TAG = "FacebookLoginCallb";

    private LoginActivity loginActivity;

    public FacebookLoginCallback(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        loginActivity.showProgress();
        AccessToken accessToken = loginResult.getAccessToken();
        new RequestTokenClient("facebook", accessToken.getToken(), accessToken.getExpires().getTime(),
                                    null, OAuthConstants.CLIENT_ID, OAuthConstants.CLIENT_SERCET)
            .observable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new TokenRequestSubscriber(loginActivity));
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "Facebook login cancelled");
    }

    @Override
    public void onError(FacebookException error) {
        Log.e(TAG, "Facebook login error", error);
    }
}
