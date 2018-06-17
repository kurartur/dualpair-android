package lt.dualpair.android.ui.splash;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.main.MainActivity;
import lt.dualpair.android.ui.socionics.SetSociotypeActivity;
import lt.dualpair.android.ui.user.SetDateOfBirthActivity;
import lt.dualpair.android.utils.ToastUtils;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getName();

    private static final int ADD_SOCIOTYPE_REQUEST_CODE = 2;
    private static final int SET_BIRTHDAY_REQUEST_CODE = 3;
    private static final int GOOGLE_API_REQUEST_CODE = 4;

    private AddAccountTask addAccountTask = null;

    private SplashViewModel viewModel;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onStart() {
        super.onStart();
        final AccountManager am = AccountManager.get(this);
        Account account = AccountUtils.getAccount(am);
        if (account == null) {
            addAccount(am);
        } else {
            viewModel = ViewModelProviders.of(this, new SplashViewModel.Factory(getApplication())).get(SplashViewModel.class);
            checkGoogleApiAvailability();
        }
    }

    private void checkGoogleApiAvailability() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int googlePlayServicesAvailable = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (googlePlayServicesAvailable == ConnectionResult.SUCCESS) {
            checkSociotype();
        } else {
            googleApiAvailability.getErrorDialog(this, googlePlayServicesAvailable, GOOGLE_API_REQUEST_CODE, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            }).show();
        }
    }

    private void checkSociotype() {
        disposable.add(viewModel.getUserSociotypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sociotypeList -> {
                    if (sociotypeList.isEmpty()) {
                        startActivityForResult(SetSociotypeActivity.createIntent(SplashActivity.this, true), ADD_SOCIOTYPE_REQUEST_CODE);
                    } else {
                        checkDateOfBirth();
                    }
                }, e -> {
                    ToastUtils.show(this, e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }));
    }

    private void checkDateOfBirth() {
        disposable.add(viewModel.getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(u -> {
                    if (u.getDateOfBirth() == null) {
                        startActivityForResult(SetDateOfBirthActivity.createIntent(SplashActivity.this), SET_BIRTHDAY_REQUEST_CODE);
                    } else {
                        startMain();
                    }
                }, e -> {
                    ToastUtils.show(this, e.getMessage());
                    Log.e(TAG, e.getMessage(), e);
                }));
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
        switch (requestCode) {
            case ADD_SOCIOTYPE_REQUEST_CODE:
            case SET_BIRTHDAY_REQUEST_CODE:
            case GOOGLE_API_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    checkSociotype();
                }
                break;
        }
    }

    private void addAccount(final AccountManager am) {
        if (addAccountTask != null) {
            addAccountTask.cancel(true);
        }
        addAccountTask = new AddAccountTask(am, this);
        addAccountTask.execute((Void)null);
    }

    private void startMain() {
        startActivity(MainActivity.createIntent(this));
        finish();
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, SplashActivity.class);
    }

    private static class AddAccountTask extends AsyncTask<Void, Void, Bundle> {

        private AccountManager am;
        private Activity activity;

        public AddAccountTask(AccountManager am, Activity activity) {
            this.am = am;
            this.activity = activity;
        }

        @Override
        protected Bundle doInBackground(Void... params) {
            return AccountUtils.addAccount(am, activity);
        }

        @Override
        protected void onPostExecute(Bundle bundle) {}

    }
}
