package com.learncity.tutor.account.create.model.qualification.educational;

import com.learncity.tutor.account.create.model.Duration;

/**
 * Created by DJ on 11/12/2016.
 */

public class SecondaryEducationalQualification extends EducationalQualification {

    private String board;

    public SecondaryEducationalQualification(String mQualificationName, String mInstitution, Duration mDuration) {
        super(mQualificationName, mInstitution, mDuration);
    }

    public SecondaryEducationalQualification(String mQualificationName, int mYearOfPassing, String mInstitution, Duration mDuration) {
        super(mQualificationName, mYearOfPassing, mInstitution, mDuration);
    }
}
