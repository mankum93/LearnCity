package com.learncity.generic.learner.account.create;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.learncity.learncity.R;

/**
 * Created by DJ on 2/23/2017.
 */

public class SignUpActivity extends AppCompatActivity {

    public static final String SIGN_UP_METHOD = "SIGN_UP_METHOD";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContentView(R.layout.activity_signup);

        //Choose the apt. fragment
        SignUpFragment fragment;
        String signUpMode = getIntent().getStringExtra(SIGN_UP_METHOD);

        if(signUpMode.equals(SignUpWithGoogleAccountFragment.SIGN_UP_WITH_GOOGLE)){
            fragment = new SignUpWithGoogleAccountFragment();
        }
        else{
            //EMAIL method
            fragment = new SignUpWithEmailFragment();
        }

        //Make a fragment transaction
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }
}
