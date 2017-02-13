package com.learncity.generic.learner.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.learncity.generic.learner.account.create.AccountCreationManager;
import com.learncity.generic.learner.account.create.NewAccountCreationActivityVer2;
import com.learncity.learncity.R;

/**
 * Created by DJ on 1/22/2017.
 */

public class SplashScreen1ActivityVer1 extends AppCompatActivity {

    private static String TAG = "SplashScreen1Activity";

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);


        //TODO: Delete the below statement; it is just for testing purposes
        startActivity(new Intent(this, NewAccountCreationActivityVer2.class));
        //Any activity which is meant to be shown first must have a check in onCreate() for an existing A/C
        //and leading to the home page of that account in that case. Currently, I am planning on having a
        //splash screen that shall have this check
        //In present case, this is the Activity responsible for that
        if(AccountCreationManager.isAccountAlreadyExistingOnThisDevice(this)){
            Log.d(TAG, "Account already existing on this device");
            //Lead to Home Account Activity
        }
        else{
            //TODO: Take the user to new account creation page
            setContentView(R.layout.activity_splashscreen1);
            //Here lies some UI control that the user will act on to get on next splash screen or the
            //new account creation page. For now, I am moving directly to the page
            startActivity(new Intent(this, NewAccountCreationActivityVer2.class));
        }
    }
}
