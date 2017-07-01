package com.learncity.tutor.account.profile;

/**
 * Created by DJ on 2/2/2017.
 */

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learncity.R;

/**
 * Created by DJ on 10/22/2016.
 */

public class MyProfileActivity extends AppCompatActivity {

    private GenericLearnerProfile myProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        MyProfileFragment profileFragment = MyProfileFragment.newInstance();

        ft.add(R.id.myprofile_fragment_container, profileFragment);
        ft.commit();
    }
}
