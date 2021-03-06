package com.learncity.learner.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.learncity.generic.learner.main.HomeActivity;
import com.learncity.generic.learner.main.model.GenericDrawerLayoutListItems;
import com.learncity.learncity.R;
import com.learncity.learner.search.RequestedTutorRecordsFragment;
import com.learncity.learner.search.SearchFragment;

public class LearnerHomeActivity extends HomeActivity {

    private ViewPager viewPager;
    private TutorRequestRecordsPagerAdapter requestRecordsPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){

        setContentView(R.layout.activity_learner_home);

        // Drawer Layout setup
        setGenericDrawerLayoutItems(GenericDrawerLayoutListItems.LEARNER_DRAWER_LAYOUT_ITEMS);
        setAdapterViewItemClickListener(new DrawerItemClickListener());

        super.onCreate(savedInstanceState);

        // Main Content ViewPager setup.
        viewPager = (ViewPager) findViewById(R.id.learner_home_viewpager);

        // Set the Adapter on ViewPager
        requestRecordsPagerAdapter = new TutorRequestRecordsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(requestRecordsPagerAdapter);

        // Setup the PagerTabStrip
        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.tutor_requests_pager_tab_strip);
        pagerTabStrip.setTabIndicatorColorResource(R.color.colorPrimary);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String drawerItemSelected = GenericDrawerLayoutListItems.LEARNER_DRAWER_LAYOUT_ITEMS[position].getNavItemName();

            if(drawerItemSelected.equals("MY PROFILE")){

                // TODO: Start an Activity representing user profile
                Toast.makeText(getBaseContext(), "Currently under development...", Toast.LENGTH_SHORT).show();

                //startActivity(new Intent(LearnerHomeActivity.this, MyProfileActivity.class));
            }
            else if(drawerItemSelected.equals("MY MATERIAL")){
                // TODO: Start an Activity representing material
                Toast.makeText(getBaseContext(), "Currently under development...", Toast.LENGTH_SHORT).show();
            }
            else{
                // For SETTINGS
                // TODO: Start an Activity representing Settings
                Toast.makeText(getBaseContext(), "Currently under development...", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // FRAGMENT PAGER ADAPTER FOR TUTOR REQUEST RECORDS---------------------------------------------------------------------

    private static class TutorRequestRecordsPagerAdapter extends FragmentPagerAdapter {

        public TutorRequestRecordsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    return new SearchFragment();
                //break;
                case 1:
                    return new RequestedTutorRecordsFragment();
                //break;
            }
            // Default: Contacts list
            return new SearchFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch(position){
                case 0:
                    return "Search Tutors";
                //break;
                case 1:
                    return "Requested Tutors";
                //break;
            }

            return "Search Tutors";
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}