package com.learncity.tutor.account.profile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DJ on 11/13/2016.
 */

public class Duration implements Parcelable {

    private int mNoOfYears;
    private int mNoOfMonths;
    private long mNoOfDays;

    public Duration(int mNoOfYears) {
        this.mNoOfYears = mNoOfYears;
    }

    public Duration(int mNoOfYears, int mNoOfMonths) {
        this(mNoOfYears);
        this.mNoOfMonths = mNoOfMonths;
    }

    public Duration(int mNoOfYears, int mNoOfMonths, long mNoOfDays) {
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


    protected Duration(Parcel in) {
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
    public static final Parcelable.Creator<Duration> CREATOR = new Parcelable.Creator<Duration>() {
        @Override
        public Duration createFromParcel(Parcel in) {
            return new Duration(in);
        }

        @Override
        public Duration[] newArray(int size) {
            return new Duration[size];
        }
    };

    //----------------------------------------------------------------------------------------------------------------------
    public static class DurationResponseView{
        private Integer _0;
        private Integer _1;
        private Integer _2;

        private Integer g;

        public Integer getGlobal() {
            return g;
        }

        public void setGlobal(Integer global) {
            this.g = global;
        }

        public DurationResponseView(Integer mNoOfYears, Integer mNoOfMonths, Integer mNoOfDays) {
            this._0 = mNoOfYears;
            this._1 = mNoOfMonths;
            this._2 = mNoOfDays;
        }

        public Integer getmNoOfYears() {
            return _0;
        }

        public void setmNoOfYears(Integer mNoOfYears) {
            this._0 = mNoOfYears;
        }

        public Integer getmNoOfMonths() {
            return _1;
        }

        public void setmNoOfMonths(Integer mNoOfMonths) {
            this._1 = mNoOfMonths;
        }

        public Integer getmNoOfDays() {
            return _2;
        }

        public void setmNoOfDays(Integer mNoOfDays) {
            this._2 = mNoOfDays;
        }
    }
}
