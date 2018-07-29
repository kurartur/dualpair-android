package lt.dualpair.android.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.CustomActionBarActivity;
import lt.dualpair.android.ui.CustomActionBarFragment;

public class UserActivity extends BaseActivity implements CustomActionBarActivity,
        UserFragment.OnUnmatchListener, UserFragment.OnReportListener {

    private static final String TAG = UserActivity.class.getName();
    private static final String ARG_USER_ID = "userId";
    private static final String USER_FRAGMENT = "UserFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Long userId = getIntent().getLongExtra(ARG_USER_ID, -1);
        if (userId == -1) {
            throw new RuntimeException("Reference not provided");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(USER_FRAGMENT) == null) {
            UserFragment userFragment = UserFragment.newInstance(userId);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(android.R.id.content, userFragment, USER_FRAGMENT);
            ft.commit();
        }

    }

    @Override
    public void onUnmatch() {
        finish();
    }

    @Override
    public void onReport() {
        finish();
    }

    @Override
    public void requestActionBar(CustomActionBarFragment fragment) {
        setupActionBar(fragment, true);
    }

    protected void setupActionBar(CustomActionBarFragment fragment, boolean homeUp) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (fragment.getActionBarView() != null) {
                actionBar.setDisplayShowCustomEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setCustomView(fragment.getActionBarView());
            } else {
                actionBar.setDisplayShowCustomEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setTitle(fragment.getActionBarTitle());
            }
        }
    }

    public static Intent createIntent(Context ctx, Long userId) {
        Intent intent = new Intent(ctx, UserActivity.class);
        intent.putExtra(ARG_USER_ID, userId);
        return intent;
    }

}
