package com.learncity.backend.account.create;


import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Subclass;
import com.learncity.backend.util.ArraysUtil;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by DJ on 10/22/2016.
 */

@Entity
@Subclass(index = true)
public class TutorProfileVer1 extends GenericLearnerProfileVer1 implements Serializable{

    /** Educational qualification proof compulsory before start of teaching */
    private EducationalQualificationVer1[] educationalQualifications;

    /** Optional requirement */
    private OccupationVer1 occupation;

    /**Type of Tutor that I am; Ex: College Professor, School Teacher, Undergraduate, etc.*/
    //List of all available types: R.array.type_of_tutor
    @Index private String[] tutorTypes;

    /**The disciplines/subjects that the tutor can teach*/
    //List of all available disciplines/subjects: R.array.list_of_disciplines
    @Index private String[] disciplines;

    /**Rating is a average of all 5 point scale scores given by the students
     * Minimum rating available: 1
     * Maximum rating available: 5
     * Initial rating(unrated): 0
     * NOTE: In case of rating 0, status should be reflected as "Unrated"*/
    private int rating = 0;     //Initial: Unrated

    private TeachingCredits teachingCredits;

    //For serialization while storing to Db
    public TutorProfileVer1(){

    }

    //End of constructors---------------------------------------------------------------------------------------------------

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
        /*if(tutorTypes == null){
            throw new IllegalStateException("Tutor Types are null");
        }*/
        this.tutorTypes = tutorTypes;
    }

    public String[] getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(String[] disciplines) {
        /*if(disciplines == null){
            throw new IllegalStateException("Disciplines are null");
        }*/
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
    public void ensureTutorTypesArrayUniqueness(){
        tutorTypes = ArraysUtil.ensureUniqueness(tutorTypes);
    }
    public void ensureDisciplinesArrayUniqueness(){
        disciplines = ArraysUtil.ensureUniqueness(disciplines);
    }
    public void refineProfileData(){
        tutorTypes = ArraysUtil.trimArray(ArraysUtil.ensureUniqueness(tutorTypes));
        disciplines = ArraysUtil.trimArray(ArraysUtil.ensureUniqueness(disciplines));
    }


    //-----------------------------------------------------------------------------------------------------------------

    public static class TutorProfileResponseView extends GenericLearnerProfileResponseView{

        private static final Logger logger = Logger.getLogger(TutorProfileResponseView.class.getName());

        private EducationalQualificationVer1.EducationalQualificationResponseView _0;
        private OccupationVer1.OccupationResponseView _1;
        private Integer _2;
        private Integer _3;
        private Integer _4;
        private TeachingCredits.CreditsResponseView _5;

        private Integer nil;

        public Integer getNil() {

            Integer i = super.getNil();
            if(i != null){
                if(i.intValue() == 1){
                    return null;
                }
            }
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

        public TutorProfileResponseView() {
        }

        public TutorProfileResponseView(Integer mName, Integer mEmailID, Integer mPhoneNo, Integer mDisplayPicturePath, Integer mCurrentStatus, Integer mPassword, LatLng.LatLngResponseView mLastKnownGeoCoordinates, EducationalQualificationVer1.EducationalQualificationResponseView educationalQualification, OccupationVer1.OccupationResponseView occupation, Integer tutorTypes, Integer disciplines, Integer rating, TeachingCredits.CreditsResponseView teachingCredits) {
            super(mName, mEmailID, mPhoneNo, mDisplayPicturePath, mCurrentStatus, mPassword, mLastKnownGeoCoordinates);
            this._0 = educationalQualification;
            this._1 = occupation;
            this._2 = tutorTypes;
            this._3 = disciplines;
            this._4 = rating;
            this._5 = teachingCredits;
        }

        public EducationalQualificationVer1.EducationalQualificationResponseView getEducationalQualification() {
            return _0;
        }

        public void setEducationalQualifications(EducationalQualificationVer1.EducationalQualificationResponseView educationalQualification) {
            this._0 = educationalQualification;
        }

        public OccupationVer1.OccupationResponseView getOccupation() {
            return _1;
        }

        public void setOccupation(OccupationVer1.OccupationResponseView occupation) {
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

        public TeachingCredits.CreditsResponseView getTeachingCredits() {
            return _5;
        }

        public void setTeachingCredits(TeachingCredits.CreditsResponseView teachingCredits) {
            this._5 = teachingCredits;
        }

        public static TutorProfileVer1 normalize(TutorProfileResponseView responseSpec, TutorProfileVer1 profile){

            if(responseSpec == null){
                return null;
            }

            logger.info("Response spec: " + responseSpec);
            Integer i = responseSpec.getNil();
            if(i != null){
                if(i.intValue() == 1){
                    return null;
                }
            }

            //Check for the Global switch
            if(responseSpec.getGlobal() == null){
                GenericLearnerProfileVer1.GenericLearnerProfileResponseView.normalize(responseSpec, profile);

                if(responseSpec.getTutorTypes() == null){
                    profile.setTutorTypes(null);
                }
                if(responseSpec.getDisciplines() == null){
                    profile.setDisciplines(null);
                }
                for(EducationalQualificationVer1 ed : profile.getEducationalQualifications()){
                    ed = EducationalQualificationVer1.EducationalQualificationResponseView.normalize(responseSpec.getEducationalQualification(), ed);
                }
                profile.setOccupation(OccupationVer1.OccupationResponseView.normalize(responseSpec.getOccupation(), profile.getOccupation()));

                profile.setTeachingCredits(TeachingCredits.CreditsResponseView.normalize(responseSpec.getTeachingCredits(), profile.getTeachingCredits()));
            }
            return profile;
        }
    }
}
