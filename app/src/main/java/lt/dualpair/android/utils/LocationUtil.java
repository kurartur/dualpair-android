package lt.dualpair.android.utils;

import com.google.android.gms.location.LocationRequest;

public class LocationUtil {

    public static LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public static double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double R = 6371e3; // earth's radius in meters
        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);
        double latDiff = Math.toRadians(latitude2 - latitude1);
        double lonDiff = Math.toRadians(longitude2 - longitude1);
        double a = Math.sin(latDiff/2) * Math.sin(latDiff/2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(lonDiff/2) * Math.sin(lonDiff/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

}
