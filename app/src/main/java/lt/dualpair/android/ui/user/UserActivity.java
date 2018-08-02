package lt.dualpair.android.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseActivity;

public class UserActivity extends BaseActivity implements UserFragment.OnUnmatchListener,
        UserFragment.OnReportListener {

    private static final String TAG = UserActivity.class.getName();
    private static final String ARG_USER_ID = "userId";
    private static final String USER_FRAGMENT = "UserFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        Long userId = getIntent().getLongExtra(ARG_USER_ID, -1);
        if (userId == -1) {
            throw new RuntimeException("Reference not provided");
        }

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(USER_FRAGMENT) == null) {
            UserFragment userFragment = UserFragment.newInstance(userId);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_frame, userFragment, USER_FRAGMENT);
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

    public static Intent createIntent(Context ctx, Long userId) {
        Intent intent = new Intent(ctx, UserActivity.class);
        intent.putExtra(ARG_USER_ID, userId);
        return intent;
    }

}
