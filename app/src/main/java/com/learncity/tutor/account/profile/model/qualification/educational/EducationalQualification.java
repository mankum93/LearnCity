package com.learncity.tutor.account.profile.model.qualification.educational;

import android.os.Parcel;
import android.os.Parcelable;
import com.learncity.tutor.account.profile.model.Duration;

/**
 * Created by DJ on 11/13/2016.
 */

public class EducationalQualification implements Parcelable {

    private String mQualificationName;
    private int mYearOfPassing;
    private String mInstitution;
    private Duration mDuration;

    //I expect any person to provide these at the least. I mean, come on!
    public EducationalQualification(String mQualificationName, String mInstitution, Duration mDuration) {
        this.mQualificationName = mQualificationName;
        this.mInstitution = mInstitution;
        this.mDuration = mDuration;
    }

    public EducationalQualification(String mQualificationName, int mYearOfPassing, String mInstitution, Duration mDuration) {
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

    public Duration getDuration() {
        return mDuration;
    }

    public void setDuration(Duration mDuration) {
        this.mDuration = mDuration;
    }

    protected EducationalQualification(Parcel in) {
        mQualificationName = in.readString();
        mYearOfPassing = in.readInt();
        mInstitution = in.readString();
        mDuration = (Duration) in.readValue(Duration.class.getClassLoader());
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
    public static final Parcelable.Creator<EducationalQualification> CREATOR = new Parcelable.Creator<EducationalQualification>() {
        @Override
        public EducationalQualification createFromParcel(Parcel in) {
            return new EducationalQualification(in);
        }

        @Override
        public EducationalQualification[] newArray(int size) {
            return new EducationalQualification[size];
        }
    };

    //-----------------------------------------------------------------------------------------------------------------------
    public static class EducationalQualificationResponseView{
        private Integer _0;
        private Integer _1;
        private Integer _2;
        private Duration.DurationResponseView _3;

        private Integer g;

        public Integer getGlobal() {
            return g;
        }

        public void setGlobal(Integer global) {
            this.g = global;
        }

        public EducationalQualificationResponseView(Integer mQualificationName, Integer mYearOfPassing, Integer mInstitution, Duration.DurationResponseView mDuration) {
            this._0 = mQualificationName;
            this._1 = mYearOfPassing;
            this._2 = mInstitution;
            this._3 = mDuration;
        }

        public Integer getmQualificationName() {
            return _0;
        }

        public void setmQualificationName(Integer mQualificationName) {
            this._0 = mQualificationName;
        }

        public Integer getmYearOfPassing() {
            return _1;
        }

        public void setmYearOfPassing(Integer mYearOfPassing) {
            this._1 = mYearOfPassing;
        }

        public Integer getmInstitution() {
            return _2;
        }

        public void setmInstitution(Integer mInstitution) {
            this._2 = mInstitution;
        }

        public Duration.DurationResponseView getmDuration() {
            return _3;
        }

        public void setmDuration(Duration.DurationResponseView mDuration) {
            this._3 = mDuration;
        }
    }
}
