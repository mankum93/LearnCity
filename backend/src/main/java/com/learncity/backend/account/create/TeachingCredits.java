package com.learncity.backend.account.create;


import java.util.Date;

/**
 * Created by DJ on 2/2/2017.
 *
 * Credits reflect a numerical score/points system that determines the ability to get contact details of a student
 * with a certain(there is no absolutely certain value) number available/associated with a Tutor's account
 */
public class TeachingCredits {

    private static final long CREDIT_INR_EQV = 10L;

    private Long id;
    /*Minimum credits possible: 0; Negative credits don't make sense in the current credits model*/
    private long mAvailableCredits = 0L;     //Initial credits: 0
    private Date mDateOfExpiryOfCredits;

    public TeachingCredits(){

    }

    public long getAvailableCredits() {
        return mAvailableCredits;
    }

    public void setAvailableCredits(long mAvailableCredits) {
        this.mAvailableCredits = mAvailableCredits;
    }

    public Date getDateOfExpiryOfCredits() {
        return mDateOfExpiryOfCredits;
    }

    public void setDateOfExpiryOfCredits(Date mDateOfExpiryOfCredits) {
        this.mDateOfExpiryOfCredits = mDateOfExpiryOfCredits;
    }

}
