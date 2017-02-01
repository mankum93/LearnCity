package com.learncity.tutor.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.learncity.generic.learner.main.HomeActivity;
import com.learncity.generic.learner.main.model.LearnerDrawerLayoutListItems;
import com.learncity.tutor.account.profile.MyProfileActivity;

public class TutorHomeActivity extends HomeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        setLearnerDrawerLayoutItems(LearnerDrawerLayoutListItems.TUTOR_DRAWER_LAYOUT_ITEMS);
        setAdapterViewItemClickListener(new DrawerItemClickListener());
        super.onCreate(savedInstanceState);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TEST: Start the Search Activity for the right condition
            if(LearnerDrawerLayoutListItems.TUTOR_DRAWER_LAYOUT_ITEMS[position].getSearchFeatureName() == "JOB VACANCIES"){
                //TODO: Start an activity that shows the available job opportunities
            }
            else if(LearnerDrawerLayoutListItems.TUTOR_DRAWER_LAYOUT_ITEMS[position].getSearchFeatureName() == "MY PROFILE"){
                startActivity(new Intent(TutorHomeActivity.this, MyProfileActivity.class));
            }
        }
    }

}