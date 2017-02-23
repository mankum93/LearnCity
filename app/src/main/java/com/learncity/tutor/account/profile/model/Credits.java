package com.learncity.tutor.account.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by DJ on 2/2/2017.
 *
 * Credits reflect a numerical score/points system that determines the ability to get contact details of a student
 * with a certain(there is no absolutely certain value) number available/associated with a Tutor's account
 */
public class Credits implements Parcelable {

    private static final long CREDIT_INR_EQV = 10L;

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

    protected Credits(Parcel in) {
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
    public static final Parcelable.Creator<Credits> CREATOR = new Parcelable.Creator<Credits>() {
        @Override
        public Credits createFromParcel(Parcel in) {
            return new Credits(in);
        }

        @Override
        public Credits[] newArray(int size) {
            return new Credits[size];
        }
    };
}
