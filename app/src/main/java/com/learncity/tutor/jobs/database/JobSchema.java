package com.learncity.tutor.jobs.database;

/**
 * Created by DJ on 6/25/2017.
 */

public final class JobSchema {

    public static final class Jobs{

        public static final class cols{

            public static String JOB_ID = "jobId";
            public static String POSTER_NAME = "posterName";
            public static String SUBJECTS = "subjects";
            public static String LOCATION = "location";
        }
    }

    public static final class JobRequestsTable{

        public static String NAME = "job_requests";

        public static final class cols{

            public static String TIME_REQUESTED = "timeRequested";
        }
    }

    public static final class JobPostingsTable{

        public static String NAME = "job_postings";

        public static final class cols{

            public static String TIME_POSTED = "timePosted";

        }
    }
}
