package com.learncity.backend.persistence;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Subclass;

/**
 * Created by DJ on 11/14/2016.
 */

@Entity
@Subclass
public class TutorProfile extends GenericLearnerProfile {

    private EducationalQualification[] educationalQualifications;
    private Occupation occupation;

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

    //Start of Constructors

    //Default constructor required for persistence(serialization)
    public TutorProfile(){

    }

    public TutorProfile(String name, String emailID, String phoneNo, int currentStatus, String password) {
        super(name, emailID, phoneNo, currentStatus, password);
    }

    public TutorProfile(String name, String emailID, String phoneNo, String imagePath, int currentStatus, String password) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
    }

    public TutorProfile(String name, String emailID, String phoneNo, int currentStatus, String password, EducationalQualification[] educationalQualifications) {
        super(name, emailID, phoneNo, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
    }

    public TutorProfile(String name, String emailID, String phoneNo, int currentStatus, String password, Occupation occupation) {
        super(name, emailID, phoneNo, currentStatus, password);
        this.occupation = occupation;
    }

    public TutorProfile(String name, String emailID, String phoneNo, int currentStatus, String password, EducationalQualification[] educationalQualifications, Occupation occupation) {
        super(name, emailID, phoneNo, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
        this.occupation = occupation;
    }

    public TutorProfile(String name, String emailID, String phoneNo, String imagePath, int currentStatus, String password, EducationalQualification[] educationalQualifications) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
    }

    public TutorProfile(String name, String emailID, String phoneNo, String imagePath, int currentStatus, String password, Occupation occupation) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
        this.occupation = occupation;
    }

    public TutorProfile(String name, String emailID, String phoneNo, String imagePath, int currentStatus, String password, EducationalQualification[] educationalQualifications, Occupation occupation) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
        this.occupation = occupation;
    }

}