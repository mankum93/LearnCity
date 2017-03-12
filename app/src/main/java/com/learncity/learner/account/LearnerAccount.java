package com.learncity.learner.account;

import com.learncity.generic.learner.account.Account;
import com.learncity.learner.account.profile.model.LearnerProfile;

/**
 * Created by DJ on 3/7/2017.
 */


public class LearnerAccount extends Account {

    public LearnerAccount(LearnerProfile profile) {
        super(profile);
    }

    public LearnerAccount(LearnerProfile profile, Account.LocationInfo locationInfo) {
        super(profile, locationInfo);
    }

    public LearnerProfile getProfile() {
        return (LearnerProfile) super.getProfile();
    }

    public void setProfile(LearnerProfile profile) {
        super.setProfile(profile);
    }
}
