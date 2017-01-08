package com.learncity.tutor.account.create.model.qualification.educational;

import com.learncity.tutor.account.create.model.Duration;
import com.learncity.tutor.account.create.model.DurationParcelable;

/**
 * Created by DJ on 11/12/2016.
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
}
