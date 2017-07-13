package com.learncity.tutor.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.learncity.generic.learner.main.HomeActivity;
import com.learncity.generic.learner.main.model.GenericDrawerLayoutListItems;
import com.learncity.learncity.R;
import com.learncity.tutor.account.profile.MyProfileActivity;
import com.learncity.tutor.jobs.JobPostingsFragment;
import com.learncity.tutor.jobs.JobRequestsFragment;
import com.learncity.tutor.jobs.Repository;
import com.learncity.tutor.jobs.database.JobsDbHelper;
import com.learncity.tutor.jobs.model.JobRequest;

import java.util.Map;

public class TutorHomeActivity extends HomeActivity {

    private static final String TAG = "TutorHomeActivity";

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "FCM Push Notification received in onNewIntent()");

        setIntent(intent);

        // Now,
        Bundle data = intent.getExtras();

        //TODO: System.currentTimeMillis() is just temp thing
        // Put here the actual time sent value for the message.

        // Lets create a new JobRequest
        JobRequest newRequest = new JobRequest(
                data.getString("messageId"),
                data.getString("name"),
                data.getString("subjects"),
                data.getString("location"),
                System.currentTimeMillis()
        );

        // Stash this job request to the Cache and the Database.
        Repository repo = Repository.getRepository();
        // Cache
        repo.updateJobRequestRecords(newRequest);
        // Db
        JobsDbHelper.insertJobRequestToDb(repo.db, newRequest);

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String drawerItemSelected = GenericDrawerLayoutListItems.LEARNER_DRAWER_LAYOUT_ITEMS[position].getFeatureName();

            if(drawerItemSelected.equals("MY PROFILE")){

                // TODO: Start an Activity representing user profile
                Toast.makeText(getBaseContext(), "Currently under development...", Toast.LENGTH_SHORT).show();

                //startActivity(new Intent(TutorHomeActivity.this, MyProfileActivity.class));
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