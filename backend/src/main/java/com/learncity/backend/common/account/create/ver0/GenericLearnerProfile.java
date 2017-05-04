package com.learncity.backend.common.account.create.ver0;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by DJ on 10/22/2016.
 */

@Entity
public class GenericLearnerProfile {

    //Profile fields
    private String mName;
    @Id
    private String mEmailID;
    private String mPhoneNo;
    private String mDisplayPicturePath;
    //A unique integer Id associated with every account on the server. This ID will start the directory for user-content
    private int mUserId;
    private int mCurrentStatus;
    private String mPassword;

    //These are the required profile fields to create an A/C
    public GenericLearnerProfile(String name, String emailID, String phoneNo, int currentStatus, String password){
        mName = name;
        mEmailID = emailID;
        mPhoneNo = phoneNo;
        mCurrentStatus = currentStatus;
        mPassword = password;
    }

    //Default constructor required for persistence(serialization)
    public GenericLearnerProfile(){

    }

    public GenericLearnerProfile(String name, String emailID, String phoneNo, String imagePath, int currentStatus, String password) {
        this(name, emailID, phoneNo, currentStatus, password);
        mDisplayPicturePath = imagePath;
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
        return mDisplayPicturePath;
    }

    public void setImagePath(String mImagePath) {
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
}
