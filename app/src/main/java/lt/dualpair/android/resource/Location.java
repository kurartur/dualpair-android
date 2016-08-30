package lt.dualpair.android.resource;

import java.io.Serializable;

public class Location implements Serializable {

    private Double latitude;
    private Double longitude;
    private String countryCode;
    private String city;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public static Location fromAndroidLocation(android.location.Location l) {
        Location location = new Location();
        location.setLatitude(l.getLatitude());
        location.setLongitude(l.getLongitude());
        return location;
    }
}
