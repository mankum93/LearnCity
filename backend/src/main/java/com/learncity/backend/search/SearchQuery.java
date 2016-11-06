package com.learncity.backend.search;

import com.googlecode.objectify.annotation.Entity;

/**
 * Created by DJ on 11/4/2016.
 */

@Entity
public class SearchQuery {

    private String mSubject;
    private String mQualification;

    public String getQualification() {
        return mQualification;
    }

    public void setQualification(String mQualification) {
        this.mQualification = mQualification;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String mSubject) {
        this.mSubject = mSubject;
    }

}
