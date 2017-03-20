package com.learncity.tutor.account.profile.model;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.learncity.backend.tutor.tutorApi.model.DurationVer1;
import com.learncity.backend.tutor.tutorApi.model.EducationalQualificationVer1;
import com.learncity.backend.tutor.tutorApi.model.OccupationVer1;
import com.learncity.backend.tutor.tutorApi.model.TutorProfileVer1;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.tutor.account.profile.model.occupation.Occupation;
import com.learncity.tutor.account.profile.model.qualification.educational.EducationalQualification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by DJ on 10/22/2016.
 */

public class TutorProfile extends GenericLearnerProfile {
    
    private static final String TAG = TutorProfile.class.getSimpleName();

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
        this.educationalQualifications = in.createTypedArray(EducationalQualification.CREATOR);
        this.tutorTypes = in.createStringArray();
        this.disciplines = in.createStringArray();
        this.occupation = in.readParcelable(Occupation.class.getClassLoader());
        this.rating = in.readInt();
        this.teachingCredits = in.readParcelable(Credits.class.getClassLoader());
    }

    //End of constructors---------------------------------------------------------------------------------------------------

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedArray(educationalQualifications, 0);
        dest.writeStringArray(tutorTypes);
        dest.writeStringArray(disciplines);
        dest.writeParcelable(occupation, 0);
        dest.writeInt(rating);
        dest.writeParcelable(teachingCredits, 0);
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

    //-----------------------------------------------------------------------------------------------------------------------
    public static TutorProfileVer1 populateProfileEntity(@NonNull TutorProfile profile, @Nullable TutorProfileVer1 profileEntity){
        //Populate the entity object with the profile info.

        if(profile == null){
            Log.d(TAG, "There is no profile to populate the entity.");
            return null;
        }
        if(profileEntity == null){
            profileEntity = new TutorProfileVer1();
        }
        com.learncity.backend.tutor.tutorApi.model.LatLng ll = null;
        if(profile.getLastKnownGeoCoordinates() != null){
            ll = new com.learncity.backend.tutor.tutorApi.model.LatLng();
            ll.setLatitude(profile.getLastKnownGeoCoordinates().latitude);
            ll.setLongitude( profile.getLastKnownGeoCoordinates().longitude);
        }

        EducationalQualificationVer1[] ed1 = new EducationalQualificationVer1[profile.getEducationalQualifications().length];
        int i = 0;
        for(EducationalQualification ed : profile.getEducationalQualifications()){
            Duration d = ed.getDuration();
            DurationVer1 d1 = new DurationVer1();
            d1.setNoOfYears(d.getNoOfYears());
            d1.setNoOfMonths(d.getNoOfMonths());
            d1.setNoOfDays(d.getNoOfDays());

            ed1[i] = new EducationalQualificationVer1();
            ed1[i].setInstitution(ed.getInstitution());
            ed1[i].setQualificationName(ed.getmQualificationName());
            ed1[i].setDuration(d1);
            ed1[i].setYearOfPassing(ed.getYearOfPassing());
        }

        // Occupation
        OccupationVer1 o = new OccupationVer1();
        Duration d = profile.getOccupation().getCurrentExperience();
        DurationVer1 d1 = new DurationVer1();
        d1.setNoOfYears(d.getNoOfYears());
        d1.setNoOfMonths(d.getNoOfMonths());
        d1.setNoOfDays(d.getNoOfDays());

        o.setCurrentDesignation(profile.getOccupation().getCurrentDesignation());
        o.setCurrentOrganization(profile.getOccupation().getCurrentOrganization());
        o.setCurrentExperience(d1);

        profileEntity.setName(profile.getName());
        profileEntity.setEmailID(profile.getEmailID());
        profileEntity.setPhoneNo(profile.getPhoneNo());
        profileEntity.setPassword(profile.getPassword());
        profileEntity.setCurrentStatus(profile.getCurrentStatus());
        profileEntity.setLastKnownGeoCoordinates(ll);
        profileEntity.setDisplayPicturePath(profile.getDisplayPicturePath());
        profileEntity.setEducationalQualifications(Arrays.asList(ed1));
        profileEntity.setOccupation(o);
        return profileEntity;
    }

    public static List<TutorProfile> populateProfilesFromEntities(List<TutorProfileVer1> profiles){
        List<TutorProfile> pi = new ArrayList<TutorProfile>(20);
        for(TutorProfileVer1 p : profiles){
            pi.add(populateProfileFromEntity(null, p));
        }
        return pi;
    }

    public static TutorProfile populateProfileFromEntity(@Nullable TutorProfile profile, @NonNull TutorProfileVer1 profileEntity){

        if(profileEntity == null){
            Log.d(TAG, "There is no profile entity to populate the profile.");
            return null;
        }
        if(profile == null){
            // Extracting educational qualifications
            EducationalQualification[] ed1 = null;
            if(profileEntity.getEducationalQualifications() != null){
                ed1 = new EducationalQualification[profileEntity.getEducationalQualifications().size()];

                int i = 0;
                for(EducationalQualificationVer1 ed : profileEntity.getEducationalQualifications()){
                    DurationVer1 d1 = ed.getDuration();
                    Duration d = null;
                    if(d1 != null){
                        d = new Duration(d1.getNoOfYears(), d1.getNoOfMonths(), d1.getNoOfDays());
                    }

                    ed1[i] = new EducationalQualification(ed.getQualificationName(), ed.getInstitution(), d);
                    ed1[i].setYearOfPassing(ed.getYearOfPassing());
                    i++;
                }
            }

            // Extracting Occupation
            Occupation o = null;
            if(profileEntity.getOccupation() != null){
                DurationVer1 d2 = profileEntity.getOccupation().getCurrentExperience();
                Duration d3 = null;
                if(d2 != null){
                    d3 = new Duration(d2.getNoOfYears(), d2.getNoOfMonths(), d2.getNoOfDays());
                }

                o = new Occupation(profileEntity.getOccupation().getCurrentOrganization(), d3,
                        profileEntity.getOccupation().getCurrentDesignation());
            }

            LatLng l = null;
            if((profileEntity.getLastKnownGeoCoordinates() != null)){
                l = new LatLng(profileEntity.getLastKnownGeoCoordinates().getLatitude()
                        , profileEntity.getLastKnownGeoCoordinates().getLongitude());
            }

            Credits c = null;
            if(profileEntity.getTeachingCredits() != null){
                try{
                    c = new Credits(profileEntity.getTeachingCredits().getAvailableCredits(),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                                    .parse(profileEntity.getTeachingCredits().getDateOfExpiryOfCredits().toStringRfc3339()));
                }catch(ParseException p){
                    throw new RuntimeException("Date format parse error");
                }
            }
            Integer _r = profileEntity.getRating();
            int r = _r == null ? -1 : _r;

            Integer s;
            profile = new TutorProfile.Builder(
                    profileEntity.getName() == null ? "" : profileEntity.getName(),
                    profileEntity.getEmailID() == null ? "" : profileEntity.getEmailID(),
                    profileEntity.getPhoneNo() == null ? "" : profileEntity.getPhoneNo(),
                    (s = profileEntity.getCurrentStatus()) == null ? STATUS_UNDEFINED :
                            (s != STATUS_LEARNER || s != STATUS_TUTOR ? STATUS_UNDEFINED : s),
                    profileEntity.getPassword() == null ? GenericLearnerProfile.PASSWORD_NULL : profileEntity.getPassword())
                    .withImagePath(profileEntity.getDisplayPicturePath())
                    .withGeoCoordinates(l)
                    .withEducationalQualifications(ed1)
                    .withOccupation(o)
                    .withDisciplines(profileEntity.getDisciplines() != null ? profileEntity.getDisciplines().toArray(new String[profileEntity.getDisciplines().size()]) : null)
                    .withTutorTypes(profileEntity.getTutorTypes() != null ? profileEntity.getTutorTypes().toArray(new String[profileEntity.getDisciplines().size()]) : null)
                    .withTeachingCredits(c)
                    .withRating(r <= 0 ? 0 : r >= 5 ? 5 : r)
                    .build();
        }
        else{
            // Extracting educational qualifications
            EducationalQualification[] ed1 = null;
            if(profileEntity.getEducationalQualifications() != null){
                ed1 = new EducationalQualification[profileEntity.getEducationalQualifications().size()];

                int i = 0;
                for(EducationalQualificationVer1 ed : profileEntity.getEducationalQualifications()){
                    DurationVer1 d1 = ed.getDuration();
                    Duration d = null;
                    if(d1 != null){
                        d = new Duration(d1.getNoOfYears(), d1.getNoOfMonths(), d1.getNoOfDays());
                    }

                    ed1[i] = new EducationalQualification(ed.getQualificationName(), ed.getInstitution(), d);
                    ed1[i].setYearOfPassing(ed.getYearOfPassing());
                    i++;
                }
            }

            // Extracting Occupation
            Occupation o = null;
            if(profileEntity.getOccupation() != null){
                DurationVer1 d2 = profileEntity.getOccupation().getCurrentExperience();
                Duration d3 = null;
                if(d2 != null){
                    d3 = new Duration(d2.getNoOfYears(), d2.getNoOfMonths(), d2.getNoOfDays());
                }

                o = new Occupation(profileEntity.getOccupation().getCurrentOrganization(), d3,
                        profileEntity.getOccupation().getCurrentDesignation());
            }

            LatLng l = null;
            if((profileEntity.getLastKnownGeoCoordinates() == null)){
                l = new LatLng(profileEntity.getLastKnownGeoCoordinates().getLatitude()
                        , profileEntity.getLastKnownGeoCoordinates().getLongitude());
            }

            Credits c = null;
            if(profileEntity.getTeachingCredits() != null){
                try{
                    c = new Credits(profileEntity.getTeachingCredits().getAvailableCredits(),
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                                    .parse(profileEntity.getTeachingCredits().getDateOfExpiryOfCredits().toStringRfc3339()));
                }catch(ParseException p){
                    throw new RuntimeException("Date format parse error");
                }
            }
            Integer _r = profileEntity.getRating();
            int r = _r == null ? -1 : _r;

            Integer s;
            profile.getTutorProfileBuilder()
                    .withName(profileEntity.getName() == null ? "" : profileEntity.getName())
                    .withEmailID(profileEntity.getEmailID() == null ? "" : profileEntity.getEmailID())
                    .withPhoneNo(profileEntity.getPhoneNo() == null ? "" : profileEntity.getPhoneNo())
                    .withCurrentStatus((s = profileEntity.getCurrentStatus()) == null ? STATUS_UNDEFINED :
                            (s != STATUS_LEARNER || s != STATUS_TUTOR ? STATUS_UNDEFINED : s))
                    .withPassword(profileEntity.getPassword() == null ? GenericLearnerProfile.PASSWORD_NULL : profileEntity.getPassword())
                    .withImagePath(profileEntity.getDisplayPicturePath())
                    .withGeoCoordinates(l)
                    .withEducationalQualifications(ed1)
                    .withOccupation(o)
                    .withDisciplines(profileEntity.getDisciplines() != null ? profileEntity.getDisciplines().toArray(new String[profileEntity.getDisciplines().size()]) : null)
                    .withTutorTypes(profileEntity.getTutorTypes() != null ? profileEntity.getTutorTypes().toArray(new String[profileEntity.getDisciplines().size()]) : null)
                    .withTeachingCredits(c)
                    .withRating(r <= 0 ? 0 : r >= 5 ? 5 : r)
                    .build();
        }

        return profile;
    }
}
