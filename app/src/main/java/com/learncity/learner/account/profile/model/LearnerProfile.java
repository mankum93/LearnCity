package com.learncity.learner.account.profile.model;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import com.learncity.backend.learnerApi.model.LearnerProfileVer1;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;

/**
 * Created by DJ on 11/13/2016.
 */

public class LearnerProfile extends GenericLearnerProfile {

    private static final String TAG = LearnerProfile.class.getSimpleName();

    private Builder builder;

    @Override
    public String toString(){
        //Currently, there are no additional fields here. Therefore, just a super call here
        return super.toString();
    }

    public Builder getLearnerProfileBuilder(){
        //Object was initially constructed from Parcel or the single public constructor
        if(builder == null){
            return new Builder(getName(), getEmailID(), getPhoneNo(), getCurrentStatus(), getPassword());
        }
        return builder;
    }

    public static class Builder {
        private String name;
        private String emailID;
        private String phoneNo;
        private int currentStatus;
        private String password;
        private String imagePath;
        private LatLng geoCoordinates;
        private LearnerProfile learnerProfile;

        public Builder(String name,
                       String emailID,
                       String phoneNo,
                       int currentStatus,
                       String password){
            this.name = name;
            this.emailID = emailID;
            this.phoneNo = phoneNo;
            this.currentStatus = currentStatus;
            this.password = password;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withEmailID(String emailID) {
            this.emailID = emailID;
            return this;
        }

        public Builder withPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
            return this;
        }

        public Builder withCurrentStatus(int currentStatus) {
            this.currentStatus = currentStatus;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder withGeoCoordinates(LatLng geoCoordinates) {
            this.geoCoordinates = geoCoordinates;
            return this;
        }

        public LearnerProfile build() {
            //First, check if there is an already existing instance built through this Builder
            //If there is, then continue construction of "that" object
            if(learnerProfile != null){
                //setState() has input validation inbuilt
                return learnerProfile.setState(name, emailID, phoneNo,
                        currentStatus, password, imagePath, geoCoordinates);
            }
            //Constructor has input invalidation inbuilt
            learnerProfile = new LearnerProfile(name, emailID, phoneNo,
                    currentStatus, password, imagePath, geoCoordinates);

            learnerProfile.builder = this;
            return learnerProfile;
        }

        public LearnerProfile getBuiltObject(){
            if(learnerProfile == null){
                throw new RuntimeException("Object has not been built; call Builder.build() to build the object");
            }

            return learnerProfile;
        }
    }

    @Override
    public LearnerProfile setState(String name,
                                   String emailID,
                                   String phoneNo,
                                   int currentStatus,
                                   String password,
                                   String imagePath,
                                   LatLng lastKnownGeoCoordinates){
        super.setState(name, emailID, phoneNo, currentStatus, password, imagePath, lastKnownGeoCoordinates);

        return this;
    }

    //Constructors-------------------------------------------------------------------------------------------------------

    //These are the required profile fields to create an A/C
    public LearnerProfile(String name,
                          String emailID,
                          String phoneNo,
                          int currentStatus,
                          String password,
                          String imagePath,
                          LatLng lastKnownGeoCoordinates){
        super(name, emailID, phoneNo, currentStatus, password, imagePath, lastKnownGeoCoordinates);
    }

    protected LearnerProfile(Parcel in) {
        super(in);
    }

    //---------------------------------------------------------------------------------------------------------------


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @SuppressWarnings("unused")
    public static final Creator<LearnerProfile> CREATOR = new Creator<LearnerProfile>() {
        @Override
        public LearnerProfile createFromParcel(Parcel in) {
            return new LearnerProfile(in);
        }

        @Override
        public LearnerProfile[] newArray(int size) {
            return new LearnerProfile[size];
        }
    };

    //------------------------------------------------------------------------------------------------------------------

    public static LearnerProfileVer1 populateProfileEntity(@NonNull LearnerProfile profile, @Nullable LearnerProfileVer1 profileEntity){
        //Populate the entity object with the profile info.

        if(profile == null){
            Log.d(TAG, "There is no profile to populate the entity.");
            return null;
        }

        if(profileEntity == null){
            profileEntity = new LearnerProfileVer1();
        }
        com.learncity.backend.learnerApi.model.LatLng ll = null;
        if(profile.getLastKnownGeoCoordinates() != null){
            ll = new com.learncity.backend.learnerApi.model.LatLng();
            ll.setLatitude(profile.getLastKnownGeoCoordinates().latitude);
            ll.setLongitude( profile.getLastKnownGeoCoordinates().longitude);
        }

        profileEntity.setName(profile.getName());
        profileEntity.setEmailID(profile.getEmailID());
        profileEntity.setPhoneNo(profile.getPhoneNo());
        profileEntity.setPassword(profile.getPassword());
        profileEntity.setCurrentStatus(profile.getCurrentStatus());
        profileEntity.setLastKnownGeoCoordinates(ll);
        profileEntity.setDisplayPicturePath(profile.getDisplayPicturePath());
        return profileEntity;
    }

    public static LearnerProfile populateProfileFromEntity(@Nullable LearnerProfile profile, @NonNull LearnerProfileVer1 profileEntity){

        if(profileEntity == null){
            Log.d(TAG, "There is no profile entity to populate the profile.");
            return null;
        }

        LatLng l = null;
        if(profileEntity.getLastKnownGeoCoordinates() != null){
            l = new LatLng(profileEntity.getLastKnownGeoCoordinates().getLatitude()
                    , profileEntity.getLastKnownGeoCoordinates().getLongitude());
        }
        if(profile == null){
            profile = new LearnerProfile.Builder(
                    profileEntity.getName(),
                    profileEntity.getEmailID(),
                    profileEntity.getPhoneNo(),
                    profileEntity.getCurrentStatus(),
                    profileEntity.getPassword())
                    .withImagePath(profileEntity.getDisplayPicturePath())
                    .withGeoCoordinates(l)
                    .build();
        }
        else{
            profile.getLearnerProfileBuilder()
                    .withName(profileEntity.getName())
                    .withEmailID(profileEntity.getEmailID())
                    .withPhoneNo(profileEntity.getPhoneNo())
                    .withCurrentStatus(profileEntity.getCurrentStatus())
                    .withPassword(profileEntity.getPassword())
                    .withImagePath(profileEntity.getDisplayPicturePath())
                    .withGeoCoordinates(l).build();
        }

        return profile;
    }

}
