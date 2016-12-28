package com.learncity.tutor.account.profile.model;


import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.tutor.account.create.model.occupation.OccupationParcelable;
import com.learncity.tutor.account.create.model.qualification.educational.EducationalQualificationParcelable;

/**
 * Created by DJ on 11/14/2016.
 */

public class TutorProfile extends GenericLearnerProfile {

    private EducationalQualificationParcelable[] educationalQualifications;
    private OccupationParcelable occupation;

    public EducationalQualificationParcelable[] getEducationalQualification() {
        return educationalQualifications;
    }

    public void setEducationalQualification(EducationalQualificationParcelable[] educationalQualifications) {
        this.educationalQualifications = educationalQualifications;
    }

    public OccupationParcelable getOccupation() {
        return occupation;
    }

    public void setOccupation(OccupationParcelable occupation) {
        this.occupation = occupation;
    }
//Start of Constructors

    public TutorProfile(String name, String emailID, String phoneNo, String currentStatus, String password) {
        super(name, emailID, phoneNo, currentStatus, password);
    }

    public TutorProfile(String name, String emailID, String phoneNo, String imagePath, String currentStatus, String password) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
    }

    public TutorProfile(String name, String emailID, String phoneNo, String currentStatus, String password, EducationalQualificationParcelable[] educationalQualifications) {
        super(name, emailID, phoneNo, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
    }

    public TutorProfile(String name, String emailID, String phoneNo, String currentStatus, String password, OccupationParcelable occupation) {
        super(name, emailID, phoneNo, currentStatus, password);
        this.occupation = occupation;
    }

    public TutorProfile(String name, String emailID, String phoneNo, String currentStatus, String password, EducationalQualificationParcelable[] educationalQualifications, OccupationParcelable occupation) {
        super(name, emailID, phoneNo, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
        this.occupation = occupation;
    }

    public TutorProfile(String name, String emailID, String phoneNo, String imagePath, String currentStatus, String password, EducationalQualificationParcelable[] educationalQualifications) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
    }

    public TutorProfile(String name, String emailID, String phoneNo, String imagePath, String currentStatus, String password, OccupationParcelable occupation) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
        this.occupation = occupation;
    }

    public TutorProfile(String name, String emailID, String phoneNo, String imagePath, String currentStatus, String password, EducationalQualificationParcelable[] educationalQualifications, OccupationParcelable occupation) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
        this.occupation = occupation;
    }

}