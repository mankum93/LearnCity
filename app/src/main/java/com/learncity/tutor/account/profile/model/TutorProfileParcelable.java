package com.learncity.tutor.account.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelable;
import com.learncity.tutor.account.profile.model.occupation.OccupationParcelable;
import com.learncity.tutor.account.profile.model.qualification.educational.EducationalQualificationParcelable;

/**
 * Created by DJ on 10/22/2016.
 */

public class TutorProfileParcelable extends GenericLearnerProfileParcelable {

    //Educational qualification proof compulsory before start of teaching
    private EducationalQualificationParcelable[] educationalQualifications;

    //Optional requirement
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
        this.tutorTypes = tutorTypes;
    }

    public String[] getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(String[] disciplines) {
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

    //Start of Constructors----------------------------------------------------------------------------------------------

    public TutorProfileParcelable(String name, String emailID, String phoneNo, int currentStatus, String password) {
        super(name, emailID, phoneNo, currentStatus, password);
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, String imagePath, int currentStatus, String password) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, int currentStatus, String password, EducationalQualificationParcelable[] educationalQualifications) {
        super(name, emailID, phoneNo, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, int currentStatus, String password, OccupationParcelable occupation) {
        super(name, emailID, phoneNo, currentStatus, password);
        this.occupation = occupation;
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, int currentStatus, String password, EducationalQualificationParcelable[] educationalQualifications, OccupationParcelable occupation) {
        super(name, emailID, phoneNo, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
        this.occupation = occupation;
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, String imagePath, int currentStatus, String password, EducationalQualificationParcelable[] educationalQualifications) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, String imagePath, int currentStatus, String password, OccupationParcelable occupation) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
        this.occupation = occupation;
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, String imagePath, int currentStatus, String password, EducationalQualificationParcelable[] educationalQualifications, OccupationParcelable occupation) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
        this.occupation = occupation;
    }

    //End of constructors---------------------------------------------------------------------------------------------------


    public TutorProfileParcelable(Parcel in) {
        super(in);
        this.educationalQualifications = (EducationalQualificationParcelable[])in.readParcelableArray(EducationalQualificationParcelable[].class.getClassLoader());
        this.tutorTypes = (String[])in.readArray(String.class.getClassLoader());
        this.disciplines = (String[])in.readArray(String.class.getClassLoader());
        this.occupation = in.readParcelable(OccupationParcelable.class.getClassLoader());
        this.rating = in.readInt();
        this.teachingCredits = in.readParcelable(CreditsParcelable.class.getClassLoader());
    }

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
    public static final Parcelable.Creator<TutorProfileParcelable> CREATOR = new Parcelable.Creator<TutorProfileParcelable>() {
        @Override
        public TutorProfileParcelable createFromParcel(Parcel in) {
            return new TutorProfileParcelable(in);
        }

        @Override
        public TutorProfileParcelable[] newArray(int size) {
            return new TutorProfileParcelable[size];
        }
    };

}
