package com.learncity.backend.account.create;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by DJ on 11/13/2016.
 */

@Entity
public class GenericLearnerProfileVer1 {

    /**Possible states of an App user
     * Undefined is for temporary profile building; A final validation before finalizing the profile shall
     * point out if the status is still undefined
     * */
    public static final int STATUS_UNDEFINED = 0x00;
    public static final int STATUS_LEARNER = 0x01;
    public static final int STATUS_TUTOR = 0x02;


    /**Name of the Learner */
    //TODO: Model the name into FIRST NAME, MIDDLE NAME and LAST NAME
    private String mName;
    /**Email Id of the Learner */
    @Id
    private String mEmailID;
    /**Phone No of the Learner */
    private String mPhoneNo;
    //TODO: Incorporate the display picture onto the server and other places like local Db
    /**For reference to the profile picture on the server */
    private String mDisplayPicturePath;
    /**Status of the person/App user - Must be out of {STATUS_LEARNER, STATUS_TUTOR, STATUS_UNDEFINED}*/
    private int mCurrentStatus;
    /**Password set by the Learner*/
    private String mPassword;

    /**This will act as a reference to the last known location managed on the server and Local Db*/
    //TODO: Incorporate the location wherever appropriate
    //NOTE: Google Play location lib required for the following class
    private LatLng mLastKnownGeoCoordinates;

    /**A unique integer Id associated with every account on the server. This ID will start the directory for user-content */
    private int mUserId;

    //For serialization while storing to the Db
    public GenericLearnerProfileVer1(){

    }
    @Override
    public String toString(){
        return new StringBuilder("Name: ")
                .append(mName)
                .append("Email ID: ")
                .append(mEmailID).append("\n")
                .append("Phone No ")
                .append(mPhoneNo).append("\n")
                .append("Password ")
                .append(mPassword).append("\n")
                .append("Current status ")
                .append(mCurrentStatus).append("\n")
                .append("Display pic path ")
                .append(mDisplayPicturePath)
                .toString();
    }

    //Getters and Setters------------------------------------------------------------------------------------------------

    public LatLng getLastKnownGeoCoordinates() {
        return mLastKnownGeoCoordinates;
    }

    public void setLastKnownGeoCoordinates(LatLng mLastKnownGeoCoordinates) {
        this.mLastKnownGeoCoordinates = mLastKnownGeoCoordinates;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {

        if(mPassword == null){
            throw new IllegalStateException("Password is null");
        }
        this.mPassword = mPassword;
    }

    public int getCurrentStatus() {
        return mCurrentStatus;
    }

    public void setCurrentStatus(int mCurrentStatus) {
        switch(mCurrentStatus){
            case STATUS_LEARNER:
                //Check next
                break;
            case STATUS_TUTOR:
                //Check next
                break;
            case STATUS_UNDEFINED:
                //Check next
                break;
            default:
                //Some INVALID status
                throw new IllegalStateException("Tutor's status is "+ mCurrentStatus + "(invalid); Must be out of the following:" +
                        "{STATUS_LEARNER, STATUS_TUTOR, STATUS_UNDEFINED}");
        }
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
        if(mName == null){
            throw new IllegalStateException("Name is null");
        }

        this.mName = mName;
    }

    public String getEmailID() {
        return mEmailID;
    }

    public void setEmailID(String mEmailID) {

        if(mEmailID == null){
            throw new IllegalStateException("EmailId is null");
        }

        this.mEmailID = mEmailID;
    }

    public String getPhoneNo() {
        return mPhoneNo;
    }

    public void setPhoneNo(String mPhoneNo) {

        if(mPhoneNo == null){
            throw new IllegalStateException("Phone No is null");
        }
        this.mPhoneNo = mPhoneNo;
    }
}
