package com.learncity.generic.learner.account.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DJ on 11/13/2016.
 */

public class GenericLearnerProfileParcelable implements Parcelable {

    public static int STATUS_LEARNER = 0x01;
    public static int STATUS_TUTOR = 0x02;

    private String mName;
    private String mEmailID;
    private String mPhoneNo;
    private String mImagePath;
    private int mCurrentStatus;
    private String mPassword;

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
        mImagePath = imagePath;
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

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
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
        mImagePath = in.readString();
        mCurrentStatus = in.readInt();
        mPassword = in.readString();
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
        dest.writeString(mImagePath);
        dest.writeInt(mCurrentStatus);
        dest.writeString(mPassword);
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
