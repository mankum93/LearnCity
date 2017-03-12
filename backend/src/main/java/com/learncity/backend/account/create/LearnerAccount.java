package com.learncity.backend.account.create;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Subclass;

import java.io.Serializable;

/**
 * Created by DJ on 3/7/2017.
 */

@Entity
@Subclass(index = true)
public class LearnerAccount extends Account implements Serializable{

    public LearnerAccount(LearnerProfileVer1 profile) {
        super(profile);
    }

    public LearnerAccount(LearnerProfileVer1 profile, LocationInfo locationInfo) {
        super(profile, locationInfo);
    }

    public LearnerAccount(){

    }

    public LearnerProfileVer1 getProfile() {
        return (LearnerProfileVer1) super.getProfile();
    }

    public void setProfile(LearnerProfileVer1 profile) {
        super.setProfile(profile);
    }
    //------------------------------------------------------------------------------------------------------------------------
    public static class LearnerAccountResponseView extends AccountResponseView{

        private Integer nil;

        public Integer getNil() {
            Integer i = super.getNil();
            if(i != null){
                if(i.intValue() == 1){
                    return null;
                }
            }
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

        public LearnerAccountResponseView(LearnerProfileVer1.LearnerProfileResponseView profile, Integer locationInfo) {
            super(profile, locationInfo);
        }

        public LearnerAccountResponseView(){

        }

        public LearnerProfileVer1.LearnerProfileResponseView getProfile() {
            return (LearnerProfileVer1.LearnerProfileResponseView) super.getProfile();
        }

        public void setProfile(LearnerProfileVer1.LearnerProfileResponseView profile) {
            super.setProfile(profile);
        }

        public static LearnerAccount normalize(LearnerAccountResponseView spec, LearnerAccount acc){

            Integer i = spec.getNil();
            if(i != null){
                if(i.intValue() == 1){
                    return null;
                }
            }

            if(spec.getGlobal() == null){
                AccountResponseView.normalize(spec, acc);

                acc.setProfile(LearnerProfileVer1.LearnerProfileResponseView.normalize(spec.getProfile(), acc.getProfile()));
            }
            return acc;
        }
    }
}
