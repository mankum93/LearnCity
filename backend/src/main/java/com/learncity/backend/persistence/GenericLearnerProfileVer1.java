package com.learncity.backend.persistence;

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
        private GenericLearnerProfileVer1 learnerProfile;

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

        public GenericLearnerProfileVer1 build() {
            //First, check if there is an already existing instance built through this Builder
            //If there is, then continue construction of "that" object
            if(learnerProfile != null){
                //setState() has input validation inbuilt
                return learnerProfile.setState(name, emailID, phoneNo,
                        currentStatus, password, imagePath, geoCoordinates);
            }
            //Constructor has input invalidation inbuilt
            learnerProfile = new GenericLearnerProfileVer1(name, emailID, phoneNo,
                    currentStatus, password, imagePath, geoCoordinates);

            learnerProfile.builder = this;
            return learnerProfile;
        }

        public GenericLearnerProfileVer1 getBuiltObject(){
            if(learnerProfile == null){
                throw new RuntimeException("Object has not been built; call Builder.build() to build the object");
            }

            return learnerProfile;
        }
    }

    public GenericLearnerProfileVer1 setState(String name,
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
    public GenericLearnerProfileVer1(String name,
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
    //For serialization while storing to the Db
    public GenericLearnerProfileVer1(){

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
    public static GenericLearnerProfileVer1 validateGenericLearnerProfile(GenericLearnerProfileVer1 learnerProfile){
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
}
