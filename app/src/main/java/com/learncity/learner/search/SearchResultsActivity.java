package com.learncity.learner.search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.learncity.backend.messagingApi.model.JsonMap;
import com.learncity.backend.messagingApi.model.Message;
import com.learncity.learncity.R;
import com.learncity.learner.Repository;
import com.learncity.learner.search.database.LearnerDbHelper;
import com.learncity.learner.search.model.request.TutorRequestRecord;
import com.learncity.tutor.account.TutorAccount;
import com.learncity.tutor.account.profile.model.TutorProfile;
import com.learncity.util.ArrayUtils;
import com.learncity.util.IdUtils;
import com.learncity.generic.learner.account.account_mgt.framework.AccountManager;

import java.io.IOException;
import java.util.List;

import static com.learncity.LearnCityApplication.BACKEND_ROOT_URL;

/**
 * Created by DJ on 3/4/2017.
 */

public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = SearchResultsActivity.class.getSimpleName();

    public static final String SEARCHED_ACCOUNTS = "SEARCHED_ACCOUNTS";
    public static final String SEARCH_QUERY_TODO = "SEARCH_QUERY_TODO";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutor_search_results);

        //Get the Recycler View
        RecyclerView searchResultsView = (RecyclerView) findViewById(R.id.search_results);

        // TODO: DON'T retrieve the searched results through deserialization but use cache
        // as search is volatile..quite.
        List<TutorAccount> list = ArrayUtils.toList(null, getIntent ().getParcelableArrayExtra(SEARCHED_ACCOUNTS));

        //Set the Adapter on this Recycler View
        String subjectsSearched = getIntent().getStringExtra(SEARCH_QUERY_TODO);
        SearchResultsAdapter adapter = new SearchResultsAdapter(list, this, subjectsSearched);
        searchResultsView.setAdapter(adapter);
        searchResultsView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStop() {
        // If the process is killed then cleanup will happen automatically
        // that is, classes will get unloaded but if not we do the cleanup here.
        SearchResultViewHolder.clearStatics();
        super.onStop();
    }

    private static class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultViewHolder>{

        // Source of Adapter data - from the parent Activity
        List<TutorAccount> accounts;
        Context context;
        String subjectsSearched;

        private Repository repo = Repository.getRepository();

        public SearchResultsAdapter(List<TutorAccount> accounts, Context context, String subjectsSearched) {
            this.accounts = accounts;
            this.context = context;
            this.subjectsSearched = subjectsSearched;
        }

        @Override
        public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // Create the view that shall hold the result
            View v = LayoutInflater.from(context).inflate(R.layout.item_tutor_search_result, parent, false);

            final SearchResultViewHolder holder = new SearchResultViewHolder(v);

            holder.requestTutorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TutorAccount tutorAccount = holder.tutorAccount;
                    TutorProfile tutorProfile = tutorAccount.getProfile();

                    Log.d(TAG, "Tutor requested: " + tutorProfile);

                    // Disable this button.
                    // TODO: When loading search results again, this result
                    // should be come as already disabled once disabled here
                    // or not shown at all.
                    v.setEnabled(false);

                    // Create the message to be sent.
                    PersistableBundleCompat bundle = new PersistableBundleCompat();
                    bundle.putString("to", tutorAccount.getEmailBasedUUID().toString());
                    bundle.putString("from", IdUtils
                            .getType5UUID(AccountManager.getAccountDetails(context).getEmailID()));
                    bundle.putInt("messageType", com.learncity.learner.search.model.message.Message.TUTORING_REQUEST);
                    bundle.putString("subjects", SearchResultsAdapter.this.subjectsSearched);
                        /*Message request = new Message();
                        request.setTo(tutorAccount.getEmailBasedUUID().toString());
                        request.setFrom(Account
                                .generateType5UUID(
                                AccountManager.getAccountDetails(context).getEmailID())
                                .toString());
                        request.setMessageType(com.learncity.learner.search.model.message.Message.TUTORING_REQUEST);*/

                    // Schedule this job now.
                    TutoringRequestForwardingJob.schedule(bundle);

                    // Assuming that the job request will be sent successfully in
                    // future(bc of exponential backoff), we can create a record
                    // of this sent request. Do this here only.

                    // Prepare the Tutor request record to be inserted.
                    String shortFormattedAddress = tutorAccount.getLocationInfo() == null ? null : tutorAccount.getLocationInfo().getShortFormattedAddress();
                    TutorRequestRecord record = TutorRequestRecord.Builder.newInstance(
                            tutorAccount.getEmailBasedUUID(),
                            System.currentTimeMillis(),
                            com.learncity.learner.search.model.message.Message.TUTORING_REQUEST,
                            tutorProfile.getName(),
                            ArrayUtils.convertArrayToString(tutorProfile.getDisciplines()),
                            ArrayUtils.convertArrayToString(tutorProfile.getTutorTypes()))
                            .withTutorRating(tutorProfile.getRating())
                            .withTutorLocation(shortFormattedAddress)
                            .build();

                    // Insert this record to Db
                    LearnerDbHelper.insertTutorRequestRecordToDatabase(repo.db, record);

                    // Update the Repo with this new data.
                    repo.updateTutorRequestRecords(record);
                }
            });

            return holder;

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

        Button requestTutorButton;

        TutorAccount tutorAccount;

        static void clearStatics(){
            profilePicPlaceholderDrawable = null;
        }

        public SearchResultViewHolder(@NonNull View itemView){
            super(itemView);

            // Dissect the view
            tutorName = (TextView) itemView.findViewById(R.id.tutor_name);
            skillSet = (TextView) itemView.findViewById(R.id.skill_set);
            tutorTypes = (TextView) itemView.findViewById(R.id.tutor_types);
            location = (TextView) itemView.findViewById(R.id.location);

            displayPic = (ImageView) itemView.findViewById(R.id.display_pic);

            rating = (SimpleRatingBar) itemView.findViewById(R.id.tutor_rating);

            requestTutorButton = (Button) itemView.findViewById(R.id.request_tutor);

            if(profilePicPlaceholderDrawable == null){
                profilePicPlaceholderDrawable = ContextCompat.getDrawable(itemView.getContext().getApplicationContext(), R.drawable.user_pic_placeholder);
            }
        }

        public void bindProfileToView(@NonNull final TutorAccount account){

            this.tutorAccount = account;

            TutorProfile tutorProfile = account.getProfile();

            // Now, bind each part
            tutorName.setText(tutorProfile.getName());

            final String[] skillSet1 = tutorProfile.getDisciplines();

            skillSet.setText(skillSet1 == null || skillSet1.length == 0 ? "" : ArrayUtils.convertArrayToString(skillSet1, ", "));

            final String[] tutorTypes1 = tutorProfile.getTutorTypes();
            tutorTypes.setText(tutorTypes1 == null || tutorTypes1.length == 0 ? "" : ArrayUtils.convertArrayToString(tutorTypes1, ", "));

            final String shortFormattedAddress = account.getLocationInfo() == null ? null : account.getLocationInfo().getShortFormattedAddress();
            location.setText(shortFormattedAddress == null || shortFormattedAddress.isEmpty() ? "" : shortFormattedAddress);

            final int r = tutorProfile.getRating();
            rating.setRating(r);

            // Binding the placeholder pic
            displayPic.setImageDrawable(profilePicPlaceholderDrawable);
            // TODO: Bind the display pic to actual user DP

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

            JsonMap dataPayload = new JsonMap();
            dataPayload.put("subjects", options.getString("subjects", ""));
            request.setDataPayload(dataPayload);

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
                    .setRequiresCharging(false)
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

            tutoringRequestForwardingClient = builder.build();
        }
    }
}
