package com.learncity.backend.common.account.create;

/**
 * Created by DJ on 3/6/2017.
 */

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.learncity.backend.util.IdUtils;

import java.io.Serializable;

import java.util.logging.Logger;

/**Class encapsulating Profile, and other details/computations possibly from the _1*/
@Entity
public class Account implements Serializable{

    private static final Logger logger = Logger.getLogger(Account.class.getSimpleName());

    /**Email Id of the User */
    @Id @Index private String mEmailID;

    /**Important to have the status indexed as this shall be used during a query for a particular user type*/
    @Index private Integer accountStatus;

    // Profile info.
    private GenericLearnerProfileVer1 profile;

    // Tutor's location info. - this would be computed from {Latitude, Longitude} which shall be available
    // from the _1 info. Also, this holds a reference to the LatLng{Latitude, Longitude} from the _1.
    private LocationInfo locationInfo;

    /**Every user has a device token corresponding to the current App instance(on the device).
     * This token is for use by Firebase Connection server to send the message to the
     * device.*/
    private String userDeviceFirebaseToken;

    /** 'UUID type 5' based on Email ID of the user(UUID impl is based on RFC-4122).
     * See <a href="https://github.com/cowtowncoder/java-uuid-generator">Project Repo</a>
     * */
    @Index private String emailBasedUUID;

    public Account(GenericLearnerProfileVer1 profile) {
        this(profile, null);
    }

    public Account(GenericLearnerProfileVer1 profile, LocationInfo locationInfo) {
        if(profile == null){
            throw new NullPointerException("Profile can't be null");
        }
        this.profile = profile;
        this.locationInfo = locationInfo;

        mEmailID = profile.getEmailID();

        // For every new Account instance construction, initialize the
        // UUID based on the provided Email ID. If this instance comes in
        // contact with an existing instance corresponding to the same Email ID(or Account),
        // the UUIDs in both the instances shall match.
        emailBasedUUID = IdUtils.getType5UUID(mEmailID);

        this.accountStatus = profile.getCurrentStatus();
    }

    protected Account(){

    }

    public String getmEmailID() {
        return mEmailID;
    }

    public Integer getAccountStatus() {
        return accountStatus;
    }

    public GenericLearnerProfileVer1 getProfile() {
        return profile;
    }

    public void setProfile(GenericLearnerProfileVer1 profile) {
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

    public String getEmailBasedUUID() {
        return emailBasedUUID;
    }

    public String getUserDeviceFirebaseToken() {
        return userDeviceFirebaseToken;
    }

    public void setUserDeviceFirebaseToken(String userDeviceFirebaseToken) {
        this.userDeviceFirebaseToken = userDeviceFirebaseToken;
    }

    //-------------------------------------------------------------------------------------------------------------------
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

    //---------------------------------------------------------------------------------------------------------------------
    public static class AccountResponseView{

        private GenericLearnerProfileVer1.GenericLearnerProfileResponseView _1;
        private Integer _2;

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

        public AccountResponseView(GenericLearnerProfileVer1.GenericLearnerProfileResponseView profile, Integer locationInfo) {
            this._1 = profile;
            this._2 = locationInfo;
        }

        public AccountResponseView(){

        }

        public GenericLearnerProfileVer1.GenericLearnerProfileResponseView getProfile() {
            return _1;
        }

        public void setProfile(GenericLearnerProfileVer1.GenericLearnerProfileResponseView profile) {
            this._1 = profile;
        }

        public Integer getLocationInfo() {
            return _2;
        }

        public void setLocationInfo(Integer locationInfo) {
            this._2 = locationInfo;
        }

        public static Account normalize(AccountResponseView spec, Account acc){
            if(spec == null){
                return null;
            }

            Integer i = spec.getNil();
            if(i != null){
                if(i.intValue() == 1){
                    return null;
                }
            }

            //Check for the Global switch
            if(spec.getGlobal() == null){
                if(spec.getLocationInfo() == null){
                    acc.setLocationInfo(null);
                }
            }
            return acc;

        }
    }
}
