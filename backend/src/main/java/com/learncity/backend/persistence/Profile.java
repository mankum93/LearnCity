package com.learncity.backend.persistence;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by DJ on 10/22/2016.
 */

@Entity
public class Profile {

    private String mName;
    @Id
    private String mEmailID;
    private String mPhoneNo;
    //Should the image path be part of profile to be persisted. It could instead be an Image resource itself.
    //Notice, this could be point of distinction between the Profile entity and MyProfile model object. Model object
    //could just save the path to the image on the disc to load faster but the entity could facilitate storage of the
    //actual profile pic.
    //TODO: Handle the above comment appropriately
    private String mImagePath;
    private String mCurrentStatus;
    private String mPassword;

    public Profile(String mName, String mEmailID, String mPhoneNo, String mImagePath, String mCurrentStatus, String mPassword) {
        this.mName = mName;
        this.mEmailID = mEmailID;
        this.mPhoneNo = mPhoneNo;
        this.mImagePath = mImagePath;
        this.mCurrentStatus = mCurrentStatus;
        this.mPassword = mPassword;
    }

    public Profile(){

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
