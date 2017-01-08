package com.learncity.generic.learner.account.create;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.learncity.learncity.R;
import com.learncity.learner.main.HomeActivity;

/**
 * Created by DJ on 10/30/2016.
 */

public class NewAccountActivity extends AppCompatActivity {

    private boolean isAccountAlreadyExisting = false;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Before attaching the A/C creation fragment, check if this device already has an existent A/C in the local DB
        if(isAccountAlreadyExisting){
            startActivity(new Intent(this, HomeActivity.class));
        }
        else{
            //Present the A/C creation/Login interface
            setContentView(R.layout.activity_new_account_create);
        }
    }
}
