package lt.dualpair.android.ui.accounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseActivity;

public class LinkAccountActivity extends BaseActivity implements LinkAccountCallback {

    private static final String TAG = "LinkAccountActivity";

    private static final String ACCOUNT_TYPE_KEY = "AccountType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_user_account_layout);

        AccountType accountType = (AccountType)getIntent().getSerializableExtra(ACCOUNT_TYPE_KEY);
        android.support.v4.app.Fragment fragment = LinkAccountFragment.getInstance(accountType, this);
        getSupportFragmentManager().beginTransaction().add(R.id.link_account_frame, fragment).commit();
    }

    @Override
    public void onSuccess() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onFailure() {
        Log.e(TAG, "Error while linking account");
    }

    public static Intent createIntent(Activity activity, AccountType accountType) {
        Intent intent = new Intent(activity, LinkAccountActivity.class);
        intent.putExtra(ACCOUNT_TYPE_KEY, accountType);
        return intent;
    }

}
