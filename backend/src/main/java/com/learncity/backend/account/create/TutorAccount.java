package com.learncity.backend.account.create;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Subclass;

import java.io.Serializable;

/**
 * Created by DJ on 3/7/2017.
 */

@Entity
@Subclass(index = true)
public class TutorAccount extends Account implements Serializable {

    public TutorAccount(TutorProfileVer1 profile) {
        super(profile);
    }

    public TutorAccount(TutorProfileVer1 profile, LocationInfo locationInfo) {
        super(profile, locationInfo);
    }

    public TutorAccount(){

    }

    public TutorProfileVer1 getProfile() {
        return (TutorProfileVer1) super.getProfile();
    }

    public void setProfile(TutorProfileVer1 profile) {
        super.setProfile(profile);
    }
    //------------------------------------------------------------------------------------------------------------------------
    public static class TutorAccountResponseView extends AccountResponseView{

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

        public TutorAccountResponseView(TutorProfileVer1.TutorProfileResponseView profile, Integer locationInfo) {
            super(profile, locationInfo);
        }

        public TutorAccountResponseView(){

        }

        public TutorProfileVer1.TutorProfileResponseView getProfile() {
            return (TutorProfileVer1.TutorProfileResponseView) super.getProfile();
        }

        public void setProfile(TutorProfileVer1.TutorProfileResponseView profile) {
            super.setProfile(profile);
        }

        public static TutorAccount normalize(TutorAccountResponseView spec, TutorAccount acc){

            Integer i = spec.getNil();
            if(i != null){
                if(i.intValue() == 1){
                    return null;
                }
            }

            if(spec.getGlobal() == null){
                AccountResponseView.normalize(spec, acc);

                acc.setProfile(TutorProfileVer1.TutorProfileResponseView.normalize(spec.getProfile(), acc.getProfile()));
            }
            return acc;
        }
    }
}
