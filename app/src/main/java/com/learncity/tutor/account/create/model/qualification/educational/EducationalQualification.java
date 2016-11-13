package com.learncity.tutor.account.create.model.qualification.educational;

import com.learncity.tutor.account.create.model.Duration;

/**
 * Created by DJ on 11/12/2016.
 */

public class EducationalQualification {

    private String mQualificationName;
    private int mYearOfPassing;
    private String mInstitution;
    private Duration mDuration;

    //I expect any person to provide these at the least. I mean, come on!
    public EducationalQualification(String mQualificationName, String mInstitution, Duration mDuration) {
        this.mQualificationName = mQualificationName;
        this.mInstitution = mInstitution;
        this.mDuration = mDuration;
    }

    public EducationalQualification(String mQualificationName, int mYearOfPassing, String mInstitution, Duration mDuration) {
        this(mQualificationName, mInstitution, mDuration);
        this.mYearOfPassing = mYearOfPassing;
    }

    public void setQualificationName(String mQualificationName) {
        this.mQualificationName = mQualificationName;
    }

    public int getYearOfPassing() {
        return mYearOfPassing;
    }

    public void setYearOfPassing(int mYearOfPassing) {
        this.mYearOfPassing = mYearOfPassing;
    }

    public String getInstitution() {
        return mInstitution;
    }

    public void setInstitution(String mInstitution) {
        this.mInstitution = mInstitution;
    }

    public Duration getDuration() {
        return mDuration;
    }

    public void setDuration(Duration mDuration) {
        this.mDuration = mDuration;
    }
}
