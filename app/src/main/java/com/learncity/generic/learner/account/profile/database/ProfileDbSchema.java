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
            public static final String DISPLAY_PIC_URI = "displayPictureURI";
        }
        public static final class LocationTable {
            public static final String NAME = "learner_location_info";

            public static final class cols{
                //Foreign key
                public static final String EMAIL_ID = "emailId";
                public static final String LATITUDE = "latitude";
                public static final String LONGITUDE = "longitude";
            }
        }
    }


    public static final class TutorProfileTable {
        public static final String NAME = "tutor_profile_info";

        public static final class cols{
            //This column will have a Tutor's types concatenated by a separator
            //It's client's responsibility to concatenate and break down this string
            public static final String TUTOR_TYPES = "tutorTypes";

            //This column will have a list of subjects  concatenated by a separator
            //It's client's responsibility to concatenate and break down this string
            public static final String SUBJECT_TYPES = "subjects";

            public static final String RATING = "rating";
        }

        //----------------TEACHING CREDITS-----------------------------------------------------------
        public static final class TeachingCreditsTable {
            public static final String NAME = "teaching_credits_info";

            public static final class cols{

                //Foreign key
                public static final String EMAIL_ID = "emailId";
                public static final String AVAILABLE_CREDITS = "availableCredits";
                public static final String DATE_OF_EXPIRY = "dateOfExpiry";
            }
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