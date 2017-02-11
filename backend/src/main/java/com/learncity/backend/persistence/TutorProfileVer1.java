package com.learncity.backend.persistence;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Subclass;

/**
 * Created by DJ on 10/22/2016.
 */

@Entity
@Subclass
public class TutorProfileVer1 extends GenericLearnerProfileVer1 {

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
}
