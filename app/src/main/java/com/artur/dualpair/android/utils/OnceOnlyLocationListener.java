package com.artur.dualpair.android.utils;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public abstract class OnceOnlyLocationListener implements LocationListener {

    protected LocationManager locationManager;

    public OnceOnlyLocationListener(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    protected abstract void handleLocationChange(Location location);

    @Override
    public void onLocationChanged(Location location) throws SecurityException {
        handleLocationChange(location);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
