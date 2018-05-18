package lt.dualpair.android.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

public class LocationSettingsLiveData extends LiveData<LocationSettingsResult> implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    public LocationSettingsLiveData(Context context, LocationRequest locationRequest) {
            googleApiClient =
                    new GoogleApiClient.Builder(context, this, this)
                            .addApi(LocationServices.API)
                            .build();
        this.locationRequest = locationRequest;
    }

        @Override
        protected void onActive() {
            googleApiClient.connect();
        }

        @Override
        protected void onInactive() {
            googleApiClient.disconnect();
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                            builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    setValue(result);
                }
            });
        }

        @Override
        public void onConnectionSuspended(int cause) {
            setValue(null);
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            setValue(null);
        }
}