package com.artur.dualpair.android.utils;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class LocationUtils {

    public static Location getLocation(LocationManager lm) throws SecurityException {
        String bestProvider = lm.getBestProvider(new Criteria(), true);
        if (bestProvider != null) {
            return lm.getLastKnownLocation(bestProvider);
        }
        return null;
    }

    public static void getLocation(LocationManager lm, LocationListener listener) throws SecurityException {
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        } else if (lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, listener);
        } else {
            // TODO throw exception
        }
    }

}
