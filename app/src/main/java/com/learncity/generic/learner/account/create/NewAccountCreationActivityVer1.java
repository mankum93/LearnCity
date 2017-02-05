package com.learncity.generic.learner.account.create;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.learncity.learncity.R;

/**
 * Created by DJ on 1/21/2017.
 */

public class NewAccountCreationActivityVer1 extends AppCompatActivity{

    //This has to be made static in order to be accessible to the A/C creation AsyncTasks so
    //that they can know about the A/C creation retrial process
    static boolean mShouldAccountCreationBeRetried = false;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_creation_ver1);
    }
}
