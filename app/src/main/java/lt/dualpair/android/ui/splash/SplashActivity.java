package lt.dualpair.android.ui.splash;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.ui.main.MainActivity;
import lt.dualpair.android.ui.user.AddSociotypeActivity;
import lt.dualpair.android.ui.user.SetDateOfBirthActivity;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getName();

    private static final int ADD_SOCIOTYPE_REQUEST_CODE = 2;
    private static final int SET_BIRTHDAY_REQUEST_CODE = 3;

    private AddAccountTask addAccountTask = null;

    private Subscription sociotypeCheckerSubscription;
    private Subscription dateOfBirthSubscription;

    @Override
    protected void onResume() {
        super.onResume();
        final AccountManager am = AccountManager.get(this);
        Account account = AccountUtils.getAccount(am);
        if (account == null) {
            addAccount(am);
        } else {
            checkSociotype();
        }
    }

    @Override
    protected void onPause() {
        if (sociotypeCheckerSubscription != null) {
            sociotypeCheckerSubscription.unsubscribe();
        }
        if (dateOfBirthSubscription != null) {
            dateOfBirthSubscription.unsubscribe();
        }
        super.onPause();
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

    private void checkSociotype() {
        sociotypeCheckerSubscription = new SociotypeChecker(this).userHasSociotype()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean hasSociotype) {
                        if (!hasSociotype) {
                            startActivityForResult(AddSociotypeActivity.createIntent(SplashActivity.this, false), ADD_SOCIOTYPE_REQUEST_CODE);
                        } else {
                            checkDateOfBirth();
                        }
                    }
                });
    }

    private void checkDateOfBirth() {
        dateOfBirthSubscription = new DateOfBirthChecker(this).userHasDateOfBirth()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean hasDateOfBirth) {
                        if (!hasDateOfBirth) {
                            startActivityForResult(SetDateOfBirthActivity.createIntent(SplashActivity.this), SET_BIRTHDAY_REQUEST_CODE);
                        } else {
                            startMain();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
        switch (requestCode) {
            case ADD_SOCIOTYPE_REQUEST_CODE:
            case SET_BIRTHDAY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    checkSociotype();
                }
                break;
        }
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
