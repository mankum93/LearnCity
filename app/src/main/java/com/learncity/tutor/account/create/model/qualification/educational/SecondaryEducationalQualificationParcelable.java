package com.learncity.tutor.account.create.model.qualification.educational;

import android.os.Parcel;
import android.os.Parcelable;

import com.learncity.tutor.account.create.model.DurationParcelable;

/**
 * Created by DJ on 1/2/2017.
 */

public class SecondaryEducationalQualificationParcelable extends EducationalQualificationParcelable {

    private String mBoard;
    public SecondaryEducationalQualificationParcelable(String boardName, String mInstitution, int mYearOfPassing){
        super("High school/10th Standard", mYearOfPassing, mInstitution, new DurationParcelable(1,0,0));
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

    protected SecondaryEducationalQualificationParcelable(Parcel in) {
        super(in);
        mBoard = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SecondaryEducationalQualificationParcelable> CREATOR = new Parcelable.Creator<SecondaryEducationalQualificationParcelable>() {
        @Override
        public SecondaryEducationalQualificationParcelable createFromParcel(Parcel in) {
            return new SecondaryEducationalQualificationParcelable(in);
        }

        @Override
        public SecondaryEducationalQualificationParcelable[] newArray(int size) {
            return new SecondaryEducationalQualificationParcelable[size];
        }
    };
}
