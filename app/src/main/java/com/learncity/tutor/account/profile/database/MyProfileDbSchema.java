package com.learncity.tutor.account.profile.database;

/**
 * Created by DJ on 10/23/2016.
 */

public class MyProfileDbSchema {

    public static final class MyProfileTable{
        public static final String NAME = "profile_info";

        public static final class cols{
            public static final String NAME = "name";
            public static final String EMAIL_ID = "emailId";
            public static final String PHONE_NO = "phoneNo";
            public static final String IMAGE_PATH = "imagePath";
            public static final String CURRENT_STATUS = "status";
            public static final String PASSWORD = "password";
        }
    }
}