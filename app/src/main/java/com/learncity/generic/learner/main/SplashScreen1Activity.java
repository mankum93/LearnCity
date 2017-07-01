package com.learncity.generic.learner.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.learncity.generic.learner.account.create.NewAccountCreationActivity;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learncity.R;
import com.learncity.learner.main.LearnerHomeActivity;
import com.learncity.tutor.main.TutorHomeActivity;
import com.learncity.generic.learner.account.account_mgt.framework.AccountManager;

/**
 * Created by DJ on 1/22/2017.
 */

public class SplashScreen1Activity extends AppCompatActivity {

    private static String TAG = "SplashScreen1Activity";

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen1);

        //TODO: Delete the below statement; it is just for testing purposes
        //startActivity(new Intent(this, NewAccountCreationActivity.class));
        //Any activity which is meant to be shown first must have a check in onCreate() for an existing A/C
        //and leading to the home page of that account in that case. Currently, I am planning on having a
        //splash screen that shall have this check
        //In present case, this is the Activity responsible for that
        GenericLearnerProfile profile = AccountManager.isExistingAccountLocally(getApplicationContext());
        if(profile != null){
            Log.d(TAG, "Account already existing on this device:" + profile);
            //Lead to Home Account Activity
            if(profile.getCurrentStatus() == GenericLearnerProfile.STATUS_LEARNER){
                startActivity(new Intent(this, LearnerHomeActivity.class));
            }
            else{
                startActivity(new Intent(this, TutorHomeActivity.class));
            }
        }
        else{
            //TODO: Take the user to new account creation page
            //Here lies some UI control that the user will act on to get on next splash screen or the
            //new account creation page. For now, I am moving directly to the page
            startActivity(new Intent(this, NewAccountCreationActivity.class));
        }
    }
}
