package lt.dualpair.android.ui;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.SearchParametersManager;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.remote.services.ServiceException;
import lt.dualpair.android.data.remote.task.user.SetLocationTask;
import lt.dualpair.android.data.resource.SearchParameters;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.gcm.RegistrationService;
import lt.dualpair.android.ui.main.MainActivity;
import lt.dualpair.android.ui.search.SearchParametersActivity;
import lt.dualpair.android.ui.user.AddSociotypeActivity;
import lt.dualpair.android.ui.user.SetDateOfBirthActivity;
import lt.dualpair.android.utils.LocationUtils;
import lt.dualpair.android.utils.OnceOnlyLocationListener;
import lt.dualpair.android.utils.ToastUtils;
import rx.Subscription;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private static final int ADD_SOCIOTYPE_REQUEST_CODE = 2;
    private static final int SET_BIRTHDAY_REQUEST_CODE = 3;
    private static final int SEARCH_PARAMETERS_REQUEST_CODE = 4;

    @Bind(R.id.progress_layout)
    LinearLayout progressLayout;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.progress_text)
    TextView progressText;

    private Subscription userSubscription;
    private Subscription searchParametersSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (userSubscription != null) {
            userSubscription.unsubscribe();
        }
        if (searchParametersSubscription != null ) {
            searchParametersSubscription.unsubscribe();
        }
    }

    private void init() {
        progressLayout.setVisibility(View.VISIBLE);

        final AccountManager accountManager = AccountManager.get(this);
        Account account = AccountUtils.getAccount(accountManager, this);
        if (account == null) {
            new AsyncTask<Void, Void, Bundle>() {
                @Override
                protected Bundle doInBackground(Void... params) {
                    return AccountUtils.addAccount(accountManager, SplashActivity.this);
                }

                @Override
                protected void onPostExecute(Bundle bundle) {
                    initUser();
                }
            }.execute((Void)null);
        } else {
            initUser();
        }

    }

    private void initUser() {
        progressText.setText(getResources().getString(R.string.loading_user) + "...");
        userSubscription = new UserDataManager(this).getUser(new EmptySubscriber<User>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ServiceException && ((ServiceException)e).getResponse().code() != 401) {
                    Log.e(TAG, "Unable to load user", e);
                    ToastUtils.show(SplashActivity.this, "Unable to load user");
                }
                finish();
            }

            @Override
            public void onNext(User user) {
                unsubscribe();
                validateUser(user);
            }
        });
    }

    private void validateUser(User user) {
        if (user.getSociotypes().isEmpty()) {
            startActivityForResult(AddSociotypeActivity.createIntent(this), ADD_SOCIOTYPE_REQUEST_CODE);
        } else if (user.getDateOfBirth() == null) {
            startActivityForResult(SetDateOfBirthActivity.createIntent(this), SET_BIRTHDAY_REQUEST_CODE);
        } else {
            validateSearchParameters();
        }

    }

    private void validateSearchParameters() {
        searchParametersSubscription = new SearchParametersManager(this).getSearchParameters(new EmptySubscriber<SearchParameters>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(SearchParameters searchParameters) {
                unsubscribe();
                if (searchParameters == null
                        || (!searchParameters.getSearchFemale() && !searchParameters.getSearchMale())
                        || searchParameters.getMinAge() == null
                        || searchParameters.getMaxAge() == null) {
                    startActivityForResult(SearchParametersActivity.createIntent(SplashActivity.this), SEARCH_PARAMETERS_REQUEST_CODE);
                } else {
                    requestLocationUpdate();
                }
            }
        });
    }

    private void requestLocationUpdate() {
        progressText.setText(getResources().getString(R.string.updating_location) + "...");
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
        new SetLocationTask(this, lt.dualpair.android.data.resource.Location.fromAndroidLocation(location)).execute(new EmptySubscriber<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to set location", e);
                ToastUtils.show(SplashActivity.this, "Unable to set location");
            }

            @Override
            public void onNext(Void aVoid) {
                progressLayout.setVisibility(View.GONE);
                startActivity(MainActivity.createIntent(SplashActivity.this));
                startService(RegistrationService.createIntent(SplashActivity.this));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_SOCIOTYPE_REQUEST_CODE:
            case SET_BIRTHDAY_REQUEST_CODE:
            case SEARCH_PARAMETERS_REQUEST_CODE:
                if (Activity.RESULT_CANCELED == resultCode) {
                    finish();
                } else {
                    initUser();
                }
                break;
        }
    }

    public static Intent createIntent(Activity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        return intent;
    }
}
