package com.learncity.tutor.account.profile.model.qualification.educational;

import android.os.Parcel;
import android.os.Parcelable;

import com.learncity.tutor.account.profile.model.Duration;

/**
 * Created by DJ on 1/2/2017.
 */

public class SeniorSecondaryEducationalQualification extends EducationalQualification {
    
    private String mBoard;

    public SeniorSecondaryEducationalQualification(String boardName, String mInstitution, int mYearOfPassing){
        super("Senior Secondary/12th Standard", mYearOfPassing, mInstitution, new Duration(1,0,0));
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

    protected SeniorSecondaryEducationalQualification(Parcel in) {
        super(in);
        mBoard = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SeniorSecondaryEducationalQualification> CREATOR = new Parcelable.Creator<SeniorSecondaryEducationalQualification>() {
        @Override
        public SeniorSecondaryEducationalQualification createFromParcel(Parcel in) {
            return new SeniorSecondaryEducationalQualification(in);
        }

        @Override
        public SeniorSecondaryEducationalQualification[] newArray(int size) {
            return new SeniorSecondaryEducationalQualification[size];
        }
    };
}
