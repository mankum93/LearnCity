package com.learncity.backend.account.create;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Subclass;

/**
 * Created by DJ on 10/22/2016.
 */

@Entity
@Subclass
public class TutorProfileVer1Temp extends GenericLearnerProfileVer1Temp {

    /**Null object pattern for Tutor Types */
    public static final String[] TUTOR_TYPES_NULL = {"TUTOR_TYPES_NULL"};

    /**Null object pattern for Disciplines */
    public static final String[] DISCIPLINES_NULL = {"DISCIPLINES_NULL"};


    /** Educational qualification proof compulsory before start of teaching */
    private EducationalQualificationVer1[] educationalQualifications;

    /** Optional requirement */
    private OccupationVer1 occupation;

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

    private TeachingCredits teachingCredits;

    @Ignore
    private Builder builder;

    //Getters and Setters-----------------------------------------------------------------------------------------

    public EducationalQualificationVer1[] getEducationalQualifications() {
        return educationalQualifications;
    }

    public void setEducationalQualifications(EducationalQualificationVer1[] educationalQualifications) {
        this.educationalQualifications = educationalQualifications;
    }

    public OccupationVer1 getOccupation() {
        return occupation;
    }

    public void setOccupation(OccupationVer1 occupation) {
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

    public TeachingCredits getTeachingCredits() {
        return teachingCredits;
    }

    public void setTeachingCredits(TeachingCredits teachingCredits) {
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

        @Ignore
        private TutorProfileVer1Temp tutorProfile;

        private String name;
        private String emailID;
        private String phoneNo;
        private int currentStatus;
        private String password;
        private String imagePath;
        private LatLng lastKnownGeoCoordinates;
        private EducationalQualificationVer1[] educationalQualifications;
        private OccupationVer1 occupation;
        private String[] tutorTypes;
        private String[] disciplines;
        private int rating = 0;
        private TeachingCredits teachingCredits;

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

        public Builder withEducationalQualifications(EducationalQualificationVer1[] educationalQualifications) {
            this.educationalQualifications = educationalQualifications;
            return this;
        }

        public Builder withOccupation(OccupationVer1 occupation) {
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

        public Builder withTeachingCredits(TeachingCredits credits){
            this.teachingCredits = credits;
            return this;
        }

        public Builder withGeoCoordinates(LatLng geoCoordinates) {
            this.lastKnownGeoCoordinates = geoCoordinates;
            return this;
        }

        public TutorProfileVer1Temp build() {
            //First, check if there is an already existing instance built through this Builder
            //If there is, then continue construction of "that" object
            if(tutorProfile != null){
                return tutorProfile.setState(name, emailID, phoneNo,
                        currentStatus, password, imagePath, lastKnownGeoCoordinates, educationalQualifications,
                        occupation, tutorTypes, disciplines, rating, teachingCredits);
            }
            tutorProfile = new TutorProfileVer1Temp(name, emailID, phoneNo,
                    currentStatus, password, imagePath, lastKnownGeoCoordinates, educationalQualifications,
                    occupation, tutorTypes, disciplines, rating, teachingCredits);

            tutorProfile.builder = this;
            return tutorProfile;
        }

        public TutorProfileVer1Temp getBuiltObject(){
            if(tutorProfile == null){
                throw new RuntimeException("Object has not been built; call Builder.build() to build the object");
            }
            return tutorProfile;
        }
    }

    public TutorProfileVer1Temp setState(String name,
                                         String emailID,
                                         String phoneNo,
                                         int currentStatus,
                                         String password,
                                         String imagePath,
                                         LatLng lastKnownGeoCoordinates,
                                         EducationalQualificationVer1[] educationalQualification,
                                         OccupationVer1 occupation,
                                         String[] tutorTypes,
                                         String[] disciplines,
                                         int rating,
                                         TeachingCredits credits) {
        super.setState(name, emailID, phoneNo, currentStatus, password, imagePath, lastKnownGeoCoordinates);
        validateInput(tutorTypes, disciplines);
        this.educationalQualifications = educationalQualification;
        this.occupation = occupation;
        this. tutorTypes = tutorTypes;
        this.disciplines = disciplines;
        this.rating = rating;
        this.teachingCredits = credits;
        return this;
    }

    //Start of Constructors----------------------------------------------------------------------------------------------

    public TutorProfileVer1Temp(String name,
                                String emailID,
                                String phoneNo,
                                int currentStatus,
                                String password,
                                String imagePath,
                                LatLng lastKnownGeoCoordinates,
                                EducationalQualificationVer1[] educationalQualification,
                                OccupationVer1 occupation,
                                String[] tutorTypes,
                                String[] disciplines,
                                int rating,
                                TeachingCredits credits) {
        super(name, emailID, phoneNo, currentStatus, password, imagePath, lastKnownGeoCoordinates);
        validateInput(tutorTypes, disciplines);
        this.educationalQualifications = educationalQualification;
        this.occupation = occupation;
        this. tutorTypes = tutorTypes;
        this.disciplines = disciplines;
        this.rating = rating;
        this.teachingCredits = credits;
    }
    //For serialization while storing to Db
    public TutorProfileVer1Temp(){

    }

    //End of constructors---------------------------------------------------------------------------------------------------

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
    public static TutorProfileVer1Temp validateTutorProfile(TutorProfileVer1Temp tutorProfile){
        //The base class fields shall be checked before the current one
        tutorProfile = (TutorProfileVer1Temp) validateGenericLearnerProfile(tutorProfile);

        if(tutorProfile.getTutorTypes() == TUTOR_TYPES_NULL){
            throw new IllegalStateException("A Tutor can not NOT have even a single TYPE");
        }
        if(tutorProfile.getDisciplines() == DISCIPLINES_NULL){
            throw new IllegalStateException("A Tutor can not NOT have even a single Subject that he or she is able to teach");
        }
        return tutorProfile;
    }
}
