package lt.dualpair.android.ui.accounts;


import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lt.dualpair.android.SocialConstants;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.remote.client.user.ConnectAccountClient;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.resource.UserAccount;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditAccountsPresenter {

    private static final String TAG = "EditAccountsPresenter";

    private EditAccountsActivity view;

    private CallbackManager callbackManager;

    public EditAccountsPresenter(EditAccountsActivity view, CallbackManager callbackManager) {
        this.view = view;
        this.callbackManager = callbackManager;
    }

    public void loadAccounts(boolean reload) {
        new UserDataManager(view).getUser(reload)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(view.<User>bindToLifecycle())
                .subscribe(new EmptySubscriber<User>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to load user", e);
                    }

                    @Override
                    public void onNext(User user) {
                        List<UserAccount> userAccounts = user.getAccounts();
                        view.renderAccounts(buildItems(userAccounts));
                    }
                });
    }

    public void onError(String message) {
        view.onError(message);
    }

    public void onAccountAdded() {
        view.onAccountAdded();
    }

    public void onCanceled() {
        view.onCanceled();
    }

    private List<SocialAccountItem> buildItems(List<UserAccount> userAccounts) {
        List<SocialAccountItem> items = new ArrayList<>();
        List<AccountType> allTypes = new ArrayList<>(Arrays.asList(AccountType.values()));
        for (UserAccount userAccount : userAccounts) {
            items.add(new SocialAccountItem(userAccount.getAccountType(), userAccount));
            allTypes.remove(userAccount.getAccountType());
        }
        for (AccountType accountType : allTypes) {
            items.add(new SocialAccountItem(accountType, null));
        }
        return items;
    }


    public void linkAccount(AccountType accountType) {
        switch (accountType) {
            case FB:
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookLoginCallback(this));
                LoginManager.getInstance().logInWithReadPermissions(view, new ArrayList<>(Arrays.asList(SocialConstants.FACEBOOK_SCOPE.split(","))));
                break;
            case VK:
                VKSdk.login(view, SocialConstants.VKONTAKTE_SCOPE);
                break;
        }
    }

    public static class FacebookLoginCallback implements FacebookCallback<LoginResult> {

        private static final String TAG = "FacebookLoginCallback";

        private EditAccountsPresenter presenter;

        public FacebookLoginCallback(EditAccountsPresenter presenter) {
            this.presenter = presenter;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            new ConnectAccountClient("facebook", accessToken.getToken(), accessToken.getExpires().getTime(), null)
                    .observable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new EmptySubscriber<Void>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "onCompleted");
                            presenter.onAccountAdded();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "onError");
                            presenter.onError(e.getMessage());
                        }

                        @Override
                        public void onNext(Void aVoid) {
                            Log.d(TAG, "onNext");
                        }
                    });
        }

        @Override
        public void onCancel() {
            presenter.onCanceled();
        }

        @Override
        public void onError(FacebookException error) {
            presenter.onError(error.getMessage());
        }

    }

    public static class VKontakteLoginCallback implements VKCallback<VKAccessToken> {

        private static final String TAG = "VKontakteLoginCallback";

        private EditAccountsPresenter presenter;

        public VKontakteLoginCallback(EditAccountsPresenter presenter) {
            this.presenter = presenter;
        }

        @Override
        public void onResult(VKAccessToken res) {
            new ConnectAccountClient("vkontakte", res.accessToken, null, null)
                    .observable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new EmptySubscriber<Void>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "onCompleted");
                            presenter.onAccountAdded();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "onError");
                        }

                        @Override
                        public void onNext(Void aVoid) {
                            Log.d(TAG, "onNext");
                        }
                    });
        }

        @Override
        public void onError(VKError error) {
            if (error.errorCode == VKError.VK_CANCELED) {
                presenter.onCanceled();
            } else {
                presenter.onError(error.errorMessage);
            }
        }
    }
}
