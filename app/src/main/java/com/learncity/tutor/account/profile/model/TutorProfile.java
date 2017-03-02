package com.learncity.tutor.account.profile.model;

import android.os.Parcel;

import com.google.android.gms.maps.model.LatLng;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.tutor.account.profile.model.occupation.Occupation;
import com.learncity.tutor.account.profile.model.qualification.educational.EducationalQualification;

/**
 * Created by DJ on 10/22/2016.
 */

public class TutorProfile extends GenericLearnerProfile {

    /**Null object pattern for Tutor Types */
    public static final String[] TUTOR_TYPES_NULL = {"TUTOR_TYPES_NULL"};

    /**Null object pattern for Disciplines */
    public static final String[] DISCIPLINES_NULL = {"DISCIPLINES_NULL"};


    /** Educational qualification proof compulsory before start of teaching */
    private EducationalQualification[] educationalQualifications;

    /** Optional requirement */
    private Occupation occupation;

    /**Type of Tutor that I am; Ex: College Professor, School Teacher, Undergraduate, etc.*/
    //List of all available types: R.array.type_of_tutor
    private String[] tutorTypes;

    /**The _3/subjects that the tutor can teach*/
    //List of all available _3/subjects: R.array.list_of_disciplines
    private String[] disciplines;

    /**Rating is a average of all 5 point scale scores given by the students
     * Minimum _4 available: 1
     * Maximum _4 available: 5
     * Initial _4(unrated): 0
     * NOTE: In case of _4 0, status should be reflected as "Unrated"*/
    private int rating = 0;     //Initial: Unrated

    private Credits teachingCredits;

    private Builder builder;


    @Override
    public String toString(){
        return new StringBuilder(super.toString())
                .append("Educational Qualifications: ")
                .append(educationalQualifications).append("\n")
                .append("Occupation: ")
                .append(occupation).append("\n")
                .append("Tutor types: ")
                .append(tutorTypes).append("\n")
                .append("Disciplines taught: ")
                .append(disciplines).append("\n")
                .append("Rating: ")
                .append(rating).append("\n")
                .append("Teaching credits")
                .append(teachingCredits).append("\n")
                .toString();
    }

    //Getters and Setters-----------------------------------------------------------------------------------------

    public EducationalQualification[] getEducationalQualifications() {
        return educationalQualifications;
    }

    public void setEducationalQualifications(EducationalQualification[] educationalQualifications) {
        this.educationalQualifications = educationalQualifications;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    public void setOccupation(Occupation occupation) {
        this.occupation = occupation;
    }

    public String[] getTutorTypes() {
        return tutorTypes;
    }

    public void setTutorTypes(String[] tutorTypes) {
        if(tutorTypes == null){
            throw new IllegalStateException("Tutor Types are null");
        }
        this.tutorTypes = tutorTypes;
    }

    public String[] getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(String[] disciplines) {
        if(disciplines == null){
            throw new IllegalStateException("Disciplines are null");
        }
        this.disciplines = disciplines;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Credits getTeachingCredits() {
        return teachingCredits;
    }

    public void setTeachingCredits(Credits teachingCredits) {
        this.teachingCredits = teachingCredits;
    }

    /**
     * returns builder for the TutorProfile
     * If there is a valid builder already used in construction of this object, return it.
     * Or, if the builder is null which implies the object was first constructed from Parcel
     * or single constructor in which case, a new builder shall be created and returned.
     * */
    public Builder getTutorProfileBuilder(){
        //Object was initially constructed from Parcel or the single public constructor
        if(builder == null){
            return new Builder(getName(), getEmailID(), getPhoneNo(), getCurrentStatus(), getPassword());
        }
        return builder;
    }

    public static class Builder {

        private TutorProfile tutorProfile;

        private String name;
        private String emailID;
        private String phoneNo;
        private int currentStatus;
        private String password;
        private String imagePath;
        private LatLng lastKnownGeoCoordinates;
        private EducationalQualification[] educationalQualifications;
        private Occupation occupation;
        private String[] tutorTypes;
        private String[] disciplines;
        private int rating = 0;
        private Credits teachingCredits;

        public Builder(String name, String emailID, String phoneNo, int currentStatus, String password){
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

        public Builder withEducationalQualifications(EducationalQualification[] educationalQualifications) {
            this.educationalQualifications = educationalQualifications;
            return this;
        }

        public Builder withOccupation(Occupation occupation) {
            this.occupation = occupation;
            return this;
        }

        public Builder withTutorTypes(String[] tutorTypes){
            this.tutorTypes = tutorTypes;
            return this;
        }

        public Builder withDisciplines(String[] disciplines){
            this.disciplines = disciplines;
            return this;
        }

        public Builder withRating(int rating){
            this.rating = rating;
            return this;
        }

        public Builder withTeachingCredits(Credits credits){
            this.teachingCredits = credits;
            return this;
        }

        public Builder withGeoCoordinates(LatLng geoCoordinates) {
            this.lastKnownGeoCoordinates = geoCoordinates;
            return this;
        }

        public TutorProfile build() {
            //First, check if there is an already existing instance built through this Builder
            //If there is, then continue construction of "that" object
            if(tutorProfile != null){
                return tutorProfile.setState(name, emailID, phoneNo,
                        currentStatus, password, imagePath, lastKnownGeoCoordinates, educationalQualifications,
                        occupation, tutorTypes, disciplines, rating, teachingCredits);
            }
            tutorProfile = new TutorProfile(name, emailID, phoneNo,
                    currentStatus, password, imagePath, lastKnownGeoCoordinates, educationalQualifications,
                    occupation, tutorTypes, disciplines, rating, teachingCredits);

            tutorProfile.builder = this;
            return tutorProfile;
        }

        public TutorProfile getBuiltObject(){
            if(tutorProfile == null){
                throw new RuntimeException("Object has not been built; call Builder.build() to build the object");
            }
            return tutorProfile;
        }
    }

    public TutorProfile setState(String name,
                                 String emailID,
                                 String phoneNo,
                                 int currentStatus,
                                 String password,
                                 String imagePath,
                                 LatLng lastKnownGeoCoordinates,
                                 EducationalQualification[] educationalQualifications,
                                 Occupation occupation,
                                 String[] tutorTypes,
                                 String[] disciplines,
                                 int rating,
                                 Credits credits) {
        super.setState(name, emailID, phoneNo, currentStatus, password, imagePath, lastKnownGeoCoordinates);
        validateInput(tutorTypes, disciplines);
        this.educationalQualifications = educationalQualifications;
        this.occupation = occupation;
        this. tutorTypes = tutorTypes;
        this.disciplines = disciplines;
        this.rating = rating;
        this.teachingCredits = credits;
        return this;
    }

    //Start of Constructors----------------------------------------------------------------------------------------------

    public TutorProfile(String name,
                        String emailID,
                        String phoneNo,
                        int currentStatus,
                        String password,
                        String imagePath,
                        LatLng lastKnownGeoCoordinates,
                        EducationalQualification[] educationalQualifications,
                        Occupation occupation,
                        String[] tutorTypes,
                        String[] disciplines,
                        int rating,
                        Credits credits) {
        super(name, emailID, phoneNo, currentStatus, password, imagePath, lastKnownGeoCoordinates);
        validateInput(tutorTypes, disciplines);
        this.educationalQualifications = educationalQualifications;
        this.occupation = occupation;
        this. tutorTypes = tutorTypes;
        this.disciplines = disciplines;
        this.rating = rating;
        this.teachingCredits = credits;
    }

    public TutorProfile(Parcel in) {
        super(in);
        this.educationalQualifications = (EducationalQualification[])in.readParcelableArray(EducationalQualification[].class.getClassLoader());
        this.tutorTypes = (String[])in.readArray(String.class.getClassLoader());
        this.disciplines = (String[])in.readArray(String.class.getClassLoader());
        this.occupation = in.readParcelable(Occupation.class.getClassLoader());
        this.rating = in.readInt();
        this.teachingCredits = in.readParcelable(Credits.class.getClassLoader());
    }

    //End of constructors---------------------------------------------------------------------------------------------------

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeArray(educationalQualifications);
        dest.writeStringArray(tutorTypes);
        dest.writeStringArray(disciplines);
        dest.writeValue(occupation);
        dest.writeInt(rating);
        dest.writeValue(teachingCredits);
    }

    @SuppressWarnings("unused")
    public static final Creator<TutorProfile> CREATOR = new Creator<TutorProfile>() {
        @Override
        public TutorProfile createFromParcel(Parcel in) {
            return new TutorProfile(in);
        }

        @Override
        public TutorProfile[] newArray(int size) {
            return new TutorProfile[size];
        }
    };

    //------------------------------------------------------------------------------------------------------------------
    /**Method invalidates the compulsory input for a learner profile
     * @param tutorTypes: For ex, I could be a Freelancer or a Schoolteacher, etc.
     * @param disciplines: The subjects or _3 that I teach; You can't be a tutor without specifying them
     * */
    private void validateInput(String[] tutorTypes,
                               String[] disciplines){
        if(tutorTypes == null){
            throw new IllegalStateException("Tutor Types are null");
        }
        if(disciplines == null){
            throw new IllegalStateException("Disciplines are null");
        }
    }
    /**This method assigns the NULL object for Tutor Types in case it is null*/
    public static String[] validateTutorTypes(String[] unvalidatedTutorType){
        if(unvalidatedTutorType == null){
            return TUTOR_TYPES_NULL;
        }
        return unvalidatedTutorType;
    }
    /**This method assigns the NULL object for Disciplines in case it is null*/
    public static String[] validateDisciplines(String[] unvalidatedDisciplines){
        if(unvalidatedDisciplines == null){
            return DISCIPLINES_NULL;
        }
        return unvalidatedDisciplines;
    }
    /**This method validates the Tutor Profile object to check if the necessary fields are NOT the NULL objects*/
    public static TutorProfile validateTutorProfile(TutorProfile tutorProfile){
        //The base class fields shall be checked before the current one
        tutorProfile = (TutorProfile) GenericLearnerProfile.validateGenericLearnerProfile(tutorProfile);

        if(tutorProfile.getTutorTypes() == TUTOR_TYPES_NULL){
            throw new IllegalStateException("A Tutor can not NOT have even a single TYPE");
        }
        if(tutorProfile.getDisciplines() == DISCIPLINES_NULL){
            throw new IllegalStateException("A Tutor can not NOT have even a single Subject that he or she is able to teach");
        }
        return tutorProfile;
    }
    //-----------------------------------------------------------------------------------------------------------------

    public static class TutorProfileResponseView extends GenericLearnerProfileResponseView{

        private EducationalQualification.EducationalQualificationResponseView[] _0;
        private Occupation.OccupationResponseView _1;
        private Integer _2;
        private Integer _3;
        private Integer _4;
        private Credits.CreditsResponseView _5;

        private Integer g;

        public Integer getGlobal() {
            return g;
        }

        public void setGlobal(Integer global) {
            this.g = global;
        }

        public TutorProfileResponseView(Integer mName, Integer mEmailID, Integer mPhoneNo, Integer mDisplayPicturePath, Integer mCurrentStatus, Integer mPassword, LatLngResponseView mLastKnownGeoCoordinates, EducationalQualification.EducationalQualificationResponseView[] educationalQualifications, Occupation.OccupationResponseView occupation, Integer tutorTypes, Integer disciplines, Integer rating, Credits.CreditsResponseView teachingCredits) {
            super(mName, mEmailID, mPhoneNo, mDisplayPicturePath, mCurrentStatus, mPassword, mLastKnownGeoCoordinates);
            this._0 = educationalQualifications;
            this._1 = occupation;
            this._2 = tutorTypes;
            this._3 = disciplines;
            this._4 = rating;
            this._5 = teachingCredits;
        }

        public EducationalQualification.EducationalQualificationResponseView[] getEducationalQualifications() {
            return _0;
        }

        public void setEducationalQualifications(EducationalQualification.EducationalQualificationResponseView[] educationalQualifications) {
            this._0 = educationalQualifications;
        }

        public Occupation.OccupationResponseView getOccupation() {
            return _1;
        }

        public void setOccupation(Occupation.OccupationResponseView occupation) {
            this._1 = occupation;
        }

        public Integer getTutorTypes() {
            return _2;
        }

        public void setTutorTypes(Integer tutorTypes) {
            this._2 = tutorTypes;
        }

        public Integer getDisciplines() {
            return _3;
        }

        public void setDisciplines(Integer disciplines) {
            this._3 = disciplines;
        }

        public Integer getRating() {
            return _4;
        }

        public void setRating(Integer rating) {
            this._4 = rating;
        }

        public Credits.CreditsResponseView getTeachingCredits() {
            return _5;
        }

        public void setTeachingCredits(Credits.CreditsResponseView teachingCredits) {
            this._5 = teachingCredits;
        }
    }
}
