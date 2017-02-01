package com.learncity.backend.search;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by DJ on 11/4/2016.
 */

@Entity
public class SearchQuery {

    public static final Integer DEFAULT_LIST_LIMIT = 20;

    private String mSubject;
    private String mQualification;

    /**A token string returned by the Db to access next set of results(on the next page)
     * For the first time, it is null that by default returns the results from the start*/
    private String mCursor;
    /**This plays in conjunction with the cursor - the no of results per page*/
    private Integer mLimit;

    public SearchQuery(){

    }

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

    public String getCursor() {
        return mCursor;
    }

    public void setCursor(String mCursor) {
        this.mCursor = mCursor;
    }

    public Integer getLimit() {
        return mLimit;
    }

    public void setLimit(Integer mLimit) {
        this.mLimit = mLimit;
    }
}
