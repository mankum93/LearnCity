package com.learncity.learner.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.learncity.learncity.R;
import com.learncity.tutor.account.TutorAccount;
import com.learncity.tutor.account.profile.model.TutorProfile;
import com.learncity.util.ArraysUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DJ on 3/4/2017.
 */

public class SearchResultsActivity extends AppCompatActivity {

    public static final String SEARCHED_ACCOUNTS = "SEARCHED_ACCOUNTS";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutor_search_results);

        //Get the Recycler View
        RecyclerView searchResultsView = (RecyclerView) findViewById(R.id.search_results);

        //Set the Adapter on this Recycler View
        SearchResultsAdapter adapter = new SearchResultsAdapter(Arrays.asList((TutorAccount[])getIntent().getParcelableArrayExtra(SEARCHED_ACCOUNTS)), this);
        searchResultsView.setAdapter(adapter);
        searchResultsView.setLayoutManager(new LinearLayoutManager(this));
    }

    private static class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultViewHolder>{

        // Source of Adapter data - from the parent Activity
        List<TutorAccount> accounts;
        Context context;

        public SearchResultsAdapter(List<TutorAccount> accounts, Context context) {
            this.accounts = accounts;
            this.context = context;
        }

        @Override
        public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // Create the view that shall hold the result
            View v = LayoutInflater.from(context).inflate(R.layout.view_tutor_search_result, parent, false);
            return new SearchResultViewHolder(v);
        }


        @Override
        public void onBindViewHolder(SearchResultViewHolder holder, int position) {
            holder.bindProfileToView(accounts.get(position));
        }

        @Override
        public int getItemCount() {
            return accounts.size();
        }
    }

    private static class SearchResultViewHolder extends RecyclerView.ViewHolder{
        public SearchResultViewHolder(View itemView){
            super(itemView);
        }

        public void bindProfileToView(TutorAccount account){
            // Dissect the view
            TextView tutorName = (TextView) itemView.findViewById(R.id.tutor_name);
            TextView skillSet = (TextView) itemView.findViewById(R.id.skill_set);
            TextView tutorTypes = (TextView) itemView.findViewById(R.id.tutor_types);
            TextView location = (TextView) itemView.findViewById(R.id.location);

            ImageView displayPic = (ImageView) itemView.findViewById(R.id.display_pic);

            RatingBar rating = (RatingBar) itemView.findViewById(R.id.tutor_rating);

            // Now, bind each part
            tutorName.setText(account.getProfile().getName());
            skillSet.setText(ArraysUtil.convertArrayToString(account.getProfile().getDisciplines(), ", "));
            tutorTypes.setText(ArraysUtil.convertArrayToString(account.getProfile().getDisciplines(), ", "));
            location.setText(account.getLocationInfo().getShortFormattedAddress());
            rating.setNumStars(account.getProfile().getRating());
            // TODO: Bind the display pic
        }
    }
}
