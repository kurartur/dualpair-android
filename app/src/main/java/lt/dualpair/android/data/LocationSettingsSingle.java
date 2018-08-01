package lt.dualpair.android.data;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class LocationSettingsSingle extends Single<LocationSettingsResult> {

    private final GoogleApiClient googleApiClient;
    SingleObserver<? super LocationSettingsResult> observer;
    private LocationRequest locationRequest;

    public LocationSettingsSingle(Context context, LocationRequest locationRequest) {
        Callbacks callbacks = new Callbacks();
        googleApiClient =
                new GoogleApiClient.Builder(context, callbacks, callbacks)
                        .addApi(LocationServices.API)
                        .build();
        this.locationRequest = locationRequest;
    }

    @Override
    protected void subscribeActual(SingleObserver<? super LocationSettingsResult> o) {
        if (observer != null) {
            o.onError(new Exception("Already subscribed to this source"));
        }
        observer = o;

        synchronized (this) {
            observer.onSubscribe(new Disposable() {
                private boolean disposed = false;

                @Override
                public void dispose() {
                    googleApiClient.disconnect();
                    disposed = true;
                }

                @Override
                public boolean isDisposed() {
                    return disposed;
                }
            });
            googleApiClient.connect();
        }
    }

    private class Callbacks implements
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                            builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    observer.onSuccess(result);
                    googleApiClient.disconnect();
                }
            });
        }

        @Override
        public void onConnectionSuspended(int i) {
            observer.onError(new Exception("Connection suspended"));
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            observer.onError(new Exception("Connection failed"));
        }

    }

}
