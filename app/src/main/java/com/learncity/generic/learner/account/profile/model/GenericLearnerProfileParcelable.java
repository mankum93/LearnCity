package com.learncity.generic.learner.account.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DJ on 11/13/2016.
 */

public class GenericLearnerProfileParcelable implements Parcelable {

    public static final int STATUS_UNDEFINED = 0x00;
    public static final int STATUS_LEARNER = 0x01;
    public static final int STATUS_TUTOR = 0x02;

    private String mName;
    private String mEmailID;
    private String mPhoneNo;
    //TODO: Incorporate the display picture onto the server and other places like local Db
    //For reference to the profile picture locally
    private String mDisplayPicturePath;
    private int mCurrentStatus;
    private String mPassword;

    //TODO: Incorporate the location wherever appropriate
    //NOTE: Google Play location lib required for the following class
    private LatLng mLastKnownGeoCoordinates;

    //Constructors-------------------------------------------------------------------------------------------------------

    //These are the required profile fields to create an A/C
    public GenericLearnerProfileParcelable(String name, String emailID, String phoneNo, int currentStatus, String password){
        mName = name;
        mEmailID = emailID;
        mPhoneNo = phoneNo;
        mCurrentStatus = currentStatus;
        mPassword = password;
    }

    public GenericLearnerProfileParcelable(String name, String emailID, String phoneNo, String imagePath, int currentStatus, String password) {
        this(name, emailID, phoneNo, currentStatus, password);
        mDisplayPicturePath = imagePath;
    }
    public GenericLearnerProfileParcelable(String name, String emailID, String phoneNo, String imagePath, int currentStatus, String password, LatLng geoCoordinates) {
        this(name, emailID, phoneNo, currentStatus, password);
        mDisplayPicturePath = imagePath;
        mLastKnownGeoCoordinates = geoCoordinates;
    }

    //Getters and Setters------------------------------------------------------------------------------------------------


    public LatLng getmLastKnownGeoCoordinates() {
        return mLastKnownGeoCoordinates;
    }

    public void setmLastKnownGeoCoordinates(LatLng mLastKnownGeoCoordinates) {
        this.mLastKnownGeoCoordinates = mLastKnownGeoCoordinates;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public int getCurrentStatus() {
        return mCurrentStatus;
    }

    public void setCurrentStatus(int mCurrentStatus) {
        this.mCurrentStatus = mCurrentStatus;
    }

    public String getDisplayPicturePath() {
        return mDisplayPicturePath;
    }

    public void setDisplayPicturePath(String mImagePath) {
        this.mDisplayPicturePath = mImagePath;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getEmailID() {
        return mEmailID;
    }

    public void setEmailID(String mEmailID) {
        this.mEmailID = mEmailID;
    }

    public String getPhoneNo() {
        return mPhoneNo;
    }

    public void setPhoneNo(String mPhoneNo) {
        this.mPhoneNo = mPhoneNo;
    }

    protected GenericLearnerProfileParcelable(Parcel in) {
        mName = in.readString();
        mEmailID = in.readString();
        mPhoneNo = in.readString();
        mDisplayPicturePath = in.readString();
        mCurrentStatus = in.readInt();
        mPassword = in.readString();
        mLastKnownGeoCoordinates = in.readParcelable(LatLng.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mEmailID);
        dest.writeString(mPhoneNo);
        dest.writeString(mDisplayPicturePath);
        dest.writeInt(mCurrentStatus);
        dest.writeString(mPassword);
        dest.writeParcelable(mLastKnownGeoCoordinates, flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GenericLearnerProfileParcelable> CREATOR = new Parcelable.Creator<GenericLearnerProfileParcelable>() {
        @Override
        public GenericLearnerProfileParcelable createFromParcel(Parcel in) {
            return new GenericLearnerProfileParcelable(in);
        }

        @Override
        public GenericLearnerProfileParcelable[] newArray(int size) {
            return new GenericLearnerProfileParcelable[size];
        }
    };
}
