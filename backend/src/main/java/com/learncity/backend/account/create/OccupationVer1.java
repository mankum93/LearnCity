package com.learncity.backend.account.create;


/**
 * Created by DJ on 11/13/2016.
 */

public class OccupationVer1{

    private String mCurrentOrganization;
    private String mCurrentDesignation;
    private DurationVer1 mCurrentExperience;

    //Designation names list
    public static String[] designationNamesList = new String[]{
            "Manager",
            "Asst. Manager",
            "Sr. Manager",
            "Secretary",
            "General Manager",
            "Team Leader",
            "Analyst",
            "Intern"
    };

    public OccupationVer1(){

    }

    //I think a person should be able to specify his/her current organization as well as their designation
    public OccupationVer1(String mCurrentOrganization, String mCurrentDesignation) {
        this.mCurrentOrganization = mCurrentOrganization;
        this.mCurrentDesignation = mCurrentDesignation;
    }

    public OccupationVer1(String mCurrentOrganization, DurationVer1 mCurrentExperience, String mCurrentDesignation) {
        this(mCurrentOrganization, mCurrentDesignation);
        this.mCurrentExperience = mCurrentExperience;
    }

    public String getCurrentOrganization() {
        return mCurrentOrganization;
    }

    public void setCurrentOrganization(String mCurrentOrganization) {
        this.mCurrentOrganization = mCurrentOrganization;
    }

    public String getCurrentDesignation() {
        return mCurrentDesignation;
    }

    public void setCurrentDesignation(String mCurrentDesignation) {
        this.mCurrentDesignation = mCurrentDesignation;
    }

    public DurationVer1 getCurrentExperience() {
        return mCurrentExperience;
    }

    public void setCurrentExperience(DurationVer1 mCurrentExperience) {
        this.mCurrentExperience = mCurrentExperience;
    }

    //----------------------------------------------------------------------------------------------------------------------
    public static class OccupationResponseView{
        private Integer _0;
        private Integer _1;
        private DurationVer1.DurationResponseView _2;

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

        public OccupationResponseView() {
        }

        public OccupationResponseView(Integer mCurrentOrganization, Integer mCurrentDesignation, DurationVer1.DurationResponseView mCurrentExperience) {
            this._0 = mCurrentOrganization;
            this._1 = mCurrentDesignation;
            this._2 = mCurrentExperience;
        }

        public Integer getmCurrentOrganization() {
            return _0;
        }

        public void setmCurrentOrganization(Integer mCurrentOrganization) {
            this._0 = mCurrentOrganization;
        }

        public Integer getmCurrentDesignation() {
            return _1;
        }

        public void setmCurrentDesignation(Integer mCurrentDesignation) {
            this._1 = mCurrentDesignation;
        }

        public DurationVer1.DurationResponseView getmCurrentExperience() {
            return _2;
        }

        public void setmCurrentExperience(DurationVer1.DurationResponseView mCurrentExperience) {
            this._2 = mCurrentExperience;
        }

        public static OccupationVer1 normalize(OccupationResponseView occupationSpec, OccupationVer1 occupation){
            Integer i = occupationSpec.getNil();
            if(i != null){
                if(i.intValue() == 1){
                    return null;
                }
            }
            if(occupationSpec.getGlobal() == null){
                if(occupationSpec.getmCurrentDesignation() == null){
                    occupation.setCurrentDesignation(null);
                }
                if(occupationSpec.getmCurrentOrganization() == null){
                    occupation.setCurrentOrganization(null);
                }
            }
            occupation.setCurrentExperience(DurationVer1.DurationResponseView.normalize(occupationSpec.getmCurrentExperience(), occupation.getCurrentExperience()));

            return occupation;
        }
    }
}
