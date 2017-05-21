package com.learncity.learner.search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.learncity.backend.messagingApi.MessagingApi;
import com.learncity.backend.messagingApi.model.Message;
import com.learncity.generic.learner.account.Account;
import com.learncity.learncity.R;
import com.learncity.tutor.account.TutorAccount;
import com.learncity.tutor.account.profile.model.TutorProfile;
import com.learncity.util.ArraysUtil;
import com.learncity.util.account_management.impl.AccountManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.learncity.LearnCityApplication.BACKEND_ROOT_URL;

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

        private Context context;

        // A Tutor request forwarding client.
        private static TutoringRequestForwardingClient client = new TutoringRequestForwardingClient();

        private static View.OnClickListener requestTutorsButtonListener;

        public SearchResultViewHolder(Context context, View itemView){
            super(itemView);

            this.context = context;

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

            final TutorProfile profile = account.getProfile();

            // Now, bind each part
            tutorName.setText(profile.getName());

            String[] skillSet1 = profile.getDisciplines();
            skillSet.setText(skillSet1 == null || skillSet1.length == 0 ? "" : ArraysUtil.convertArrayToString(profile.getDisciplines(), ", "));

            String[] tutorTypes1 = profile.getTutorTypes();
            tutorTypes.setText(tutorTypes1 == null || tutorTypes1.length == 0 ? "" : ArraysUtil.convertArrayToString(profile.getTutorTypes(), ", "));

            String shortFormattedAddress = account.getLocationInfo() == null ? null : account.getLocationInfo().getShortFormattedAddress();
            location.setText(shortFormattedAddress == null || shortFormattedAddress.isEmpty() ? "" : shortFormattedAddress);

            int r = profile.getRating();
            rating.setRating(r);

            // Binding the placeholder pic
            displayPic.setImageDrawable(profilePicPlaceholderDrawable);
            // TODO: Bind the display pic to actual user DP

            if(requestTutorsButtonListener == null){
                requestTutorsButtonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Tutor requested: " + profile);

                        // Create the message to be sent.
                        PersistableBundleCompat bundle = new PersistableBundleCompat();
                        bundle.putString("to", account.getEmailBasedUUID().toString());
                        bundle.putString("from", Account
                                .generateType5UUID(AccountManager.getAccountDetails(context).getEmailID())
                                .toString());
                        bundle.putInt("messageType", com.learncity.learner.search.model.Message.TUTORING_REQUEST);
                        /*Message request = new Message();
                        request.setTo(account.getEmailBasedUUID().toString());
                        request.setFrom(Account
                                .generateType5UUID(
                                AccountManager.getAccountDetails(context).getEmailID())
                                .toString());
                        request.setMessageType(com.learncity.learner.search.model.Message.TUTORING_REQUEST);*/

                        // Schedule this job now.
                        TutoringRequestForwardingJob.schedule(bundle);
                    }
                };
            }
            requestTutorButton.setOnClickListener(requestTutorsButtonListener);
        }
    }

    public static class TutoringRequestForwardingJobCreator implements JobCreator{
        @Override
        public Job create(String tag) {
            switch (tag) {
                case TutoringRequestForwardingJob.TAG:
                    return new TutoringRequestForwardingJob();
                default:
                    return null;
            }
        }
    }

    private static class TutoringRequestForwardingJob extends Job{

        public static final String TAG = "job_tutoring_request_tag";

        private static final TutoringRequestForwardingClient client = new TutoringRequestForwardingClient();

        @NonNull
        @Override
        protected Result onRunJob(Params params) {

            PersistableBundleCompat options = params.getExtras();

            Message request = new Message();
            request.setTo(options.getString("to", ""));
            request.setFrom(options.getString("from", ""));
            request.setMessageType(options.getInt("messageType", -1));

            try {
                client.sendTutoringRequest(request);
            } catch (IOException e) {
                e.printStackTrace();
                return Result.RESCHEDULE;
            }
            return Result.SUCCESS;
        }

        public static void schedule(PersistableBundleCompat extras) {

            int jobId = new JobRequest.Builder(TAG)
                    .setExecutionWindow(3000L, 7000L)
                    .setBackoffCriteria(5000L, JobRequest.BackoffPolicy.EXPONENTIAL)
                    .setRequiresCharging(true)
                    .setRequiresDeviceIdle(false)
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .setExtras(extras)
                    .setRequirementsEnforced(true)
                    .setPersisted(true)
                    .setUpdateCurrent(true)
                    .build()
                    .schedule();
        }
    }

    private static class TutoringRequestForwardingClient{

        private MessagingApi tutoringRequestForwardingClient;

        public TutoringRequestForwardingClient() {
            initClient();
        }

        public void sendTutoringRequest(Message message) throws IOException{
            tutoringRequestForwardingClient.sendMessage(message).execute();
        }

        private void initClient(){
            MessagingApi.Builder builder = new MessagingApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl(BACKEND_ROOT_URL)
                    .setApplicationName("Learn City")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            tutoringRequestForwardingClient = builder.build();
        }
    }
}
