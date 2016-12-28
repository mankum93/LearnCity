package com.learncity.tutor.account.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelable;
import com.learncity.tutor.account.create.model.occupation.OccupationParcelable;
import com.learncity.tutor.account.create.model.qualification.educational.EducationalQualificationParcelable;

/**
 * Created by DJ on 10/22/2016.
 */

public class TutorProfileParcelable extends GenericLearnerProfileParcelable {

    private EducationalQualificationParcelable[] educationalQualifications;
    private OccupationParcelable occupation;

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

    //Start of Constructors

    public TutorProfileParcelable(String name, String emailID, String phoneNo, String currentStatus, String password) {
        super(name, emailID, phoneNo, currentStatus, password);
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, String imagePath, String currentStatus, String password) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, String currentStatus, String password, EducationalQualificationParcelable[] educationalQualifications) {
        super(name, emailID, phoneNo, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, String currentStatus, String password, OccupationParcelable occupation) {
        super(name, emailID, phoneNo, currentStatus, password);
        this.occupation = occupation;
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, String currentStatus, String password, EducationalQualificationParcelable[] educationalQualifications, OccupationParcelable occupation) {
        super(name, emailID, phoneNo, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
        this.occupation = occupation;
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, String imagePath, String currentStatus, String password, EducationalQualificationParcelable[] educationalQualifications) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, String imagePath, String currentStatus, String password, OccupationParcelable occupation) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
        this.occupation = occupation;
    }

    public TutorProfileParcelable(String name, String emailID, String phoneNo, String imagePath, String currentStatus, String password, EducationalQualificationParcelable[] educationalQualifications, OccupationParcelable occupation) {
        super(name, emailID, phoneNo, imagePath, currentStatus, password);
        this.educationalQualifications = educationalQualifications;
        this.occupation = occupation;
    }

    public TutorProfileParcelable(Parcel in) {
        super(in);
        this.educationalQualifications = (EducationalQualificationParcelable[])in.readParcelableArray(EducationalQualificationParcelable[].class.getClassLoader());
        this.occupation = (OccupationParcelable)in.readParcelable(OccupationParcelable.class.getClassLoader());
    }

    public TutorProfileParcelable(Parcel in, OccupationParcelable occupation) {
        super(in);
        this.occupation = occupation;
    }

    public TutorProfileParcelable(Parcel in, EducationalQualificationParcelable[] educationalQualifications, OccupationParcelable occupation) {
        super(in);
        this.educationalQualifications = educationalQualifications;
        this.occupation = occupation;
    }

    //End of constructors

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeArray(educationalQualifications);
        dest.writeValue(occupation);
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
