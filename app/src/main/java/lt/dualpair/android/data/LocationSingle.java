package lt.dualpair.android.data;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class LocationSingle extends Single<Location> {

    private final GoogleApiClient googleApiClient;
    SingleObserver<? super Location> observer;
    private LocationRequest locationRequest;

    public LocationSingle(Context context, LocationRequest locationRequest) {
        Callbacks callbacks = new Callbacks();
        googleApiClient =
                new GoogleApiClient.Builder(context, callbacks, callbacks)
                        .addApi(LocationServices.API)
                        .build();
        this.locationRequest = locationRequest;
    }

    @Override
    protected void subscribeActual(SingleObserver<? super Location> o) {
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
            GoogleApiClient.OnConnectionFailedListener,
            LocationListener {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }

        @Override
        public void onConnectionSuspended(int i) {
            observer.onError(new Exception("Connection suspended"));
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            observer.onError(new Exception("Connection failed"));
        }

        @Override
        public void onLocationChanged(Location location) {
            observer.onSuccess(location);
            googleApiClient.disconnect();
        }
    }
}
