package com.artur.dualpair.android.ui;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.artur.dualpair.android.R;
import com.artur.dualpair.android.core.user.GetUserPrincipalTask;
import com.artur.dualpair.android.core.user.SetLocationTask;
import com.artur.dualpair.android.dto.SearchParameters;
import com.artur.dualpair.android.dto.User;
import com.artur.dualpair.android.rx.EmptySubscriber;
import com.artur.dualpair.android.services.ServiceException;
import com.artur.dualpair.android.ui.main.MainActivity;
import com.artur.dualpair.android.ui.search.SearchParametersActivity;
import com.artur.dualpair.android.ui.user.AddSociotypeActivity;
import com.artur.dualpair.android.ui.user.SetDateOfBirthActivity;
import com.artur.dualpair.android.utils.LocationUtils;
import com.artur.dualpair.android.utils.OnceOnlyLocationListener;
import com.artur.dualpair.android.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

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

    private void init() {
        progressLayout.setVisibility(View.VISIBLE);
        initUser();
    }

    private void initUser() {
        progressText.setText(getResources().getString(R.string.loading_user) + "...");
        new GetUserPrincipalTask(this).execute(new EmptySubscriber<User>() {
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
                validateUser(user);
            }
        });
    }

    private void validateUser(User user) {
        SearchParameters searchParameters = user.getSearchParameters();
        if (user.getSociotypes().isEmpty()) {
            startActivityForResult(AddSociotypeActivity.createIntent(this), ADD_SOCIOTYPE_REQUEST_CODE);
        } else if (user.getDateOfBirth() == null) {
            startActivityForResult(SetDateOfBirthActivity.createIntent(this), SET_BIRTHDAY_REQUEST_CODE);
        } else if (searchParameters == null
                || (!searchParameters.getSearchFemale() && !searchParameters.getSearchMale())
                || searchParameters.getMinAge() == null
                || searchParameters.getMaxAge() == null) {
            startActivityForResult(SearchParametersActivity.createIntent(this), SEARCH_PARAMETERS_REQUEST_CODE);
        } else {
            requestLocationUpdate();
        }
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
        new SetLocationTask(this, com.artur.dualpair.android.dto.Location.fromAndroidLocation(location)).execute(new EmptySubscriber<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Unable to set location", e);
                ToastUtils.show(SplashActivity.this, "Unable to set location");
            }

            @Override
            public void onNext(Void aVoid) {
                progressLayout.setVisibility(View.GONE);
                startActivity(MainActivity.createIntent(SplashActivity.this));
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
