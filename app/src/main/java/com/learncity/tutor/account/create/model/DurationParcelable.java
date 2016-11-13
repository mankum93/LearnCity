package com.learncity.tutor.account.create.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DJ on 11/13/2016.
 */

public class DurationParcelable implements Parcelable {

    private int mNoOfYears;
    private int mNoOfMonths;
    private long mNoOfDays;

    public DurationParcelable(int mNoOfYears) {
        this.mNoOfYears = mNoOfYears;
    }

    public DurationParcelable(int mNoOfYears, int mNoOfMonths) {
        this(mNoOfYears);
        this.mNoOfMonths = mNoOfMonths;
    }

    public DurationParcelable(int mNoOfYears, int mNoOfMonths, long mNoOfDays) {
        this(mNoOfYears, mNoOfMonths);
        this.mNoOfDays = mNoOfDays;
    }

    public int getNoOfYears() {
        return mNoOfYears;
    }

    public void setNoOfYears(int mNoOfYears) {
        this.mNoOfYears = mNoOfYears;
    }

    public int getNoOfMonths() {
        return mNoOfMonths;
    }

    public void setNoOfMonths(int mNoOfMonths) {
        this.mNoOfMonths = mNoOfMonths;
    }

    public long getNoOfDays() {
        return mNoOfDays;
    }

    public void setNoOfDays(long mNoOfDays) {
        this.mNoOfDays = mNoOfDays;
    }


    protected DurationParcelable(Parcel in) {
        mNoOfYears = in.readInt();
        mNoOfMonths = in.readInt();
        mNoOfDays = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mNoOfYears);
        dest.writeInt(mNoOfMonths);
        dest.writeLong(mNoOfDays);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DurationParcelable> CREATOR = new Parcelable.Creator<DurationParcelable>() {
        @Override
        public DurationParcelable createFromParcel(Parcel in) {
            return new DurationParcelable(in);
        }

        @Override
        public DurationParcelable[] newArray(int size) {
            return new DurationParcelable[size];
        }
    };
}
