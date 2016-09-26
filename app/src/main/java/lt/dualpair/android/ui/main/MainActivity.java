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
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.match.MatchActivity;
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

        viewPager.setAdapter(new MainFragmentPageAdapter(this, getFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        newMatchEventSubscription = RxBus.getInstance().register(NewMatchEvent.class, new Action1<NewMatchEvent>() {
            @Override
            public void call(NewMatchEvent newMatchEvent) {
                showNewMatch();
            }
        });
        isInForeground = true;
    }

    private void showNewMatch() {
        Intent intent = new Intent(this, MatchActivity.class);
        startActivity(intent);
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
