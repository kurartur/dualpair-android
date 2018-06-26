package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import lt.dualpair.android.R;
import lt.dualpair.android.bus.NewMatchEvent;
import lt.dualpair.android.bus.RxBus;
import lt.dualpair.android.data.local.entity.UserListItem;
import lt.dualpair.android.gcm.RegistrationService;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.CustomActionBarActivity;
import lt.dualpair.android.ui.CustomActionBarFragment;
import lt.dualpair.android.ui.user.UserFragment;

public class MainActivity extends BaseActivity implements CustomActionBarActivity,
        UserListRecyclerAdapter.OnItemClickListener, FragmentManager.OnBackStackChangedListener,
        UserFragment.OnUnmatchListener, UserFragment.OnReportListener {

    private static final String TAG = "MainActivity";
    private static final String USER_FRAGMENT = "UserFragment";
    private static volatile boolean isInForeground = false;
    private Disposable newMatchEventSubscription;

    @Bind(R.id.navigation)
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        ButterKnife.bind(this);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        newMatchEventSubscription = RxBus.getInstance().register(NewMatchEvent.class, new Consumer<NewMatchEvent>() {
            @Override
            public void accept(NewMatchEvent newMatchEvent) {
                showNewMatch(newMatchEvent.getMatchId());
            }
        });
        isInForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForeground = false;
        newMatchEventSubscription.dispose();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof CustomActionBarFragment) {
            setupActionBar((CustomActionBarFragment) fragment, false);
        } else {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    @Override
    public void requestActionBar(CustomActionBarFragment fragment) {
        setupActionBar(fragment, fragment instanceof UserFragment);
    }

    protected void setupActionBar(CustomActionBarFragment fragment, boolean homeUp) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(homeUp);
            actionBar.setDisplayShowHomeEnabled(homeUp);
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

    private void init() {
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_search:
                        selectedFragment = ReviewFragment.newInstance();
                        break;
                    case R.id.action_matches:
                        selectedFragment = MatchListFragment.newInstance();
                        break;
                    case R.id.action_profile:
                        selectedFragment = ProfileFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_up, android.R.anim.fade_out);
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ReviewFragment.newInstance());
        transaction.commit();

        startService(RegistrationService.createIntent(this));
    }

    @Override
    public void onClick(UserListItem item) {
        FragmentManager fm = getSupportFragmentManager();
        UserFragment f = UserFragment.newInstance(item.getUserId());
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(android.R.id.content, f, USER_FRAGMENT);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onUnmatch() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onReport() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager fm = getSupportFragmentManager();
        int backStackEntryCount = fm.getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            Fragment f = fm.getFragments().get(0);
            setupActionBar((CustomActionBarFragment) f, false);
        }
    }

    private void showNewMatch(Long matchId) {
        startActivity(NewMatchActivity.createIntent(this, matchId));
    }

    public static boolean isInForeground() {
        return isInForeground;
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, MainActivity.class);
    }
}
