package com.learncity.search;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.learncity.learncity.R;

/**
 * Created by DJ on 10/18/2016.
 */
public class SearchActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_tutors);
        //Add fragment transactions for the three search parameters
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        SubjectSearchFragment subjectSearchFragment = new SubjectSearchFragment();
        ft.add(R.id.search_activity_fragment_container, subjectSearchFragment);
        ft.commit();
    }
}
