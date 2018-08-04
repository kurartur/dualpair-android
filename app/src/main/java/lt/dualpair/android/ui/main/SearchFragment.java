package lt.dualpair.android.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.trello.rxlifecycle2.android.FragmentEvent;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.ConnectivityMonitor;
import lt.dualpair.android.R;
import lt.dualpair.android.data.LocationSettingsSingle;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.VisibilitySwitcher;
import lt.dualpair.android.ui.search.SearchParametersActivity;
import lt.dualpair.android.utils.DrawableUtils;
import lt.dualpair.android.utils.LocationUtil;

public class SearchFragment extends BaseFragment implements ReviewFragment.OnResponseGivenCallback {

    private static final int SP_REQ_CODE = 1;

    private static final int LOCATION_PERMISSIONS_REQ_CODE = 1;
    private static final int LOCATION_SETTINGS_REQ_CODE = 4;
    private static final int PERMISSION_SETTING_REQ_CODE = 5;

    private static final String REVIEW_FRAME = "REVIEW_FRAME";

    private SearchViewModel viewModel;

    private VisibilitySwitcher visibilitySwitcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.search_layout, container, false);
        requestOfflineNotification(view.findViewById(R.id.offline));
        visibilitySwitcher = new VisibilitySwitcher(view,
                R.id.loading,
                R.id.review_frame,
                R.id.no_connection,
                R.id.unexpected_error,
                R.id.no_matches
        );
        visibilitySwitcher.switchTo(R.id.loading);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.review_frame, ReviewFragment.newInstance(this), REVIEW_FRAME);
        ft.commit();

        viewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        doLocationChecks();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.review_fragment_menu, menu);
        for(int i=0 ; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            DrawableUtils.setActionBarIconColorFilter(getActivity(), menuItem.getIcon());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_parameters_menu_item:
                startActivityForResult(SearchParametersActivity.createIntent(getActivity(), true), SP_REQ_CODE);
                break;
            case R.id.history_menu_item:
                startActivity(ReviewHistoryActivity.createIntent(getActivity()));
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOCATION_SETTINGS_REQ_CODE:
                find();
                break;
            case PERMISSION_SETTING_REQ_CODE:
                doLocationChecks();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSIONS_REQ_CODE:
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission);
                        if (!showRationale) {
                            showLocationAccessDeniedNotification();
                        } else {
                            showLocationAccessExplanation();
                        }
                    } else {
                        checkLocationSettings();
                    }
                }
                break;
        }
    }

    private void doLocationChecks() {
        if (!canAccessLocation()) {
            askForPermissionToAccessLocation();
        } else {
            checkLocationSettings();;
        }
    }

    private boolean canAccessLocation() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void askForPermissionToAccessLocation() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSIONS_REQ_CODE);
    }

    private void showLocationAccessExplanation() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }
        builder.setTitle(R.string.location_permissions_explanation_title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.location_permission_explanation_message)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        askForPermissionToAccessLocation();
                    }
                })
                .setNegativeButton(R.string.decline_anyway, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finishAffinity();
                    }
                })
                .show();
    }

    private void showLocationAccessDeniedNotification() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }
        builder.setTitle(R.string.location_permissions_explanation_title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.location_permission_denied_explanation_message)
                .setPositiveButton(R.string.open_app_settings,  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, PERMISSION_SETTING_REQ_CODE);
                    }
                })
                .setNegativeButton(R.string.close_app, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finishAffinity();
                    }
                })
                .show();
    }

    @SuppressLint("CheckResult")
    public void checkLocationSettings() {
        new LocationSettingsSingle(getContext(), LocationUtil.createLocationRequest())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(new Consumer<LocationSettingsResult>() {
                    @Override
                    public void accept(LocationSettingsResult result) throws Exception {
                        final Status status = result.getStatus();
                        final LocationSettingsStates states = result.getLocationSettingsStates();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                // All location settings are satisfied. The client can
                                // initialize location requests here.
                                find();
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied, but this can be fixed
                                // by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    startIntentSenderForResult(status.getResolution().getIntentSender(), LOCATION_SETTINGS_REQ_CODE, null, 0, 0, 0, null);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way
                                // to fix the settings so we won't show the dialog.
                                break;
                        }
                    }
        });
    }

    @SuppressLint("CheckResult")
    private void find() {
        if (ConnectivityMonitor.getInstance().getConnectivityInfo().blockingFirst().isNetworkAvailable()) {
            visibilitySwitcher.switchTo(R.id.loading);
            viewModel.find()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindUntilEvent(FragmentEvent.DESTROY))
                    .subscribe(this::render,
                            throwable -> {
                                Log.e(getClass().getName(), throwable.getMessage(), throwable);
                                visibilitySwitcher.switchTo(R.id.unexpected_error);
                            }, () -> visibilitySwitcher.switchTo(R.id.no_matches));
        } else {
            visibilitySwitcher.switchTo(R.id.no_connection);
        }
    }

    public void render(UserForView userForView) {
        ReviewFragment fragment = (ReviewFragment) getActivity().getSupportFragmentManager().findFragmentByTag(REVIEW_FRAME);
        fragment.renderReview(userForView);
        visibilitySwitcher.switchTo(R.id.review_frame);
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

    @Override
    public void onResponseGiven() {
        find();
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }
}
