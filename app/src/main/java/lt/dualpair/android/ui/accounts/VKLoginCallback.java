package lt.dualpair.android.ui.accounts;

import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.api.VKError;

import lt.dualpair.android.accounts.OAuthConstants;
import lt.dualpair.android.data.remote.client.authentication.RequestTokenClient;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VKLoginCallback implements VKCallback<VKAccessToken> {

    private static final String TAG = "VKLoginCallback";

    private LoginActivity loginActivity;

    public VKLoginCallback(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Override
    public void onResult(VKAccessToken res) {
        loginActivity.showProgress();
        new RequestTokenClient("vkontakte", res.accessToken, res.expiresIn == 0 ? null : getExpiresIn(res.expiresIn),
                null, OAuthConstants.CLIENT_ID, OAuthConstants.CLIENT_SERCET)
                .observable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new TokenRequestSubscriber(loginActivity));
    }

    @Override
    public void onError(VKError error) {
        Log.e(TAG, "VK login error", error.httpError);
    }

    private Long getExpiresIn(int expiresInSec) {
        throw new UnsupportedOperationException("Not implemented"); // TODO
    }

}
