package com.learncity.tutor.account.create.model.occupation;

import android.os.Parcel;
import android.os.Parcelable;

import com.learncity.tutor.account.create.model.Duration;

/**
 * Created by DJ on 11/13/2016.
 */

public class OccupationParcelable implements Parcelable {

    private String mCurrentOrganization;
    private String mCurrentDesignation;
    private Duration mCurrentExperience;

    //I think a person should be able to specify his/her current organization as well as their designation
    public OccupationParcelable(String mCurrentOrganization, String mCurrentDesignation) {
        this.mCurrentOrganization = mCurrentOrganization;
        this.mCurrentDesignation = mCurrentDesignation;
    }

    public OccupationParcelable(String mCurrentOrganization, Duration mCurrentExperience, String mCurrentDesignation) {
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

    public Duration getCurrentExperience() {
        return mCurrentExperience;
    }

    public void setCurrentExperience(Duration mCurrentExperience) {
        this.mCurrentExperience = mCurrentExperience;
    }

    protected OccupationParcelable(Parcel in) {
        mCurrentOrganization = in.readString();
        mCurrentDesignation = in.readString();
        mCurrentExperience = (Duration) in.readValue(Duration.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCurrentOrganization);
        dest.writeString(mCurrentDesignation);
        dest.writeValue(mCurrentExperience);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OccupationParcelable> CREATOR = new Parcelable.Creator<OccupationParcelable>() {
        @Override
        public OccupationParcelable createFromParcel(Parcel in) {
            return new OccupationParcelable(in);
        }

        @Override
        public OccupationParcelable[] newArray(int size) {
            return new OccupationParcelable[size];
        }
    };
}
