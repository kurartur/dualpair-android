package lt.dualpair.android.ui.main;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.bus.NewMatchEvent;
import lt.dualpair.android.bus.RxBus;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.task.user.SetLocationTask;
import lt.dualpair.android.gcm.RegistrationService;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.match.NewMatchActivity;
import lt.dualpair.android.utils.LocationUtils;
import lt.dualpair.android.utils.OnceOnlyLocationListener;
import lt.dualpair.android.utils.ToastUtils;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_CODE = 1;
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

        final AccountManager am = AccountManager.get(this);
        Account account = AccountUtils.getAccount(am);
        if (account == null) {
            new AsyncTask<Void, Void, Bundle>() {
                @Override
                protected Bundle doInBackground(Void... params) {
                    return AccountUtils.addAccount(am, MainActivity.this);
                }

                @Override
                protected void onPostExecute(Bundle bundle) {
                    init();
                }
            }.execute((Void)null);
        } else {
            init();
        }
    }

    private void init() {
        MainFragmentPageAdapter adapter = new MainFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (adapter.getIconId(i) == null) {
                tab.setText(adapter.getTitleId(i));
            } else {
                tab.setIcon(adapter.getIconId(i));
            }
        }
        startService(RegistrationService.createIntent(this));
        requestLocationUpdate();
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

    private void requestLocationUpdate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            updateLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocation();
                } else {
                    finish();
                }
                break;
            }
        }
    }

    private void updateLocation() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = LocationUtils.getLocation(lm);
        if (location != null) {
            saveLocation(location);
        } else {
            LocationUtils.getLocation(lm, new OnceOnlyLocationListener(lm) {
                @Override
                protected void handleLocationChange(Location location) {
                    saveLocation(location);
                }
            });
        }
    }

    private void saveLocation(Location location) {
        new SetLocationTask(null, lt.dualpair.android.data.resource.Location.fromAndroidLocation(location)).execute(this) // TODO token null
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to set location", e);
                ToastUtils.show(MainActivity.this, "Unable to set location");
            }
        });
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, MainActivity.class);
    }
}
