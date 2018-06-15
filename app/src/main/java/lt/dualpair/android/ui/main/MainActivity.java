package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.LinearLayout;

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
import lt.dualpair.android.utils.DrawableUtils;

public class MainActivity extends BaseActivity implements CustomActionBarActivity,
        UserListRecyclerAdapter.OnItemClickListener, FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "MainActivity";
    private static final String USER_FRAGMENT = "UserFragment";
    private static volatile boolean isInForeground = false;
    private Disposable newMatchEventSubscription;

    @Bind(R.id.tabs)
    TabLayout tabLayout;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.layout_main)
    LinearLayout mainLayout;

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
        final MainFragmentPageAdapter adapter = new MainFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setIcon(DrawableUtils.getActionBarIcon(this, adapter.getIconId(i)));
            if (i == 0) {
                DrawableUtils.setAccentColorFilter(this, tab.getIcon());
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                DrawableUtils.setAccentColorFilter(MainActivity.this, tab.getIcon());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                DrawableUtils.setActionBarIconColorFilter(MainActivity.this, tab.getIcon());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

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
    public void onBackStackChanged() {
        FragmentManager fm = getSupportFragmentManager();
        int backStackEntryCount = fm.getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            Fragment f = fm.findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
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
