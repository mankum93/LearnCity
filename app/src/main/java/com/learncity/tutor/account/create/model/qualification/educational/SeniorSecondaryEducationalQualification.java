package com.learncity.tutor.account.create.model.qualification.educational;

import com.learncity.tutor.account.create.model.Duration;

/**
 * Created by DJ on 11/12/2016.
 */

public class SeniorSecondaryEducationalQualification extends EducationalQualification {

    private String board;

    public SeniorSecondaryEducationalQualification(String mQualificationName, String mInstitution, Duration mDuration) {
        super(mQualificationName, mInstitution, mDuration);
    }

    public SeniorSecondaryEducationalQualification(String mQualificationName, int mYearOfPassing, String mInstitution, Duration mDuration) {
        super(mQualificationName, mYearOfPassing, mInstitution, mDuration);
    }
}
