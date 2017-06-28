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
import com.learncity.tutor.jobs.model.JobRequest;
import com.learncity.util.ArrayUtils;
import com.learncity.util.DataSetObserver;
import com.learncity.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DJ on 6/25/2017.
 */

public class JobRequestsFragment extends Fragment {

    private RecyclerView jobRequests;
    private LinearLayoutManager layoutManager;
    private Repository repo;
    private DataSetObserver<JobRequest> jobRequestRecordsObserver;
    private JobRequestsRecyclerViewAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        repo = Repository.getRepository(getContext().getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_job_requests, container, false);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        jobRequests = (RecyclerView) view.findViewById(R.id.view_pager_job_requests);

        // Retrieve the list of Tutor request records.
        List<JobRequest> jobRequestList = repo.getJobRequests();

        // Setup the LayoutManager
        layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        jobRequests.setLayoutManager(layoutManager);

        // Setup the Adapter
        adapter = new JobRequestsRecyclerViewAdapter(jobRequestList, getContext().getApplicationContext());
        jobRequests.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(this.jobRequestRecordsObserver == null){
            // Register Tutor request records observers with the Repo.
            jobRequestRecordsObserver = new DataSetObserver<JobRequest>() {

                @Override
                public void onChanged(@NonNull List<JobRequest> newTutorRequestRecords) {
                    adapter.setJobRequestsList(newTutorRequestRecords);
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

            repo.registerJobRequestsObserver(jobRequestRecordsObserver);
        }
        if(repo.getJobRequests() != null){
            adapter.setJobRequestsList(repo.getJobRequests());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        repo.unregisterJobRequestsObserver(jobRequestRecordsObserver);

        super.onStop();
    }

    // Recycler View Adapter----------------------------------------------------------------------------------------------

    private static class JobRequestsRecyclerViewAdapter extends RecyclerView.Adapter<JobRequestsViewHolder>{

        private List<JobRequest> tutorJobRequestRecordList;
        private Context context;

        public JobRequestsRecyclerViewAdapter(List<JobRequest> tutorJobRequestRecordList, @NonNull Context context) {
            if(tutorJobRequestRecordList == null){
                this.tutorJobRequestRecordList = new ArrayList<>();
            }
            else{
                this.tutorJobRequestRecordList = tutorJobRequestRecordList;
            }

            this.context = context;
        }

        @Override
        public JobRequestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // Create the view that shall hold the result
            View v = LayoutInflater.from(context).inflate(R.layout.item_tutor_job_request, parent, false);
            return new JobRequestsViewHolder(context, v);
        }


        @Override
        public void onBindViewHolder(JobRequestsViewHolder holder, int position) {
            holder.bindJobRequestRecordToView(tutorJobRequestRecordList.get(position));
        }

        @Override
        public int getItemCount() {
            return tutorJobRequestRecordList.size();
        }

        public void refreshJobRequestsList(@NonNull List<JobRequest> tutorRequestRecordList) {
            this.tutorJobRequestRecordList.clear();
            this.tutorJobRequestRecordList.addAll(tutorRequestRecordList);
        }

        public void setJobRequestsList(@NonNull List<JobRequest> tutorJobRequestRecordList) {
            this.tutorJobRequestRecordList = tutorJobRequestRecordList;
        }
    }

    private static class JobRequestsViewHolder extends RecyclerView.ViewHolder{

        private TextView requesterNameAndLocation;
        private TextView subjects;
        private TextView timeRequested;

        private Context context;

        public JobRequestsViewHolder(@NonNull Context context, @NonNull View itemView){
            super(itemView);

            this.context = context;

            // Dissect the view
            requesterNameAndLocation = (TextView) itemView.findViewById(R.id.requester_name_and_location);
            subjects = (TextView) itemView.findViewById(R.id.subjects);
            timeRequested = (TextView) itemView.findViewById(R.id.timestamp);

        }

        public void bindJobRequestRecordToView(@NonNull final JobRequest jobRequest){

            // Now, bind each part

            final String shortFormattedAddress = jobRequest.getLocation();

            requesterNameAndLocation.setText(context.getResources().getString(R.string.name_and_location,
                    jobRequest.getPosterName(),
                    shortFormattedAddress == null || shortFormattedAddress.isEmpty() ? "" : shortFormattedAddress));

            final String skillSet1 = jobRequest.getSubjects();
            subjects.setText(ArrayUtils.convertArrayToString(skillSet1.split("__,__"), ", "));

            timeRequested.setText(DateTimeUtils.timeMillisToHH_MMFormat(jobRequest.getRequestedTimeStamp(), "hh:mm a"));
        }
    }
}
