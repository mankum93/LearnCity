package com.learncity.backend.account.create;


/**
 * Created by DJ on 11/13/2016.
 */

public class EducationalQualificationVer1{

    private Long id;
    private String mQualificationName;
    private int mYearOfPassing;
    private String mInstitution;
    private DurationVer1 mDuration;

    public EducationalQualificationVer1(){

    }

    //I expect any person to provide these at the least. I mean, come on!
    public EducationalQualificationVer1(String mQualificationName, String mInstitution, DurationVer1 mDuration) {
        this.mQualificationName = mQualificationName;
        this.mInstitution = mInstitution;
        this.mDuration = mDuration;
    }

    public EducationalQualificationVer1(String mQualificationName, int mYearOfPassing, String mInstitution, DurationVer1 mDuration) {
        this(mQualificationName, mInstitution, mDuration);
        this.mYearOfPassing = mYearOfPassing;
    }

    public String getmQualificationName() {
        return mQualificationName;
    }

    public void setQualificationName(String mQualificationName) {
        this.mQualificationName = mQualificationName;
    }

    public int getYearOfPassing() {
        return mYearOfPassing;
    }

    public void setYearOfPassing(int mYearOfPassing) {
        this.mYearOfPassing = mYearOfPassing;
    }

    public String getInstitution() {
        return mInstitution;
    }

    public void setInstitution(String mInstitution) {
        this.mInstitution = mInstitution;
    }

    public DurationVer1 getDuration() {
        return mDuration;
    }

    public void setDuration(DurationVer1 mDuration) {
        this.mDuration = mDuration;
    }

    //-----------------------------------------------------------------------------------------------------------------------
    public static class EducationalQualificationResponseView{
        private Integer _0;
        private Integer _1;
        private Integer _2;
        private DurationVer1.DurationResponseView _3;

        private Integer nil;

        public Integer getNil() {
            return nil;
        }

        public void setNil(Integer nil) {
            this.nil = nil;
        }

        private Integer g;

        public Integer getGlobal() {
            return g;
        }

        public void setGlobal(Integer global) {
            this.g = global;
        }

        public EducationalQualificationResponseView() {
        }

        public EducationalQualificationResponseView(Integer mQualificationName, Integer mYearOfPassing, Integer mInstitution, DurationVer1.DurationResponseView mDuration) {
            this._0 = mQualificationName;
            this._1 = mYearOfPassing;
            this._2 = mInstitution;
            this._3 = mDuration;
        }

        public Integer getmQualificationName() {
            return _0;
        }

        public void setmQualificationName(Integer mQualificationName) {
            this._0 = mQualificationName;
        }

        public Integer getmYearOfPassing() {
            return _1;
        }

        public void setmYearOfPassing(Integer mYearOfPassing) {
            this._1 = mYearOfPassing;
        }

        public Integer getmInstitution() {
            return _2;
        }

        public void setmInstitution(Integer mInstitution) {
            this._2 = mInstitution;
        }

        public DurationVer1.DurationResponseView getmDuration() {
            return _3;
        }

        public void setmDuration(DurationVer1.DurationResponseView mDuration) {
            this._3 = mDuration;
        }

        public static EducationalQualificationVer1 normalize(EducationalQualificationResponseView spec, EducationalQualificationVer1 ed){
            Integer i = spec.getNil();
            if(i != null){
                if(i.intValue() == 1){
                    return null;
                }
            }
            if(spec.getGlobal() == null){
                if(spec.getmInstitution() == null){
                    ed.setInstitution(null);
                }
                if(spec.getmQualificationName() == null){
                    ed.setInstitution(null);
                }
                ed.setDuration(DurationVer1.DurationResponseView.normalize(spec.getmDuration(), ed.getDuration()));
            }
            return ed;
        }
    }
}
