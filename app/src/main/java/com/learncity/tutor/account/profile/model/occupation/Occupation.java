package com.learncity.tutor.account.profile.model.occupation;

import android.os.Parcel;
import android.os.Parcelable;

import com.learncity.tutor.account.profile.model.Duration;

/**
 * Created by DJ on 11/13/2016.
 */

public class Occupation implements Parcelable {

    private String mCurrentOrganization;
    private String mCurrentDesignation;
    private Duration mCurrentExperience;

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

    //I think a person should be able to specify his/her current organization as well as their designation
    public Occupation(String mCurrentOrganization, String mCurrentDesignation) {
        this.mCurrentOrganization = mCurrentOrganization;
        this.mCurrentDesignation = mCurrentDesignation;
    }

    public Occupation(String mCurrentOrganization, Duration mCurrentExperience, String mCurrentDesignation) {
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

    protected Occupation(Parcel in) {
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
    public static final Parcelable.Creator<Occupation> CREATOR = new Parcelable.Creator<Occupation>() {
        @Override
        public Occupation createFromParcel(Parcel in) {
            return new Occupation(in);
        }

        @Override
        public Occupation[] newArray(int size) {
            return new Occupation[size];
        }
    };
}
