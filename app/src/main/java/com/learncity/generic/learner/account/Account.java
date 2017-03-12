package com.learncity.generic.learner.account;

/**
 * Created by DJ on 3/6/2017.
 */


import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;

/**Class encapsulating Profile, and other details/computations possibly from the _1*/
public class Account {

    /**Email Id of the User */
    private String mEmailID;

    /**Important to have the status indexed as this shall be used during a query for a particular user type*/
    private Integer accountStatus;

    // Profile info.
    private GenericLearnerProfile profile;

    // Tutor's location info. - this would be computed from {Latitude, Longitude} which shall be available
    // from the _1 info. Also, this holds a reference to the LatLng{Latitude, Longitude} from the _1.
    private LocationInfo locationInfo;

    public Account(GenericLearnerProfile profile) {
        if(profile == null){
            throw new NullPointerException("Profile can't be null");
        }
        this.profile = profile;
        mEmailID = profile.getEmailID();
        // Auto boxing
        this.accountStatus = profile.getCurrentStatus();
    }

    public Account(GenericLearnerProfile profile, LocationInfo locationInfo) {
        if(profile == null){
            throw new NullPointerException("Profile can't be null");
        }
        this.profile = profile;
        this.locationInfo = locationInfo;

        mEmailID = profile.getEmailID();

        this.accountStatus = profile.getCurrentStatus();
    }

    private Account(){

    }

    public GenericLearnerProfile getProfile() {
        return profile;
    }

    public void setProfile(GenericLearnerProfile profile) {
        if(profile == null){
            throw new NullPointerException("Profile can't be null");
        }
        this.profile = profile;
        mEmailID = profile.getEmailID();
    }

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(LocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }

    public static class LocationInfo{
        private String shortFormattedAddress;

        public LocationInfo(String shortFormattedAddress) {
            this.shortFormattedAddress = shortFormattedAddress;
        }

        public String getShortFormattedAddress() {
            return shortFormattedAddress;
        }

        public void setShortFormattedAddress(String shortFormattedAddress) {
            this.shortFormattedAddress = shortFormattedAddress;
        }
    }
}
