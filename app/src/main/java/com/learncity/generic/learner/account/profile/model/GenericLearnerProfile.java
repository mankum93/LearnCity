package com.learncity.generic.learner.account.profile.model;

/**
 * Created by DJ on 10/22/2016.
 */

public class GenericLearnerProfile {

    private String mName;
    private String mEmailID;
    private String mPhoneNo;
    private String mImagePath;
    private String mCurrentStatus;
    private String mPassword;

    //These are the required profile fields to create an A/C
    public GenericLearnerProfile(String name, String emailID, String phoneNo, String currentStatus, String password){
        mName = name;
        mEmailID = emailID;
        mPhoneNo = phoneNo;
        mCurrentStatus = currentStatus;
        mPassword = password;
    }

    public GenericLearnerProfile(String name, String emailID, String phoneNo, String imagePath, String currentStatus, String password) {
        this(name, emailID, phoneNo, currentStatus, password);
        mImagePath = imagePath;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getCurrentStatus() {
        return mCurrentStatus;
    }

    public void setCurrentStatus(String mCurrentStatus) {
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
}
