package com.learncity.tutor.account.profile.model.qualification.educational;

import android.os.Parcel;
import android.os.Parcelable;

import com.learncity.tutor.account.profile.model.Duration;

/**
 * Created by DJ on 1/2/2017.
 */

public class SecondaryEducationalQualification extends EducationalQualification {

    private String mBoard;
    public SecondaryEducationalQualification(String boardName, String mInstitution, int mYearOfPassing){
        super("High school/10th Standard", mYearOfPassing, mInstitution, new Duration(1,0,0));
        mBoard = boardName;
    }

    public String getmBoard() {
        return mBoard;
    }

    public void setmBoard(String mBoard) {
        this.mBoard = mBoard;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mBoard);
    }

    protected SecondaryEducationalQualification(Parcel in) {
        super(in);
        mBoard = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SecondaryEducationalQualification> CREATOR = new Parcelable.Creator<SecondaryEducationalQualification>() {
        @Override
        public SecondaryEducationalQualification createFromParcel(Parcel in) {
            return new SecondaryEducationalQualification(in);
        }

        @Override
        public SecondaryEducationalQualification[] newArray(int size) {
            return new SecondaryEducationalQualification[size];
        }
    };

    //--------------------------------------------------------------------------------------------------------------------
    public static class SecondaryEducationalQualificationResponseView extends EducationalQualificationResponseView{
        private Integer _0;

        private Integer g;

        public Integer getGlobal() {
            return g;
        }

        public void setGlobal(Integer global) {
            this.g = global;
        }

        public SecondaryEducationalQualificationResponseView(Integer mQualificationName, Integer mYearOfPassing, Integer mInstitution, Duration.DurationResponseView mDuration, Integer mBoard) {
            super(mQualificationName, mYearOfPassing, mInstitution, mDuration);
            this._0 = mBoard;
        }

        public Integer getmBoard() {
            return _0;
        }

        public void setmBoard(Integer mBoard) {
            this._0 = mBoard;
        }
    }
}
