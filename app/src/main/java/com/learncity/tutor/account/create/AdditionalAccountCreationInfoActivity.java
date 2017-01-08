package com.learncity.tutor.account.create;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.learncity.learncity.R;

/**
 * Created by DJ on 11/12/2016.
 * This activity will always be started by the Account creation activity - NewAccountActivity
 */

public class AdditionalAccountCreationInfoActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutor_new_account_additional_info);
    }
}
