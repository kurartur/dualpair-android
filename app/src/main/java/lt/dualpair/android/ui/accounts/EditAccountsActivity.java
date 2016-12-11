package lt.dualpair.android.ui.accounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.facebook.CallbackManager;
import com.vk.sdk.VKSdk;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.ToastUtils;

public class EditAccountsActivity extends BaseActivity {

    private static final String TAG = "EditAccountsActivity";
    private static final String ACCOUNT_TYPE_KEY = "ACCOUNT_TYPE";

    @Bind(R.id.account_list) RecyclerView accountList;

    private EditAccountsPresenter presenter;

    private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar(true, getString(R.string.accounts_and_communication));
        setContentView(R.layout.edit_accounts);
        ButterKnife.bind(this);
        presenter = new EditAccountsPresenter(this, callbackManager);
        presenter.loadAccounts(false);

        if (getIntent().hasExtra(ACCOUNT_TYPE_KEY)) {
            presenter.linkAccount((AccountType)getIntent().getSerializableExtra(ACCOUNT_TYPE_KEY));
        }
    }

    public void renderAccounts(List<SocialAccountItem> socialAccountItems) {
        accountList.setAdapter(new AccountListAdapter(socialAccountItems, new AccountListAdapter.OnItemClickListener() {
            @Override
            public void onClick(SocialAccountItem item) {
                if (item.getUserAccount() == null) {
                    presenter.linkAccount(item.getAccountType());
                }
            }
        }));
    }

    public void onAccountAdded() {
        if (getIntent().hasExtra(ACCOUNT_TYPE_KEY)) {
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            presenter.loadAccounts(true);
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
        VKSdk.onActivityResult(requestCode, resultCode, data, new EditAccountsPresenter.VKontakteLoginCallback(presenter));
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
