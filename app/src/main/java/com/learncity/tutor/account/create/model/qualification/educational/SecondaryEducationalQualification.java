package com.learncity.tutor.account.create.model.qualification.educational;

import com.learncity.tutor.account.create.model.Duration;

/**
 * Created by DJ on 11/12/2016.
 */

public class SecondaryEducationalQualification extends EducationalQualification {

    private String mBoard;

    public SecondaryEducationalQualification(String boardName, String mInstitution, int mYearOfPassing){
        super("High School/10th Standard", mYearOfPassing, mInstitution, new Duration(1,0,0));
        mBoard = boardName;
    }

    public String getmBoard() {
        return mBoard;
    }

    public void setmBoard(String mBoard) {
        this.mBoard = mBoard;
    }
}
