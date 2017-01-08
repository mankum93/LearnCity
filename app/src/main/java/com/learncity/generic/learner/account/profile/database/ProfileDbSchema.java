package com.learncity.generic.learner.account.profile.database;

/**
 * Created by DJ on 10/23/2016.
 */

public class ProfileDbSchema {

    public static final class LearnerProfileTable {
        public static final String NAME = "learner_profile_info";

        public static final class cols{
            public static final String NAME = "name";
            public static final String EMAIL_ID = "emailId";
            public static final String PHONE_NO = "phoneNo";
            public static final String CURRENT_STATUS = "status";
            public static final String PASSWORD = "password";
        }
    }


    public static final class TutorProfileTable {
        public static final String NAME = "tutor_profile_info";

        public static final class cols{
            //Currently there are no PRIMITIVE columns other then the above 5 columns for
            //the TutorProfile. It has embedded EducationalQualification and such that
            //have separate tables
        }


        //----------------EDUCATIONAL QUALIFICATION--------------------------------------------------
        public static final class EducationalQualificationTable {
            public static final String NAME = "educational_qualification_info";

            public static final class cols{

                //Foreign key
                public static final String EMAIL_ID = "emailId";
                public static final String QUALIFICATION_NAME = "qualificationName";
                public static final String YEAR_PASSING = "yearOfPassing";
                public static final String INSTITUTION_NAME = "institutionName";
            }
        }

        public static final class SecondaryEducationalQualificationTable {
            public static final String NAME = "secondary_educational_qualification_info";

            public static final class cols{
                public static final String BOARD_NAME = "boardName";
            }
        }

        public static final class SeniorSecondaryEducationalQualificationTable {
            public static final String NAME = "senior_secondary_educational_qualification_info";

            public static final class cols{
                public static final String BOARD_NAME = "boardName";
            }
        }



        //----------------OCCUPATION-----------------------------------------------------------------
        public static final class OccupationTable {
            public static final String NAME = "occupation_info";

            public static final class cols{

                //Foreign key
                public static final String EMAIL_ID = "emailId";
                public static final String ORGANIZATION_NAME = "organizationName";
                public static final String DESIGNATION_NAME = "designationName";
            }

        }
        public static final class DurationTable {
            public static final String NAME_EDUCATIONALQUALIFICATION = "duration_educationalqualification";
            public static final String NAME_SECONDARYEDUCATIONALQUALIFICATION = "duration_secondaryeducationalqualification";
            public static final String NAME_SENIORSECONDARYEDUCATIONALQUALIFICATION = "duration_seniorsecondaryeducationalqualification";
            public static final String NAME_OCCUPATION = "duration_occupation";

            public static final class cols{

                //Foreign key
                public static final String EMAIL_ID = "emailId";
                public static final String NO_OF_YEARS = "noOfYears";
                public static final String NO_OF_MONTHS = "noOfMonths";
                public static final String NO_OF_DAYS = "noOfDays";
            }
        }
    }


}