package com.learncity.generic.learner.account.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by DJ on 11/13/2016.
 */

public class GenericLearnerProfile implements Parcelable {

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

    public GenericLearnerProfile setState(String name,
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
    public GenericLearnerProfile(String name,
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

    protected GenericLearnerProfile(Parcel in) {
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
    public static final Creator<GenericLearnerProfile> CREATOR = new Creator<GenericLearnerProfile>() {
        @Override
        public GenericLearnerProfile createFromParcel(Parcel in) {
            return new GenericLearnerProfile(in);
        }

        @Override
        public GenericLearnerProfile[] newArray(int size) {
            return new GenericLearnerProfile[size];
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
    /**This method assigns the NULL object for name in case it is null*/
    public static String validateName(String unvalidateName){
        if(unvalidateName == null){
            return NAME_NULL;
        }
        return unvalidateName;
    }
    /**This method assigns the NULL object for Email Id in case it is null*/
    public static String validateEmailId(String unvalidatedEmailId){
        if(unvalidatedEmailId == null){
            return EMAIL_NULL;
        }
        return unvalidatedEmailId;
    }
    /**This method assigns the NULL object for Phone No in case it is null*/
    public static String validatePhoneNo(String unvalidatedPhoneNo){
        if(unvalidatedPhoneNo == null){
            return PHONE_NO_NULL;
        }
        return unvalidatedPhoneNo;
    }
    /**This method assigns the NULL object for Password in case it is null*/
    public static String validatePassword(String unvalidatedPassword){
        if(unvalidatedPassword == null){
            return PHONE_NO_NULL;
        }
        return unvalidatedPassword;
    }
    /**This method assigns the NULL object for Status in case it is null*/
    public static int validateStatus(int unvalidatedStatus){
        switch(unvalidatedStatus){
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
                return STATUS_UNDEFINED;
        }
        return unvalidatedStatus;
    }
    /**This method validates the Learner Profile object to check if the necessary fields are NOT the NULL objects*/
    public static GenericLearnerProfile validateGenericLearnerProfile(GenericLearnerProfile learnerProfile){
        //Validating the Name
        if(learnerProfile.getName().equals(NAME_NULL)){
            throw new IllegalStateException("The Name of a person can not be a NULL object");
        }
        //Validating the Email Id
        if(learnerProfile.getEmailID().equals(EMAIL_NULL)){
            throw new IllegalStateException("The Email of a person can not be a NULL object");
        }
        //Validating the Password
        if(learnerProfile.getPassword().equals(PASSWORD_NULL)){
            throw new IllegalStateException("The Password of a person can not be a NULL object");
        }
        //Validating the Phone No
        if(learnerProfile.getPhoneNo().equals(PHONE_NO_NULL)){
            throw new IllegalStateException("The Phone No of a person can not be a NULL object");
        }
        //Validating the Status
        if(learnerProfile.getCurrentStatus() == STATUS_UNDEFINED){
            throw new IllegalStateException("The Status of a person can not be a undefined");
        }
        return learnerProfile;
    }
    //--------------------------------------------------------------------------------------------------------------------

    public static class GenericLearnerProfileResponseView {
        private Integer _0;
        private Integer _1;
        private Integer _2;
        private Integer _3;
        private Integer _4;
        private Integer _5;
        private LatLngResponseView _6;

        private Integer g;

        public Integer getGlobal() {
            return g;
        }

        public void setGlobal(Integer global) {
            this.g = global;
        }

        public GenericLearnerProfileResponseView(Integer mName, Integer mEmailID, Integer mPhoneNo, Integer mDisplayPicturePath, Integer mCurrentStatus, Integer mPassword, LatLngResponseView mLastKnownGeoCoordinates) {
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

        public LatLngResponseView getmLastKnownGeoCoordinates() {
            return _6;
        }

        public void setmLastKnownGeoCoordinates(LatLngResponseView mLastKnownGeoCoordinates) {
            this._6 = mLastKnownGeoCoordinates;
        }
    }
    public static class LatLngResponseView{
        private Integer _0;
        private Integer _1;

        private Integer g;

        public Integer getGlobal() {
            return g;
        }

        public void setGlobal(Integer global) {
            this.g = global;
        }

        public LatLngResponseView(Integer latitude, Integer longitude) {
            this._0 = latitude;
            this._1 = longitude;
        }

        public Integer getLatitude() {
            return _0;
        }

        public void setLatitude(Integer latitude) {
            this._0 = latitude;
        }

        public Integer getLongitude() {
            return _1;
        }

        public void setLongitude(Integer longitude) {
            this._1 = longitude;
        }
    }
}
