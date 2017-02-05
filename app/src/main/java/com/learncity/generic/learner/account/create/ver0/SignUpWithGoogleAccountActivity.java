package com.learncity.generic.learner.account.create.ver0;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.learncity.learncity.R;

/**
 * Created by DJ on 10/30/2016.
 */

public class SignUpWithGoogleAccountActivity extends AppCompatActivity {

    public static String EXTRA_GENERIC_PROFILE_WITH_UNDEFINED_STATUS = "GENERIC_PROFILE_WITH_UNDEFINED_STATUS";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up_with_google);
    }
}
