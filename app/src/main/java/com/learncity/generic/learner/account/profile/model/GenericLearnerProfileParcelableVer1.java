package com.learncity.generic.learner.account.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DJ on 11/13/2016.
 */

public class GenericLearnerProfileParcelableVer1 implements Parcelable {

    /**Possible states of an App user
     * Undefined is for temporary profile building; A final validation before finalizing the profile shall
     * point out if the status is still undefined
     * */
    public static final int STATUS_UNDEFINED = 0x00;
    public static final int STATUS_LEARNER = 0x01;
    public static final int STATUS_TUTOR = 0x02;

    /**Null object pattern for Email Id */
    public static final String EMAIL_NULL = "EMAIL_NULL";

    /**Null object pattern for Phone No*/
    public static final String PHONE_NO_NULL = "PHONE_NO_NULL";

    /**Null object pattern for Name */
    public static final String NAME_NULL = "NAME_NULL";

    /**Null object pattern for Password */
    public static final String PASSWORD_NULL = "PASSWORD_NULL";


    /**Name of the Learner */
    //TODO: Model the name into FIRST NAME, MIDDLE NAME and LAST NAME
    private String mName;
    /**Email Id of the Learner */
    private String mEmailID;
    /**Phone No of the Learner */
    private String mPhoneNo;
    //TODO: Incorporate the display picture onto the server and other places like local Db
    /**For reference to the profile picture locally */
    private String mDisplayPicturePath;
    /**Status of the person/App user - Must be out of {STATUS_LEARNER, STATUS_TUTOR, STATUS_UNDEFINED}*/
    private int mCurrentStatus;
    /**Password set by the Learner*/
    private String mPassword;

    /**This will act as a reference to the last known location managed on the server and Local Db*/
    //TODO: Incorporate the location wherever appropriate
    //NOTE: Google Play location lib required for the following class
    private LatLng mLastKnownGeoCoordinates;

    private Builder builder;

    public Builder getLearnerProfileBuilder(){
        //Object was initially constructed from Parcel or the single public constructor
        if(builder == null){
            return new Builder(getName(), getEmailID(), getPhoneNo(), getCurrentStatus(), getPassword());
        }
        return builder;
    }

    public static class Builder {
        private String name;
        private String emailID;
        private String phoneNo;
        private int currentStatus;
        private String password;
        private String imagePath;
        private LatLng geoCoordinates;
        private GenericLearnerProfileParcelableVer1 learnerProfile;

        public Builder(String name,
                       String emailID,
                       String phoneNo,
                       int currentStatus,
                       String password){
            this.name = name;
            this.emailID = emailID;
            this.phoneNo = phoneNo;
            this.currentStatus = currentStatus;
            this.password = password;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withEmailID(String emailID) {
            this.emailID = emailID;
            return this;
        }

        public Builder withPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
            return this;
        }

        public Builder withCurrentStatus(int currentStatus) {
            this.currentStatus = currentStatus;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder withGeoCoordinates(LatLng geoCoordinates) {
            this.geoCoordinates = geoCoordinates;
            return this;
        }

        public GenericLearnerProfileParcelableVer1 build() {
            //First, check if there is an already existing instance built through this Builder
            //If there is, then continue construction of "that" object
            if(learnerProfile != null){
                //setState() has input validation inbuilt
                return learnerProfile.setState(name, emailID, phoneNo,
                        currentStatus, password, imagePath, geoCoordinates);
            }
            //Constructor has input invalidation inbuilt
            learnerProfile = new GenericLearnerProfileParcelableVer1(name, emailID, phoneNo,
                    currentStatus, password, imagePath, geoCoordinates);

            learnerProfile.builder = this;
            return learnerProfile;
        }

        public GenericLearnerProfileParcelableVer1 getBuiltObject(){
            if(learnerProfile == null){
                throw new RuntimeException("Object has not been built; call Builder.build() to build the object");
            }

            return learnerProfile;
        }
    }

    public GenericLearnerProfileParcelableVer1 setState(String name,
                                                        String emailID,
                                                        String phoneNo,
                                                        int currentStatus,
                                                        String password,
                                                        String imagePath,
                                                        LatLng lastKnownGeoCoordinates){
        validateInput(name, emailID, phoneNo, currentStatus, password);
        mName = name;
        mEmailID = emailID;
        mPhoneNo = phoneNo;
        mCurrentStatus = currentStatus;
        mPassword = password;
        mDisplayPicturePath = imagePath;
        mLastKnownGeoCoordinates = lastKnownGeoCoordinates;

        return this;
    }

    //Constructors-------------------------------------------------------------------------------------------------------

    //These are the required profile fields to create an A/C
    public GenericLearnerProfileParcelableVer1(String name,
                                               String emailID,
                                               String phoneNo,
                                               int currentStatus,
                                               String password,
                                               String imagePath,
                                               LatLng lastKnownGeoCoordinates){
        validateInput(name, emailID, phoneNo, currentStatus, password);
        mName = name;
        mEmailID = emailID;
        mPhoneNo = phoneNo;
        mCurrentStatus = currentStatus;
        mPassword = password;
        mDisplayPicturePath = imagePath;
        mLastKnownGeoCoordinates = lastKnownGeoCoordinates;
    }

    protected GenericLearnerProfileParcelableVer1(Parcel in) {
        mName = in.readString();
        mEmailID = in.readString();
        mPhoneNo = in.readString();
        mDisplayPicturePath = in.readString();
        mCurrentStatus = in.readInt();
        mPassword = in.readString();
        mLastKnownGeoCoordinates = in.readParcelable(LatLng.class.getClassLoader());
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

    //---------------------------------------------------------------------------------------------------------------


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
    public static final Creator<GenericLearnerProfileParcelableVer1> CREATOR = new Creator<GenericLearnerProfileParcelableVer1>() {
        @Override
        public GenericLearnerProfileParcelableVer1 createFromParcel(Parcel in) {
            return new GenericLearnerProfileParcelableVer1(in);
        }

        @Override
        public GenericLearnerProfileParcelableVer1[] newArray(int size) {
            return new GenericLearnerProfileParcelableVer1[size];
        }
    };

    //------------------------------------------------------------------------------------------------------------------
    /**Method invalidates the compulsory input for a learner profile
     * @param name: Name of the Learner
     * @param emailID: Email Id of the Learner
     * @param phoneNo: Phone No of the Learner
     * @param currentStatus: Current Status of the learner from {STATUS_LEARNER, STATUS_TUTOR, STATUS_UNDEFINED}
     * @param password: Password of the Learner
     * */
    private void validateInput(String name,
                               String emailID,
                               String phoneNo,
                               int currentStatus,
                               String password){
        if(name == null){
            throw new IllegalStateException("Name is null");
        }
        if(emailID == null){
            throw new IllegalStateException("EmailId is null");
        }
        if(phoneNo == null){
            throw new IllegalStateException("Phone No is null");
        }
        if(password == null){
            throw new IllegalStateException("Password is null");
        }

        switch(currentStatus){
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
                throw new IllegalStateException("Tutor's status is "+ currentStatus + "(invalid); Must be out of the following:" +
                        "{STATUS_LEARNER, STATUS_TUTOR, STATUS_UNDEFINED}");
        }
    }
}
