package lt.dualpair.android.ui;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import lt.dualpair.android.ConnectivityMonitor;
import lt.dualpair.android.R;

public class BaseActivity extends AppCompatActivity implements LifecycleProvider<ActivityEvent> {

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected void setupActionBar(boolean enableHome, String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (enableHome) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            actionBar.setTitle(title);
        } else {
            Log.i(getClass().getSimpleName(), "ActionBar not found");
        }
    }

    protected void setupActionBar() {
        setupActionBar(true, getResources().getString(R.string.app_name));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    public Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject;
    }

    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }


    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    @CallSuper
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }

    @SuppressLint("CheckResult")
    protected void requestOfflineNotification(final View offlineNotificationView) {
        ConnectivityMonitor.getInstance().getConnectivityInfo()
                .compose(bindToLifecycle())
                .subscribe(new Consumer<ConnectivityMonitor.ConnectivityInfo>() {
                    @Override
                    public void accept(ConnectivityMonitor.ConnectivityInfo connectivityInfo) throws Exception {
                        if (offlineNotificationView != null) {
                            offlineNotificationView.setVisibility(connectivityInfo.isNetworkAvailable() ? View.GONE : View.VISIBLE);
                        }
                    }
                });
    }

}
