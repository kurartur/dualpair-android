package lt.dualpair.android.ui.accounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseActivity;

public class EditAccountsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.edit_accounts);
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, EditAccountsActivity.class);
    }
}
