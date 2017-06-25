package com.learncity.tutor.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.learncity.generic.learner.main.HomeActivity;
import com.learncity.generic.learner.main.model.GenericDrawerLayoutListItems;
import com.learncity.learncity.R;
import com.learncity.tutor.account.profile.MyProfileActivity;
import com.learncity.tutor.jobs.JobPostingsFragment;
import com.learncity.tutor.jobs.JobRequestsFragment;

public class TutorHomeActivity extends HomeActivity {

    private ViewPager viewPager;
    private TutorJobsPagerAdapter requestRecordsPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){

        setContentView(R.layout.activity_tutor_home);

        // Drawer Layout setup
        setGenericDrawerLayoutItems(GenericDrawerLayoutListItems.TUTOR_DRAWER_LAYOUT_ITEMS);
        setAdapterViewItemClickListener(new DrawerItemClickListener());

        super.onCreate(savedInstanceState);

        // Main Content ViewPager setup.
        viewPager = (ViewPager) findViewById(R.id.tutor_home_viewpager);

        // Set the Adapter on ViewPager
        requestRecordsPagerAdapter = new TutorJobsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(requestRecordsPagerAdapter);

        // Setup the PagerTabStrip
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.tutor_jobs_pager_tab_strip);
        pagerTabStrip.setTabIndicatorColorResource(R.color.colorPrimary);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(GenericDrawerLayoutListItems.TUTOR_DRAWER_LAYOUT_ITEMS[position].getFeatureName().equals("MY PROFILE")){
                startActivity(new Intent(TutorHomeActivity.this, MyProfileActivity.class));
            }
        }
    }


    // FRAGMENT PAGER ADAPTER FOR TUTOR REQUEST RECORDS---------------------------------------------------------------------

    private static class TutorJobsPagerAdapter extends FragmentPagerAdapter {

        public TutorJobsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    return new JobRequestsFragment();
                //break;
                case 1:
                    return new JobPostingsFragment();
                //break;
            }
            // Default: Contacts list
            return new JobRequestsFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch(position){
                case 0:
                    return "Job Requests";
                //break;
                case 1:
                    return "Job Postings";
                //break;
            }

            return "Job Requests";
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}