package com.learncity.backend.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.learncity.backend.account.create.GenericLearnerProfileVer1;
import com.learncity.backend.account.create.LearnerProfileVer1;
import com.learncity.backend.account.create.TutorProfileVer1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;


/**
 * Created by DJ on 2/27/2017.
 */

public class ProfileUtils {

    private static String PATH_LEARNER_PROFILES = "LearnerProfiles.json";
    private static String PATH_TUTOR_PROFILES = "TutorProfiles1.json";

    public static GenericLearnerProfileVer1[] getJSONToProfiles(int userStatus){
        GenericLearnerProfileVer1[] profiles = null;
        Gson gson = new Gson();

        if(userStatus == GenericLearnerProfileVer1.STATUS_LEARNER){
            try{
                profiles = gson.fromJson(new JsonReader(new BufferedReader(new FileReader(PATH_LEARNER_PROFILES))), LearnerProfileVer1[].class);
            }
            catch(FileNotFoundException fnfe){
                fnfe.printStackTrace();
            }
        }
        else if(userStatus == GenericLearnerProfileVer1.STATUS_TUTOR){
            try{
                profiles = gson.fromJson(new JsonReader(new BufferedReader(new FileReader(PATH_TUTOR_PROFILES))), TutorProfileVer1[].class);
            }
            catch(FileNotFoundException fnfe){
                fnfe.printStackTrace();
            }
        }
        else{
            throw new IllegalStateException("Invalid User status");
        }
        return profiles;
    }
}
