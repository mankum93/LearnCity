package com.learncity.backend.common.account.create;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

/**
 * Created by DJ on 11/13/2016.
 */


@Entity
public class GenericLearnerProfileVer1 implements Serializable{

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

        /*if(mPassword == null){
            throw new IllegalStateException("Password is null");
        }*/
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
        /*if(mName == null){
            throw new IllegalStateException("Name is null");
        }*/

        this.mName = mName;
    }

    public String getEmailID() {
        return mEmailID;
    }

    public void setEmailID(String mEmailID) {

        /*if(mEmailID == null){
            throw new IllegalStateException("EmailId is null");
        }*/

        this.mEmailID = mEmailID;
    }

    public String getPhoneNo() {
        return mPhoneNo;
    }

    public void setPhoneNo(String mPhoneNo) {

        /*if(mPhoneNo == null){
            throw new IllegalStateException("Phone No is null");
        }*/
        this.mPhoneNo = mPhoneNo;
    }

    //--------------------------------------------------------------------------------------------------------------------

    public static class GenericLearnerProfileResponseView {
        private Integer _0;
        private Integer _1;
        private Integer _2;
        private Integer _3;
        private Integer _4;
        private Integer _5;
        private LatLng.LatLngResponseView _6;

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

        public GenericLearnerProfileResponseView() {
        }

        public GenericLearnerProfileResponseView(Integer mName, Integer mEmailID, Integer mPhoneNo, Integer mDisplayPicturePath, Integer mCurrentStatus, Integer mPassword, LatLng.LatLngResponseView mLastKnownGeoCoordinates) {
            this._0 = mName;
            this._1 = mEmailID;
            this._2 = mPhoneNo;
            this._3 = mDisplayPicturePath;
            this._4 = mCurrentStatus;
            this._5 = mPassword;
            this._6 = mLastKnownGeoCoordinates;
        }

        public Integer getmName() {
            return _0;
        }

        public void setmName(Integer mName) {
            this._0 = mName;
        }

        public Integer getmEmailID() {
            return _1;
        }

        public void setmEmailID(Integer mEmailID) {
            this._1 = mEmailID;
        }

        public Integer getmPhoneNo() {
            return _2;
        }

        public void setmPhoneNo(Integer mPhoneNo) {
            this._2 = mPhoneNo;
        }

        public Integer getmDisplayPicturePath() {
            return _3;
        }

        public void setmDisplayPicturePath(Integer mDisplayPicturePath) {
            this._3 = mDisplayPicturePath;
        }

        public Integer getmCurrentStatus() {
            return _4;
        }

        public void setmCurrentStatus(Integer mCurrentStatus) {
            this._4 = mCurrentStatus;
        }

        public Integer getmPassword() {
            return _5;
        }

        public void setmPassword(Integer mPassword) {
            this._5 = mPassword;
        }

        public LatLng.LatLngResponseView getmLastKnownGeoCoordinates() {
            return _6;
        }

        public void setmLastKnownGeoCoordinates(LatLng.LatLngResponseView mLastKnownGeoCoordinates) {
            this._6 = mLastKnownGeoCoordinates;
        }

        public static GenericLearnerProfileVer1 normalize(GenericLearnerProfileResponseView spec, GenericLearnerProfileVer1 profile){

            if(spec == null){
                return null;
            }

            //All the base class fields have been asked to be null - WTH?
            Integer i = spec.getNil();
            if(i != null){
                if(i.intValue() == 1){
                    return null;
                }
            }
            if(spec.getNil() == null){
                if(spec.getmName() == null){
                    profile.setName(null);
                }
                if(spec.getmEmailID() == null){
                    profile.setEmailID(null);
                }
                if(spec.getmPhoneNo() == null){
                    profile.setPhoneNo(null);
                }
                if(spec.getmPassword() == null){
                    profile.setPassword(null);
                }
                if(spec.getmDisplayPicturePath() == null){
                    profile.setDisplayPicturePath(null);
                }
                profile.setLastKnownGeoCoordinates(LatLng.LatLngResponseView.normalize(spec.getmLastKnownGeoCoordinates(), profile.getLastKnownGeoCoordinates()));
            }

            return profile;
        }
    }
}
