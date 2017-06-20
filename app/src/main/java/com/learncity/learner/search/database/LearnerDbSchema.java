package com.learncity.learner.search.database;

/**
 * Created by DJ on 6/17/2017.
 */

public final class LearnerDbSchema {

    public static final class RequestRecordsTable{

        public static final class cols{
            public static final String TO = "requestTo";
            public static final String TIMESTAMP = "timeStamp";
            public static final String REQUEST_TYPE = "requestType";
        }
    }

    public static final class TutorRequestRecordsTable{

        public static final String NAME = "tutor_request_records";

        public static final class cols{

            public static final String TUTOR_NAME = "tutorName";
            public static final String TUTOR_LOCATION = "tutorLocation";
            public static final String TUTOR_RATING = "tutorRating";
            public static final String TUTOR_SUBJECTS = "tutorSubjects";
            public static final String TUTOR_TYPE = "tutorType";
        }
    }
}
