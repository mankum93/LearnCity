package com.learncity.tutor.account.create.model.occupation;

import com.learncity.tutor.account.create.model.Duration;

/**
 * Created by DJ on 11/12/2016.
 */

public class Occupation {

    private String mCurrentOrganization;
    private String mCurrentDesignation;
    private Duration mCurrentExperience;

    //I think a person should be able to specify his/her current organization as well as their designation
    public Occupation(String mCurrentOrganization, String mCurrentDesignation) {
        this.mCurrentOrganization = mCurrentOrganization;
        this.mCurrentDesignation = mCurrentDesignation;
    }

    public Occupation(String mCurrentOrganization, Duration mCurrentExperience, String mCurrentDesignation) {
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

    public Duration getCurrentExperience() {
        return mCurrentExperience;
    }

    public void setCurrentExperience(Duration mCurrentExperience) {
        this.mCurrentExperience = mCurrentExperience;
    }
}
