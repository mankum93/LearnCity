package com.learncity.generic.learner.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.learncity.generic.learner.account.create.NewAccountCreationActivityVer1;
import com.learncity.generic.learner.account.profile.database.ProfileDbHelperVer1;
import com.learncity.learncity.R;

import java.io.File;

/**
 * Created by DJ on 1/22/2017.
 */

public class SplashScreen1Activity extends AppCompatActivity {

    private static String TAG = "SplashScreen1Activity";

    private boolean isAccountAlreadyExistingOnThisDevice = false;
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        //TODO: Delete the below statement; it is just for testing purposes
        startActivity(new Intent(this, NewAccountCreationActivityVer1.class));
        //Any activity which is meant to be shown first must have a check in onCreate() for an existing A/C
        //and leading to the home page of that account in that case. Currently, I am planning on having a
        //splash screen that shall have this check
        //In present case, this is the Activity responsible for that
        if(isAccountAlreadyExistingOnThisDevice()){
            Log.d(TAG, "Account already existing on this device");
            //Lead to Home Account Activity
        }
        else{
            //TODO: Take the user to new account creation page
            setContentView(R.layout.activity_splashscreen1);
            //Here lies some UI control that the user will act on to get on next splash screen or the
            //new account creation page. For now, I am moving directly to the page
            startActivity(new Intent(this, NewAccountCreationActivityVer1.class));
        }
    }

    public boolean isAccountAlreadyExistingOnThisDevice() {

        File dbPath = getDatabasePath(ProfileDbHelperVer1.DATABASE_NAME);
        if(dbPath != null){
            isAccountAlreadyExistingOnThisDevice = true;
            return isAccountAlreadyExistingOnThisDevice;
        }
        return isAccountAlreadyExistingOnThisDevice;
    }
}
