package lt.dualpair.android.ui.accounts;

import android.accounts.AccountManager;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
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

        // Callback registration
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // TODO send token to server (exchange it to long living)

                /*new RequestTokenClient(code, OAuthConstants.CLIENT_ID, OAuthConstants.CLIENT_SERCET, REDIRECT_URI).observable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new EmptySubscriber<Token>() {
                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "Did not get token", e);
                                loginActivity.showError("Error while logging in");
                            }

                            @Override
                            public void onNext(Token token) {
                                TokenProvider.getInstance().storeToken(token.getAccessToken());
                                endAuth(token);
                            }
                        });

                new GetUserPrincipalClient().observable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new EmptySubscriber<User>() {
                            @Override
                            public void onNext(User user) {

                                Log.d(TAG, "START endAuth");

                                String accountName = user.getName() + " (" + user.getId() + ")";
                                Account account = new Account(accountName, AccountConstants.ACCOUNT_TYPE);
                                Bundle userData = new Bundle();
                                userData.putString(ARG_USER_ID, user.getId().toString());
                                userData.putString(ARG_USER_NAME, user.getName());

                                accountManager.addAccountExplicitly(account, token.getRefreshToken(), userData);
                                accountManager.setAuthToken(account, AccountConstants.ACCOUNT_TYPE, token.getAccessToken());

                                Bundle result = new Bundle();
                                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                                result.putString(AccountManager.KEY_AUTHTOKEN, token.getAccessToken());

                                accountManager.setPassword(account, token.getRefreshToken());

                                TokenProvider.getInstance().storeToken(token.getAccessToken());

                                loginActivity.setAccountAuthenticatorResult(result);
                                loginActivity.setResult(Activity.RESULT_OK);

                                Log.d(TAG, "END endAuth");

                                loginActivity.finish();
                            }
                        });*/
            }

            @Override
            public void onCancel() {
                // TODO open login screen again?
            }

            @Override
            public void onError(FacebookException exception) {
                // TODO show error toast or popup
            }
        });

    }

}
