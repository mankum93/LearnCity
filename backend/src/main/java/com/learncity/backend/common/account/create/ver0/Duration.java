package com.learncity.backend.common.account.create.ver0;

/**
 * Created by DJ on 11/12/2016.
 */

public class Duration {

    private Long id;
    private int mNoOfYears;
    private int mNoOfMonths;
    private long mNoOfDays;

    public Duration(){

    }

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

}
