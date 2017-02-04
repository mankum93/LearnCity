package com.learncity.tutor.account.profile.model;

import android.os.Parcel;

import com.google.android.gms.maps.model.LatLng;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelableVer1;
import com.learncity.tutor.account.profile.model.occupation.OccupationParcelable;
import com.learncity.tutor.account.profile.model.qualification.educational.EducationalQualificationParcelable;

/**
 * Created by DJ on 10/22/2016.
 */

public class TutorProfileParcelableVer1 extends GenericLearnerProfileParcelableVer1 {

    /**Null object pattern for Tutor Types */
    public static final String[] TUTOR_TYPES_NULL = {"TUTOR_TYPES_NULL"};

    /**Null object pattern for Disciplines */
    public static final String[] DISCIPLINES_NULL = {"DISCIPLINES_NULL"};


    /** Educational qualification proof compulsory before start of teaching */
    private EducationalQualificationParcelable[] educationalQualifications;

    /** Optional requirement */
    private OccupationParcelable occupation;

    /**Type of Tutor that I am; Ex: College Professor, School Teacher, Undergraduate, etc.*/
    //List of all available types: R.array.type_of_tutor
    private String[] tutorTypes;

    /**The disciplines/subjects that the tutor can teach*/
    //List of all available disciplines/subjects: R.array.list_of_disciplines
    private String[] disciplines;

    /**Rating is a average of all 5 point scale scores given by the students
     * Minimum rating available: 1
     * Maximum rating available: 5
     * Initial rating(unrated): 0
     * NOTE: In case of rating 0, status should be reflected as "Unrated"*/
    private int rating = 0;     //Initial: Unrated

    private CreditsParcelable teachingCredits;

    private Builder builder;

    //Getters and Setters-----------------------------------------------------------------------------------------

    public EducationalQualificationParcelable[] getEducationalQualifications() {
        return educationalQualifications;
    }

    public void setEducationalQualifications(EducationalQualificationParcelable[] educationalQualifications) {
        this.educationalQualifications = educationalQualifications;
    }

    public OccupationParcelable getOccupation() {
        return occupation;
    }

    public void setOccupation(OccupationParcelable occupation) {
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
        if(tutorTypes == null){
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

    public CreditsParcelable getTeachingCredits() {
        return teachingCredits;
    }

    public void setTeachingCredits(CreditsParcelable teachingCredits) {
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

        private TutorProfileParcelableVer1 tutorProfile;

        private String name;
        private String emailID;
        private String phoneNo;
        private int currentStatus;
        private String password;
        private String imagePath;
        private LatLng lastKnownGeoCoordinates;
        private EducationalQualificationParcelable[] educationalQualifications;
        private OccupationParcelable occupation;
        private String[] tutorTypes;
        private String[] disciplines;
        private int rating = 0;
        private CreditsParcelable teachingCredits;

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

        public Builder withEducationalQualifications(EducationalQualificationParcelable[] educationalQualifications) {
            this.educationalQualifications = educationalQualifications;
            return this;
        }

        public Builder withOccupation(OccupationParcelable occupation) {
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

        public Builder withTeachingCredits(CreditsParcelable credits){
            this.teachingCredits = credits;
            return this;
        }

        public Builder withGeoCoordinates(LatLng geoCoordinates) {
            this.lastKnownGeoCoordinates = geoCoordinates;
            return this;
        }

        public TutorProfileParcelableVer1 build() {
            //First, check if there is an already existing instance built through this Builder
            //If there is, then continue construction of "that" object
            if(tutorProfile != null){
                return tutorProfile.setState(name, emailID, phoneNo,
                        currentStatus, password, imagePath, lastKnownGeoCoordinates, educationalQualifications,
                        occupation, tutorTypes, disciplines, rating, teachingCredits);
            }
            tutorProfile = new TutorProfileParcelableVer1(name, emailID, phoneNo,
                    currentStatus, password, imagePath, lastKnownGeoCoordinates, educationalQualifications,
                    occupation, tutorTypes, disciplines, rating, teachingCredits);

            tutorProfile.builder = this;
            return tutorProfile;
        }

        public TutorProfileParcelableVer1 getBuiltObject(){
            if(tutorProfile == null){
                throw new RuntimeException("Object has not been built; call Builder.build() to build the object");
            }
            return tutorProfile;
        }
    }

    public TutorProfileParcelableVer1 setState(String name,
                                               String emailID,
                                               String phoneNo,
                                               int currentStatus,
                                               String password,
                                               String imagePath,
                                               LatLng lastKnownGeoCoordinates,
                                               EducationalQualificationParcelable[] educationalQualificationParcelables,
                                               OccupationParcelable occupationParcelable,
                                               String[] tutorTypes,
                                               String[] disciplines,
                                               int rating,
                                               CreditsParcelable credits) {
        super.setState(name, emailID, phoneNo, currentStatus, password, imagePath, lastKnownGeoCoordinates);
        validateInput(tutorTypes, disciplines);
        this.educationalQualifications = educationalQualificationParcelables;
        this.occupation = occupationParcelable;
        this. tutorTypes = tutorTypes;
        this.disciplines = disciplines;
        this.rating = rating;
        this.teachingCredits = credits;
        return this;
    }

    //Start of Constructors----------------------------------------------------------------------------------------------

    public TutorProfileParcelableVer1(String name,
                                      String emailID,
                                      String phoneNo,
                                      int currentStatus,
                                      String password,
                                      String imagePath,
                                      LatLng lastKnownGeoCoordinates,
                                      EducationalQualificationParcelable[] educationalQualificationParcelables,
                                      OccupationParcelable occupationParcelable,
                                      String[] tutorTypes,
                                      String[] disciplines,
                                      int rating,
                                      CreditsParcelable credits) {
        super(name, emailID, phoneNo, currentStatus, password, imagePath, lastKnownGeoCoordinates);
        validateInput(tutorTypes, disciplines);
        this.educationalQualifications = educationalQualificationParcelables;
        this.occupation = occupationParcelable;
        this. tutorTypes = tutorTypes;
        this.disciplines = disciplines;
        this.rating = rating;
        this.teachingCredits = credits;
    }

    public TutorProfileParcelableVer1(Parcel in) {
        super(in);
        this.educationalQualifications = (EducationalQualificationParcelable[])in.readParcelableArray(EducationalQualificationParcelable[].class.getClassLoader());
        this.tutorTypes = (String[])in.readArray(String.class.getClassLoader());
        this.disciplines = (String[])in.readArray(String.class.getClassLoader());
        this.occupation = in.readParcelable(OccupationParcelable.class.getClassLoader());
        this.rating = in.readInt();
        this.teachingCredits = in.readParcelable(CreditsParcelable.class.getClassLoader());
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
    public static final Creator<TutorProfileParcelableVer1> CREATOR = new Creator<TutorProfileParcelableVer1>() {
        @Override
        public TutorProfileParcelableVer1 createFromParcel(Parcel in) {
            return new TutorProfileParcelableVer1(in);
        }

        @Override
        public TutorProfileParcelableVer1[] newArray(int size) {
            return new TutorProfileParcelableVer1[size];
        }
    };

    //------------------------------------------------------------------------------------------------------------------
    /**Method invalidates the compulsory input for a learner profile
     * @param tutorTypes: For ex, I could be a Freelancer or a Schoolteacher, etc.
     * @param disciplines: The subjects or disciplines that I teach; You can't be a tutor without specifying them
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

}
