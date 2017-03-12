package com.learncity.tutor.account;

import com.learncity.generic.learner.account.Account;
import com.learncity.tutor.account.profile.model.TutorProfile;

/**
 * Created by DJ on 3/7/2017.
 */

public class TutorAccount extends Account {

    public TutorAccount(TutorProfile profile) {
        super(profile);
    }

    public TutorAccount(TutorProfile profile, LocationInfo locationInfo) {
        super(profile, locationInfo);
    }

    public TutorProfile getProfile() {
        return (TutorProfile) super.getProfile();
    }

    public void setProfile(TutorProfile profile) {
        super.setProfile(profile);
    }
}
