package com.learncity.backend.account.create;


/**
 * Created by DJ on 11/13/2016.
 */

public class EducationalQualificationVer1{

    private Long id;
    private String mQualificationName;
    private int mYearOfPassing;
    private String mInstitution;
    private DurationVer1 mDuration;

    public EducationalQualificationVer1(){

    }

    //I expect any person to provide these at the least. I mean, come on!
    public EducationalQualificationVer1(String mQualificationName, String mInstitution, DurationVer1 mDuration) {
        this.mQualificationName = mQualificationName;
        this.mInstitution = mInstitution;
        this.mDuration = mDuration;
    }

    public EducationalQualificationVer1(String mQualificationName, int mYearOfPassing, String mInstitution, DurationVer1 mDuration) {
        this(mQualificationName, mInstitution, mDuration);
        this.mYearOfPassing = mYearOfPassing;
    }

    public String getmQualificationName() {
        return mQualificationName;
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

    public DurationVer1 getDuration() {
        return mDuration;
    }

    public void setDuration(DurationVer1 mDuration) {
        this.mDuration = mDuration;
    }
}
