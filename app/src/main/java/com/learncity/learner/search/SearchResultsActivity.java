package com.learncity.learner.search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.learncity.learncity.R;
import com.learncity.tutor.account.TutorAccount;
import com.learncity.util.ArraysUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DJ on 3/4/2017.
 */

public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = SearchResultsActivity.class.getSimpleName();

    public static final String SEARCHED_ACCOUNTS = "SEARCHED_ACCOUNTS";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutor_search_results);

        //Get the Recycler View
        RecyclerView searchResultsView = (RecyclerView) findViewById(R.id.search_results);

        List<TutorAccount> list = new ArrayList<>(10);
        for(Parcelable p : getIntent ().getParcelableArrayExtra(SEARCHED_ACCOUNTS)){
            list.add((TutorAccount)p);
        }
        //Set the Adapter on this Recycler View
        SearchResultsAdapter adapter = new SearchResultsAdapter(list, this);
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
            return new SearchResultViewHolder(context, v);
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

        private static Drawable profilePicPlaceholderDrawable;

        private TextView tutorName;
        private TextView skillSet;
        private TextView tutorTypes;
        private TextView location;
        private ImageView displayPic;
        private SimpleRatingBar rating;

        private ImageButton requestTutorButton;

        private static View.OnClickListener requestTutorsButtonListener;

        public SearchResultViewHolder(Context context, View itemView){
            super(itemView);

            // Dissect the view
            tutorName = (TextView) itemView.findViewById(R.id.tutor_name);
            skillSet = (TextView) itemView.findViewById(R.id.skill_set);
            tutorTypes = (TextView) itemView.findViewById(R.id.tutor_types);
            location = (TextView) itemView.findViewById(R.id.location);

            displayPic = (ImageView) itemView.findViewById(R.id.display_pic);

            rating = (SimpleRatingBar) itemView.findViewById(R.id.tutor_rating);

            requestTutorButton = (ImageButton) itemView.findViewById(R.id.request_tutor);

            if(profilePicPlaceholderDrawable == null){
                profilePicPlaceholderDrawable = ContextCompat.getDrawable(context, R.drawable.user_pic_placeholder);
            }
        }

        public void bindProfileToView(final TutorAccount account){

            // Now, bind each part
            tutorName.setText(account.getProfile().getName());

            String[] skillSet1 = account.getProfile().getDisciplines();
            skillSet.setText(skillSet1 == null || skillSet1.length == 0 ? "" : ArraysUtil.convertArrayToString(account.getProfile().getDisciplines(), ", "));

            String[] tutorTypes1 = account.getProfile().getTutorTypes();
            tutorTypes.setText(tutorTypes1 == null || tutorTypes1.length == 0 ? "" : ArraysUtil.convertArrayToString(account.getProfile().getTutorTypes(), ", "));

            String shortFormattedAddress = account.getLocationInfo() == null ? null : account.getLocationInfo().getShortFormattedAddress();
            location.setText(shortFormattedAddress == null || shortFormattedAddress.isEmpty() ? "" : shortFormattedAddress);

            int r = account.getProfile().getRating();
            rating.setRating(r);

            // Binding the placeholder pic
            displayPic.setImageDrawable(profilePicPlaceholderDrawable);
            // TODO: Bind the display pic to actual user DP

            if(requestTutorsButtonListener == null){
                requestTutorsButtonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Tutor requested: " + account.getProfile());
                        // TODO: Send a notification to the Tutor
                    }
                };
            }
            requestTutorButton.setOnClickListener(requestTutorsButtonListener);
        }
    }
}
