package com.learncity.generic.learner.account;

/**
 * Created by DJ on 3/6/2017.
 */


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.fasterxml.uuid.UUIDType;
import com.fasterxml.uuid.impl.NameBasedGenerator;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**Class encapsulating Profile, and other details/computations of/from the fields*/
public class Account implements Parcelable {

    private static final String TAG = Account.class.getSimpleName();

    /**Email Id of the User */
    private String mEmailID;

    /**Important to have the status indexed as this shall be used during a query for a particular user type*/
    private Integer accountStatus;

    // Profile info.
    private GenericLearnerProfile profile;

    // Tutor's location info. - this would be computed from {Latitude, Longitude}
    private LocationInfo locationInfo;

    /** 'UUID type 5' based on Email ID of the user(UUID impl is based on RFC-4122).
     * See <a href="https://github.com/cowtowncoder/java-uuid-generator">Project Repo</a>
     * */
    private UUID emailBasedUUID;

    public Account(GenericLearnerProfile profile) {
        this(profile, null);
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

    public UUID getEmailBasedUUID() {
        return emailBasedUUID;
    }

    public void setEmailBasedUUID(UUID emailBasedUUID) {
        this.emailBasedUUID = emailBasedUUID;
    }

    //----------------------------------------------------------------------------------------------------------------
    public static class LocationInfo implements Parcelable {
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

        protected LocationInfo(Parcel in) {
            shortFormattedAddress = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(shortFormattedAddress);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<LocationInfo> CREATOR = new Parcelable.Creator<LocationInfo>() {
            @Override
            public LocationInfo createFromParcel(Parcel in) {
                return new LocationInfo(in);
            }

            @Override
            public LocationInfo[] newArray(int size) {
                return new LocationInfo[size];
            }
        };
    }

    protected Account(Parcel in) {
        mEmailID = in.readString();
        accountStatus = in.readByte() == 0x00 ? null : in.readInt();
        profile = (GenericLearnerProfile) in.readValue(GenericLearnerProfile.class.getClassLoader());
        locationInfo = (LocationInfo) in.readValue(LocationInfo.class.getClassLoader());
        emailBasedUUID = UUID.fromString(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEmailID);
        if (accountStatus == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(accountStatus);
        }
        dest.writeValue(profile);
        dest.writeValue(locationInfo);
        dest.writeString(emailBasedUUID.toString());
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

}