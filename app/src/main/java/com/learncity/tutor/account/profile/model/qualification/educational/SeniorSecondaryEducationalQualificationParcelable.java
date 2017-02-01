package com.learncity.tutor.account.profile.model.qualification.educational;

import android.os.Parcel;
import android.os.Parcelable;

import com.learncity.tutor.account.profile.model.DurationParcelable;

/**
 * Created by DJ on 1/2/2017.
 */

public class SeniorSecondaryEducationalQualificationParcelable extends EducationalQualificationParcelable {
    
    private String mBoard;

    public SeniorSecondaryEducationalQualificationParcelable(String boardName, String mInstitution, int mYearOfPassing){
        super("Senior Secondary/12th Standard", mYearOfPassing, mInstitution, new DurationParcelable(1,0,0));
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

    protected SeniorSecondaryEducationalQualificationParcelable(Parcel in) {
        super(in);
        mBoard = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SeniorSecondaryEducationalQualificationParcelable> CREATOR = new Parcelable.Creator<SeniorSecondaryEducationalQualificationParcelable>() {
        @Override
        public SeniorSecondaryEducationalQualificationParcelable createFromParcel(Parcel in) {
            return new SeniorSecondaryEducationalQualificationParcelable(in);
        }

        @Override
        public SeniorSecondaryEducationalQualificationParcelable[] newArray(int size) {
            return new SeniorSecondaryEducationalQualificationParcelable[size];
        }
    };
}
