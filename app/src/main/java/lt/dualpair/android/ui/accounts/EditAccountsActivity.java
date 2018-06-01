package lt.dualpair.android.ui.accounts;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.SocialConstants;
import lt.dualpair.android.data.remote.client.user.ConnectAccountClient;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.ToastUtils;

public class EditAccountsActivity extends BaseActivity {

    private static final String TAG = "EditAccountsActivity";
    private static final String ACCOUNT_TYPE_KEY = "ACCOUNT_TYPE";

    @Bind(R.id.account_list) RecyclerView accountList;

    private CallbackManager callbackManager = CallbackManager.Factory.create();

    private EditAccountsViewModel viewModel;

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
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            viewModel.reloadAccounts();
        }
    }

    public void onError(String text) {
        ToastUtils.show(this, text);
        if (getIntent().hasExtra(ACCOUNT_TYPE_KEY)) {
            finish();
        }
    }

    public void onCanceled() {
        if (getIntent().hasExtra(ACCOUNT_TYPE_KEY)) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        VKSdk.onActivityResult(requestCode, resultCode, data, new VKontakteLoginCallback(this));
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, EditAccountsActivity.class);
    }

    public static Intent createIntent(Activity activity, AccountType accountType) {
        Intent intent = new Intent(activity, EditAccountsActivity.class);
        intent.putExtra(ACCOUNT_TYPE_KEY, accountType);
        return intent;
    }

    public static class FacebookLoginCallback implements FacebookCallback<LoginResult> {

        private static final String TAG = "FacebookLoginCallback";

        private EditAccountsActivity activity;

        public FacebookLoginCallback(EditAccountsActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            new ConnectAccountClient("facebook", accessToken.getToken(), accessToken.getExpires().getTime(), null)
                    .completable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action() {
                        @Override
                        public void run() {
                            activity.onAccountAdded();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            activity.onError(throwable.getMessage());
                        }
                    });
        }

        @Override
        public void onCancel() {
            activity.onCanceled();
        }

        @Override
        public void onError(FacebookException error) {
            activity.onError(error.getMessage());
        }

    }

    public static class VKontakteLoginCallback implements VKCallback<VKAccessToken> {

        private static final String TAG = "VKontakteLoginCallback";

        private EditAccountsActivity activity;

        public VKontakteLoginCallback(EditAccountsActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onResult(VKAccessToken res) {
            new ConnectAccountClient("vkontakte", res.accessToken, null, null)
                    .completable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> {
                        Log.d(TAG, "onCompleted");
                        activity.onAccountAdded();
                    });
        }

        @Override
        public void onError(VKError error) {
            if (error.errorCode == VKError.VK_CANCELED) {
                activity.onCanceled();
            } else {
                activity.onError(error.errorMessage);
            }
        }
    }
}
