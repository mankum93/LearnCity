package com.learncity.tutor.jobs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learncity.learncity.R;
import com.learncity.tutor.jobs.model.JobPosting;
import com.learncity.tutor.jobs.model.JobRequest;
import com.learncity.util.ArraysUtil;
import com.learncity.util.DataSetObserver;
import com.learncity.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DJ on 6/25/2017.
 */

public class JobPostingsFragment extends Fragment {

    private RecyclerView jobPostings;
    private LinearLayoutManager layoutManager;
    private Repository repo;
    private DataSetObserver<JobPosting> jobPostingRecordsObserver;
    private JobPostingsRecyclerViewAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        repo = Repository.getRepository(getContext().getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_job_postings, container, false);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        jobPostings = (RecyclerView) view.findViewById(R.id.view_pager_job_postings);

        // Retrieve the list of Job Posting records.
        List<JobPosting> jobPostingList = repo.getJobPostings();

        // Setup the LayoutManager
        layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        jobPostings.setLayoutManager(layoutManager);

        // Setup the Adapter
        adapter = new JobPostingsRecyclerViewAdapter(jobPostingList, getContext().getApplicationContext());
        jobPostings.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(this.jobPostingRecordsObserver == null){
            // Register Job Postings observers with the Repo.
            jobPostingRecordsObserver = new DataSetObserver<JobPosting>() {

                @Override
                public void onChanged(@NonNull List<JobPosting> newTutorPostingRecords) {
                    adapter.setTutorJobPostingRecordList(newTutorPostingRecords);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    adapter.notifyItemRangeChanged(positionStart, itemCount);
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                    // Do nothing
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    adapter.notifyItemRangeInserted(positionStart, itemCount);
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    adapter.notifyItemRangeRemoved(positionStart, itemCount);
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    // TODO: Yet to implement.
                }
            };

            repo.registerJobPostingsObserver(jobPostingRecordsObserver);
        }
        if(repo.getJobRequests() != null){
            adapter.setTutorJobPostingRecordList(repo.getJobPostings());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        repo.unregisterJobPostingsObserver(jobPostingRecordsObserver);

        super.onStop();
    }

    // Recycler View Adapter----------------------------------------------------------------------------------------------

    private static class JobPostingsRecyclerViewAdapter extends RecyclerView.Adapter<JobPostingsViewHolder>{

        private List<JobPosting> jobPostingList;
        private Context context;

        public JobPostingsRecyclerViewAdapter(List<JobPosting> jobPostingList, @NonNull Context context) {
            if(jobPostingList == null){
                this.jobPostingList = new ArrayList<>();
            }
            else{
                this.jobPostingList = jobPostingList;
            }

            this.context = context;
        }

        @Override
        public JobPostingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // Create the view that shall hold the result
            View v = LayoutInflater.from(context).inflate(R.layout.item_tutor_job_posting, parent, false);
            return new JobPostingsViewHolder(context, v);
        }


        @Override
        public void onBindViewHolder(JobPostingsViewHolder holder, int position) {
            holder.bindJobPostingsRecordToView(jobPostingList.get(position));
        }

        @Override
        public int getItemCount() {
            return jobPostingList.size();
        }

        public void refreshJobPostingRecordList(@NonNull List<JobPosting> jobPostingRecordList) {
            this.jobPostingList.clear();
            this.jobPostingList.addAll(jobPostingRecordList);
        }

        public void setTutorJobPostingRecordList(@NonNull List<JobPosting> jobPostingRecordList) {
            this.jobPostingList = jobPostingRecordList;
        }
    }

    private static class JobPostingsViewHolder extends RecyclerView.ViewHolder{

        private TextView requesterNameAndLocation;
        private TextView subjects;
        private TextView timeRequested;

        private Context context;

        public JobPostingsViewHolder(@NonNull Context context, @NonNull View itemView){
            super(itemView);

            this.context = context;

            // Dissect the view
            requesterNameAndLocation = (TextView) itemView.findViewById(R.id.requester_name_and_location);
            subjects = (TextView) itemView.findViewById(R.id.subjects);
            timeRequested = (TextView) itemView.findViewById(R.id.timestamp);

        }

        public void bindJobPostingsRecordToView(@NonNull final JobPosting jobPosting){

            // Now, bind each part

            final String shortFormattedAddress = jobPosting.getLocation();

            requesterNameAndLocation.setText(context.getResources().getString(R.string.name_and_location,
                    jobPosting.getPosterName(),
                    shortFormattedAddress == null || shortFormattedAddress.isEmpty() ? "" : shortFormattedAddress));

            final String skillSet1 = jobPosting.getSubjects();
            subjects.setText(ArraysUtil.convertArrayToString(skillSet1.split("__,__"), ", "));

            // TODO: Make this timestamp something like, "Posted x hrs./days ago" as that makes sense
            // as a Job posting.
            timeRequested.setText(DateTimeUtils.timeMillisToHH_MMFormat(jobPosting.getJobPostingTimeStamp(), "hh:mm a"));
        }
    }
}
