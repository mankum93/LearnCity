package com.learncity.backend.learner.account.create;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Subclass;
import com.learncity.backend.common.account.create.GenericLearnerProfileVer1;
import com.learncity.backend.common.account.create.LatLng;

import java.io.Serializable;

/**
 * Created by DJ on 10/22/2016.
 */

@Entity
@Subclass(index = true)
public class LearnerProfileVer1 extends GenericLearnerProfileVer1 implements Serializable{

    //For serialization while storing to Db
    public LearnerProfileVer1(){
    }
    //--------------------------------------------------------------------------------------------------------------------
    public static class LearnerProfileResponseView extends GenericLearnerProfileResponseView{

        public LearnerProfileResponseView(Integer mName, Integer mEmailID, Integer mPhoneNo, Integer mDisplayPicturePath, Integer mCurrentStatus, Integer mPassword, LatLng.LatLngResponseView mLastKnownGeoCoordinates) {
            super(mName, mEmailID, mPhoneNo, mDisplayPicturePath, mCurrentStatus, mPassword, mLastKnownGeoCoordinates);
        }
    }
}
