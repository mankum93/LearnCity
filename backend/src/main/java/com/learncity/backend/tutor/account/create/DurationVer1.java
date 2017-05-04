package com.learncity.backend.tutor.account.create;


import java.io.Serializable;

/**
 * Created by DJ on 11/13/2016.
 */

public class DurationVer1 implements Serializable{

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

        private Integer nil;

        public Integer getNil() {
            return nil;
        }

        public void setNil(Integer nil) {
            this.nil = nil;
        }

        public DurationResponseView() {
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

        //No normalization is required because these are primitives
        public static DurationVer1 normalize(DurationResponseView durationSpec, DurationVer1 duration){
            if(durationSpec == null){
                return null;
            }
            Integer i = durationSpec.getNil();
            if(i != null){
                if(i.intValue() == 1){
                    return null;
                }
            }
            return duration;
        }
    }
}
