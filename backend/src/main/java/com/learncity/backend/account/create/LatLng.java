package com.learncity.backend.account.create;

/**
 * Created by DJ on 2/4/2017.
 */

public class LatLng {

    Long id;
    private double latitude;
    private double longitude;

    public LatLng(){

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
