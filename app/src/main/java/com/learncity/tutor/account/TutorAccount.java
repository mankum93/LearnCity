package com.learncity.tutor.account;

import android.os.Parcel;
import android.os.Parcelable;

import com.learncity.generic.learner.account.Account;
import com.learncity.tutor.account.profile.model.TutorProfile;

/**
 * Created by DJ on 3/7/2017.
 */

public class TutorAccount extends Account{

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

    public TutorAccount(Parcel in) {
        super(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TutorAccount> CREATOR = new Parcelable.Creator<TutorAccount>() {
        @Override
        public TutorAccount createFromParcel(Parcel in) {
            return new TutorAccount(in);
        }

        @Override
        public TutorAccount[] newArray(int size) {
            return new TutorAccount[size];
        }
    };
}
