package com.learncity.backend.account.create;


/**
 * Created by DJ on 11/13/2016.
 */

public class DurationVer1 {

    private Long id;
    private int mNoOfYears;
    private int mNoOfMonths;
    private long mNoOfDays;

    public DurationVer1(){

    }

    public DurationVer1(int mNoOfYears) {
        this.mNoOfYears = mNoOfYears;
    }

    public DurationVer1(int mNoOfYears, int mNoOfMonths) {
        this(mNoOfYears);
        this.mNoOfMonths = mNoOfMonths;
    }

    public DurationVer1(int mNoOfYears, int mNoOfMonths, long mNoOfDays) {
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

}
