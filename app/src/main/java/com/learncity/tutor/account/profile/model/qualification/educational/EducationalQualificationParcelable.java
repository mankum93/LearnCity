package com.learncity.tutor.account.profile.model.qualification.educational;

import android.os.Parcel;
import android.os.Parcelable;
import com.learncity.tutor.account.profile.model.DurationParcelable;

/**
 * Created by DJ on 11/13/2016.
 */

public class EducationalQualificationParcelable implements Parcelable {

    private String mQualificationName;
    private int mYearOfPassing;
    private String mInstitution;
    private DurationParcelable mDuration;

    //I expect any person to provide these at the least. I mean, come on!
    public EducationalQualificationParcelable(String mQualificationName, String mInstitution, DurationParcelable mDuration) {
        this.mQualificationName = mQualificationName;
        this.mInstitution = mInstitution;
        this.mDuration = mDuration;
    }

    public EducationalQualificationParcelable(String mQualificationName, int mYearOfPassing, String mInstitution, DurationParcelable mDuration) {
        this(mQualificationName, mInstitution, mDuration);
        this.mYearOfPassing = mYearOfPassing;
    }

    public String getmQualificationName() {
        return mQualificationName;
    }

    public void setQualificationName(String mQualificationName) {
        this.mQualificationName = mQualificationName;
    }

    public int getYearOfPassing() {
        return mYearOfPassing;
    }

    public void setYearOfPassing(int mYearOfPassing) {
        this.mYearOfPassing = mYearOfPassing;
    }

    public String getInstitution() {
        return mInstitution;
    }

    public void setInstitution(String mInstitution) {
        this.mInstitution = mInstitution;
    }

    public DurationParcelable getDuration() {
        return mDuration;
    }

    public void setDuration(DurationParcelable mDuration) {
        this.mDuration = mDuration;
    }

    protected EducationalQualificationParcelable(Parcel in) {
        mQualificationName = in.readString();
        mYearOfPassing = in.readInt();
        mInstitution = in.readString();
        mDuration = (DurationParcelable) in.readValue(DurationParcelable.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mQualificationName);
        dest.writeInt(mYearOfPassing);
        dest.writeString(mInstitution);
        dest.writeValue(mDuration);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EducationalQualificationParcelable> CREATOR = new Parcelable.Creator<EducationalQualificationParcelable>() {
        @Override
        public EducationalQualificationParcelable createFromParcel(Parcel in) {
            return new EducationalQualificationParcelable(in);
        }

        @Override
        public EducationalQualificationParcelable[] newArray(int size) {
            return new EducationalQualificationParcelable[size];
        }
    };
}
