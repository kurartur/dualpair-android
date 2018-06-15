package lt.dualpair.android.ui.accounts;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.SocialConstants;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.ToastUtils;

public class EditAccountsActivity extends BaseActivity implements CommonOnLoginCallback {

    private static final String TAG = "EditAccountsActivity";
    private static final String ACCOUNT_TYPE_KEY = "ACCOUNT_TYPE";

    @Bind(R.id.account_list) RecyclerView accountList;

    private CallbackManager callbackManager = CallbackManager.Factory.create();

    private EditAccountsViewModel viewModel;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar(true, getString(R.string.accounts_and_communication));
        setContentView(R.layout.edit_accounts);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(ACCOUNT_TYPE_KEY)) {
            linkAccount((AccountType)getIntent().getSerializableExtra(ACCOUNT_TYPE_KEY));
        }
        viewModel = ViewModelProviders.of(this, new EditAccountsViewModel.Factory(getApplication())).get(EditAccountsViewModel.class);
        subscribeUi();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        VKSdk.onActivityResult(requestCode, resultCode, data, new VKontakteLoginCallback(this));
    }

    private void subscribeUi() {
        viewModel.getAccounts().observe(this, new Observer<List<SocialAccountItem>>() {
            @Override
            public void onChanged(@Nullable List<SocialAccountItem> accounts) {
                renderAccounts(accounts);
            }
        });
    }

    public void renderAccounts(List<SocialAccountItem> socialAccountItems) {
        accountList.setAdapter(new AccountListAdapter(socialAccountItems, new AccountListAdapter.OnItemClickListener() {
            @Override
            public void onClick(SocialAccountItem item) {
                if (item.getUserAccount() == null) {
                    linkAccount(item.getAccountType());
                }
            }
        }));
    }

    public void linkAccount(AccountType accountType) {
        switch (accountType) {
            case FB:
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookLoginCallback(this));
                LoginManager.getInstance().logInWithReadPermissions(this, new ArrayList<>(Arrays.asList(SocialConstants.FACEBOOK_SCOPE.split(","))));
                break;
            case VK:
                VKSdk.login(this, SocialConstants.VKONTAKTE_SCOPE);
                break;
        }
    }

    public void onAccountAdded() {
        if (getIntent().hasExtra(ACCOUNT_TYPE_KEY)) {
            setResult(Activity.RESULT_OK, getIntent());
            finish();
        }
    }

    public void onCanceled() {
        if (getIntent().hasExtra(ACCOUNT_TYPE_KEY)) {
            finish();
        }
    }

    @Override
    public void onSuccess(String providerId, String accessToken, Long expiresIn, String scope) {
        disposable.add(
            viewModel.connectAccount(providerId, accessToken, expiresIn, scope)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(this::onAccountAdded, this::onError)
        );
    }

    @Override
    public void onError(Throwable throwable) {
        ToastUtils.show(this, throwable.getMessage());
        if (getIntent().hasExtra(ACCOUNT_TYPE_KEY)) {
            finish();
        }
    }

    public static class FacebookLoginCallback implements FacebookCallback<LoginResult> {

        private CommonOnLoginCallback callback;

        public FacebookLoginCallback(CommonOnLoginCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            callback.onSuccess("facebook", accessToken.getToken(), accessToken.getExpires().getTime(), null);
        }

        @Override
        public void onCancel() {
            callback.onCanceled();
        }

        @Override
        public void onError(FacebookException error) {
            callback.onError(error);
        }

    }

    public static class VKontakteLoginCallback implements VKCallback<VKAccessToken> {

        private CommonOnLoginCallback callback;

        public VKontakteLoginCallback(CommonOnLoginCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onResult(VKAccessToken res) {
            callback.onSuccess("vkontakte", res.accessToken, null, null);
        }

        @Override
        public void onError(VKError error) {
            if (error.errorCode == VKError.VK_CANCELED) {
                callback.onCanceled();
            } else {
                callback.onError(new Exception(error.errorMessage));
            }
        }
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, EditAccountsActivity.class);
    }

    public static Intent createIntent(Activity activity, AccountType accountType) {
        Intent intent = new Intent(activity, EditAccountsActivity.class);
        intent.putExtra(ACCOUNT_TYPE_KEY, accountType);
        return intent;
    }
}
