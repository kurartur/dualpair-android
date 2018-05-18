package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.bus.NewMatchEvent;
import lt.dualpair.android.bus.RxBus;
import lt.dualpair.android.gcm.RegistrationService;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.match.NewMatchActivity;
import lt.dualpair.android.utils.DrawableUtils;
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static volatile boolean isInForeground = false;
    private Subscription newMatchEventSubscription;

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
        init();
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
    protected void onResume() {
        super.onResume();
        newMatchEventSubscription = RxBus.getInstance().register(NewMatchEvent.class, new Action1<NewMatchEvent>() {
            @Override
            public void call(NewMatchEvent newMatchEvent) {
                showNewMatch(newMatchEvent.getMatchId());
            }
        });
        isInForeground = true;
    }

    private void showNewMatch(Long matchId) {
        startActivity(NewMatchActivity.createIntent(this, matchId));
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForeground = false;
        newMatchEventSubscription.unsubscribe();
    }

    public static boolean isInForeground() {
        return isInForeground;
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, MainActivity.class);
    }
}
