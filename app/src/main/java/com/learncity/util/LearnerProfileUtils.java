package com.learncity.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.learncity.backend.learnerApi.model.LearnerProfileVer1;
import com.learncity.learner.account.profile.model.LearnerProfile;

/**
 * Created by DJ on 6/30/2017.
 */

public final class LearnerProfileUtils {

    private static final String TAG = LearnerProfileUtils.class.getSimpleName();


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
