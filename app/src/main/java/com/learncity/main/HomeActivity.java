package com.learncity.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.learncity.learncity.R;
import com.learncity.search.SearchActivity;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    //We could also consider going without a member variable to hold the title. Could directly call Activity.getTitle()
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new HomeDrawerViewAdapter(this,
                R.layout.home_drawer_list_item_1, AppFeatures.appFeatures));
        // TODO: Set the list's click listener

        /*The v7.app.ActionBarDrawerToggle doesn't let you pass a cutom icon for the drawer indicator.
        **This is only available in v4 compat lib. v7 provides a default "hamburger" icon in the implementation
        * TO-TRY: Changing the icon to something custom
         */
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close){

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View drawerView){
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mTitle);

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }
            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                //For now, I have decided to leave the title blank. So, do nothing when the drawer opens

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }

        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


    }
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //TODO: Find menu action items and set their visibility appropriately
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TEST: Start the Search Activity for the right condition
            if(AppFeatures.appFeatures[position].getSearchFeatureName() == "SEARCH TUTORS"){
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            }
        }
    }

}

class HomeDrawerViewAdapter extends ArrayAdapter<AppFeatures.AppFeature> {

        public HomeDrawerViewAdapter(Context context, int listLayoutId, AppFeatures.AppFeature[] appFeatures) {
            super(context, listLayoutId, appFeatures);
        }

        public View getView(int position, View recycleView, ViewGroup parent) {
            //Get the feature for this position
            AppFeatures.AppFeature appFeature = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            if (recycleView == null) {
                //There is no view to recycle so we create a brand new one
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                recycleView = inflater.inflate(R.layout.home_drawer_list_item_1, parent, false);
                viewHolder.appFeatureName = (TextView) recycleView.findViewById(R.id.drawer_list_item_text_view);
                recycleView.setTag(viewHolder);
                //TODO: Set a click listener on the text view
            }
            else {
                viewHolder = (ViewHolder) recycleView.getTag();
            }
            viewHolder.appFeatureName.setText(appFeature.getSearchFeatureName());

        return recycleView;
        }

    //Purpose of the class: To cache the views. Basically, it shall hold the references to the child views for a root view.
    //All that's needed is to refresh these child views and we are good!
    private static class ViewHolder {
        TextView appFeatureName;
        //TODO: Insert an icon for the app feature
    }
}