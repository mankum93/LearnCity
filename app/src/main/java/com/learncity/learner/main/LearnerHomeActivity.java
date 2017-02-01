package com.learncity.learner.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.learncity.generic.learner.main.HomeActivity;
import com.learncity.generic.learner.main.model.LearnerDrawerLayoutListItems;
import com.learncity.learner.account.profile.MyProfileActivity;
import com.learncity.learner.search.SearchActivity;

public class LearnerHomeActivity extends HomeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        setLearnerDrawerLayoutItems(LearnerDrawerLayoutListItems.LEARNER_DRAWER_LAYOUT_ITEMS);
        setAdapterViewItemClickListener(new DrawerItemClickListener());
        super.onCreate(savedInstanceState);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TEST: Start the Search Activity for the right condition
            if(LearnerDrawerLayoutListItems.LEARNER_DRAWER_LAYOUT_ITEMS[position].getSearchFeatureName() == "SEARCH TUTORS"){
                startActivity(new Intent(LearnerHomeActivity.this, SearchActivity.class));
            }
            else if(LearnerDrawerLayoutListItems.LEARNER_DRAWER_LAYOUT_ITEMS[position].getSearchFeatureName() == "MY PROFILE"){
                startActivity(new Intent(LearnerHomeActivity.this, MyProfileActivity.class));
            }
        }
    }

}