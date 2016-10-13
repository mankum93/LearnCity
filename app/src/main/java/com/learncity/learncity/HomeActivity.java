package com.learncity.learncity;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}

class HomeDrawerViewAdapter extends ArrayAdapter<AppFeatures.AppFeature> {

        public HomeDrawerViewAdapter(Context context, ArrayList<AppFeatures.AppFeature> appFeatures) {
            super(context, R.layout.home_drawer_list_item_1, appFeatures);
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

