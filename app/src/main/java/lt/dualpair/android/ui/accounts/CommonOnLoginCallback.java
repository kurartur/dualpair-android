package lt.dualpair.android.ui.accounts;

public interface CommonOnLoginCallback {
    void onSuccess(String providerId, String accessToken, Long expiresIn, String scope);
    void onCanceled();
    void onError(Throwable throwable);
}