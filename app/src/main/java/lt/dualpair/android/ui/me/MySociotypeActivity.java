package lt.dualpair.android.ui.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseActivity;

public class MySociotypeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.sociotype);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(android.R.id.content, MySociotypeFragment.newInstance());
        ft.commit();
    }

    public static Intent createIntent(Context ctx) {
        return new Intent(ctx, MySociotypeActivity.class);
    }

}
