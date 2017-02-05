package com.learncity.backend.persistence;


/**
 * Created by DJ on 11/13/2016.
 */

public class OccupationVer1{

    private Long id;
    private String mCurrentOrganization;
    private String mCurrentDesignation;
    private DurationVer1 mCurrentExperience;

    //Designation names list
    public static String[] designationNamesList = new String[]{
            "Manager",
            "Asst. Manager",
            "Sr. Manager",
            "Secretary",
            "General Manager",
            "Team Leader",
            "Analyst",
            "Intern"
    };

    public OccupationVer1(){

    }

    //I think a person should be able to specify his/her current organization as well as their designation
    public OccupationVer1(String mCurrentOrganization, String mCurrentDesignation) {
        this.mCurrentOrganization = mCurrentOrganization;
        this.mCurrentDesignation = mCurrentDesignation;
    }

    public OccupationVer1(String mCurrentOrganization, DurationVer1 mCurrentExperience, String mCurrentDesignation) {
        this(mCurrentOrganization, mCurrentDesignation);
        this.mCurrentExperience = mCurrentExperience;
    }

    public String getCurrentOrganization() {
        return mCurrentOrganization;
    }

    public void setCurrentOrganization(String mCurrentOrganization) {
        this.mCurrentOrganization = mCurrentOrganization;
    }

    public String getCurrentDesignation() {
        return mCurrentDesignation;
    }

    public void setCurrentDesignation(String mCurrentDesignation) {
        this.mCurrentDesignation = mCurrentDesignation;
    }

    public DurationVer1 getCurrentExperience() {
        return mCurrentExperience;
    }

    public void setCurrentExperience(DurationVer1 mCurrentExperience) {
        this.mCurrentExperience = mCurrentExperience;
    }
}
