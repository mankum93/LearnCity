package com.learncity.tutor.account.profile.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by DJ on 2/2/2017.
 *
 * TeachingCredits reflect a numerical score/points system that determines the ability to get contact details of a student
 * with a certain(there is no absolutely certain value) number available/associated with a Tutor's account
 */
public class TeachingCredits implements Parcelable {

    private static final long CREDIT_INR_EQV = 10L;
    public static final long INITIAL_TEACHING_CREDITS = 100L;

    /*Minimum credits possible: 0; Negative credits don't make sense in the current credits model*/
    private long mAvailableCredits = 0L;     //Initial credits: 0
    private Date mDateOfExpiryOfCredits;

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

    public TeachingCredits(long mAvailableCredits, Date mDateOfExpiryOfCredits) {
        this.mAvailableCredits = mAvailableCredits;
        this.mDateOfExpiryOfCredits = mDateOfExpiryOfCredits;
    }

    protected TeachingCredits(Parcel in) {
        mAvailableCredits = in.readLong();
        mDateOfExpiryOfCredits = new Date(in.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mAvailableCredits);
        dest.writeLong(mDateOfExpiryOfCredits.getTime());
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TeachingCredits> CREATOR = new Parcelable.Creator<TeachingCredits>() {
        @Override
        public TeachingCredits createFromParcel(Parcel in) {
            return new TeachingCredits(in);
        }

        @Override
        public TeachingCredits[] newArray(int size) {
            return new TeachingCredits[size];
        }
    };

    // Utility methods----------------------------------------------------------------------------------------------------

    public static TeachingCredits assignInitialCredits(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
        cal.add(Calendar.MONTH, 1); // to get previous year add -1
        Date trialExpireDate = cal.getTime();
        TeachingCredits initialCredits = new TeachingCredits(INITIAL_TEACHING_CREDITS, trialExpireDate);

        return initialCredits;
    }

    //--------------------------------------------------------------------------------------------------------------------

    public static class CreditsResponseView{
        private Integer _0;
        private Date _1;

        private Integer g;

        public Integer getGlobal() {
            return g;
        }

        public void setGlobal(Integer global) {
            this.g = global;
        }

        public CreditsResponseView(Integer mAvailableCredits, Date mDateOfExpiryOfCredits) {
            this._0 = mAvailableCredits;
            this._1 = mDateOfExpiryOfCredits;
        }

        public Integer getmAvailableCredits() {
            return _0;
        }

        public void setmAvailableCredits(Integer mAvailableCredits) {
            this._0 = mAvailableCredits;
        }

        public Date getmDateOfExpiryOfCredits() {
            return _1;
        }

        public void setmDateOfExpiryOfCredits(Date mDateOfExpiryOfCredits) {
            this._1 = mDateOfExpiryOfCredits;
        }
    }
}
