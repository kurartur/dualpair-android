package lt.dualpair.android.ui.accounts;

import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.api.VKError;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.accounts.OAuthConstants;
import lt.dualpair.android.data.remote.client.authentication.RequestTokenClient;

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
                .subscribe(new TokenRequestObserver(loginActivity));
    }

    @Override
    public void onError(VKError error) {
        Log.e(TAG, "VK login error", error.httpError);
    }

    private Long getExpiresIn(int expiresInSec) {
        throw new UnsupportedOperationException("Not implemented"); // TODO
    }

}
