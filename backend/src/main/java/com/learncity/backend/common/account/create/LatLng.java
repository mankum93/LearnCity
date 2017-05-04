package com.learncity.backend.common.account.create;

import java.io.Serializable;

/**
 * Created by DJ on 2/4/2017.
 */


public class LatLng implements Serializable {

    private double latitude;
    private double longitude;

    public LatLng(){

    }

    @Override
    public String toString(){
        return new StringBuilder("Latitude: " + latitude + "\n")
                .append("Longitude: " + longitude)
                .toString();
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

    //-----------------------------------------------------------------------------------------------------------------------
    public static class LatLngResponseView{
        private Integer _0;
        private Integer _1;

        private Integer nil;

        public Integer getNil() {
            return nil;
        }

        public void setNil(Integer nil) {
            this.nil = nil;
        }

        private Integer g;

        public Integer getGlobal() {
            return g;
        }

        public void setGlobal(Integer global) {
            this.g = global;
        }

        public LatLngResponseView() {
        }

        public LatLngResponseView(Integer latitude, Integer longitude) {
            this._0 = latitude;
            this._1 = longitude;
        }

        public Integer getLatitude() {
            return _0;
        }

        public void setLatitude(Integer latitude) {
            this._0 = latitude;
        }

        public Integer getLongitude() {
            return _1;
        }

        public void setLongitude(Integer longitude) {
            this._1 = longitude;
        }

        public static LatLng normalize(LatLngResponseView latLngSpec, LatLng latlng){

            if(latLngSpec == null){
                return null;
            }

            Integer i = latLngSpec.getNil();
            if(i != null){
                if(i.intValue() == 1){
                    return null;
                }
            }
            return latlng;
        }
    }
}
