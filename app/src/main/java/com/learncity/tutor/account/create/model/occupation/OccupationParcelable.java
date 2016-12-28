package com.learncity.tutor.account.create.model.occupation;

import android.os.Parcel;
import android.os.Parcelable;

import com.learncity.tutor.account.create.model.Duration;
import com.learncity.tutor.account.create.model.DurationParcelable;

/**
 * Created by DJ on 11/13/2016.
 */

public class OccupationParcelable implements Parcelable {

    private String mCurrentOrganization;
    private String mCurrentDesignation;
    private DurationParcelable mCurrentExperience;

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
    public OccupationParcelable(String mCurrentOrganization, String mCurrentDesignation) {
        this.mCurrentOrganization = mCurrentOrganization;
        this.mCurrentDesignation = mCurrentDesignation;
    }

    public OccupationParcelable(String mCurrentOrganization, DurationParcelable mCurrentExperience, String mCurrentDesignation) {
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

    public DurationParcelable getCurrentExperience() {
        return mCurrentExperience;
    }

    public void setCurrentExperience(DurationParcelable mCurrentExperience) {
        this.mCurrentExperience = mCurrentExperience;
    }

    protected OccupationParcelable(Parcel in) {
        mCurrentOrganization = in.readString();
        mCurrentDesignation = in.readString();
        mCurrentExperience = (DurationParcelable) in.readValue(Duration.class.getClassLoader());
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
