package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserListItem;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.CustomActionBarFragment;
import lt.dualpair.android.ui.user.UserFragment;

public class ReviewHistoryActivity extends BaseActivity implements UserListRecyclerAdapter.OnItemClickListener,
        FragmentManager.OnBackStackChangedListener{

    private final static String LIST_FRAGMENT = "ReviewedUserListFragment";
    private final static String USER_FRAGMENT = "UserFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_history_layout);

        FragmentManager fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(this);
        ReviewedUserListFragment fragment = (ReviewedUserListFragment) fm.findFragmentByTag(LIST_FRAGMENT);
        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            fragment = new ReviewedUserListFragment();
            ft.add(android.R.id.content, fragment, LIST_FRAGMENT);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        Fragment top = fragments.get(fragments.size() - 1);
        if (top != null) {
            if (top instanceof CustomActionBarFragment) {
                CustomActionBarFragment custom = (CustomActionBarFragment)top;
                setupActionBar(custom);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void setupActionBar(CustomActionBarFragment fragment) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (fragment.getActionBarTitle() != null) {
                actionBar.setDisplayShowCustomEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setTitle(fragment.getActionBarTitle());
            } else if (fragment.getActionBarView() != null) {
                actionBar.setDisplayShowCustomEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setCustomView(fragment.getActionBarView());
            }
        }
    }

    @Override
    public void onClick(UserListItem item) {
        FragmentManager fm = getSupportFragmentManager();
        UserFragment fragment = UserFragment.newInstance(item.getUserId());
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(android.R.id.content, fragment, USER_FRAGMENT);
        ft.addToBackStack(null);
        ft.commit();
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, ReviewHistoryActivity.class);
    }

}
