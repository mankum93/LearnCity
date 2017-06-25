package com.learncity.tutor.jobs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.learncity.tutor.jobs.database.JobsDbHelper;
import com.learncity.tutor.jobs.model.JobPosting;
import com.learncity.tutor.jobs.model.JobRequest;
import com.learncity.util.DataSetObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DJ on 6/17/2017.
 */

public class Repository {

    private static final String TAG = Repository.class.getSimpleName();

    /**
     * Serves as a central repo for Jobs(requests and postings) that can be retrieved by components on demand.
     */
    private List<JobRequest> jobRequests;
    private List<JobPosting> jobPostings;

    private List<DataSetObserver<JobRequest>> jobRequestsObservers = new ArrayList<>();
    private List<DataSetObserver<JobPosting>> jobPostingsObservers = new ArrayList<>();

    public SQLiteDatabase db;
    public JobsDbHelper helper;

    private Context context;

    private static Repository repoInstance;

    public Repository(@NonNull Context context) {

        this.context = context;

        helper = new JobsDbHelper(context);
        db = helper.getWritableDatabase();

        // Load the Job requests from the Db.
        jobRequests = JobsDbHelper.getAllJobRequestsFromDb(db, JobsDbHelper.SORT_ORDER_DESC);
        jobPostings = JobsDbHelper.getAllJobPostingsFromDb(db, JobsDbHelper.SORT_ORDER_DESC);
    }

    public static Repository getRepository(@NonNull Context context){

        if(repoInstance == null){

            repoInstance = new Repository(context);
        }
        else{
            // Refresh context.
            repoInstance.context = context;
        }
        return repoInstance;
    }

    public static Repository getRepository(){

        return repoInstance;
    }

    public List<JobRequest> getJobRequests() {
        return jobRequests;
    }

    public void setJobRequests(@NonNull List<JobRequest> jobRequests) {
        this.jobRequests = jobRequests;

        // New records, inform all the observers.
        for(DataSetObserver<JobRequest> observer : this.jobRequestsObservers){
            observer.onChanged(this.jobRequests);
        }
    }

    public List<JobPosting> getJobPostings() {
        return jobPostings;
    }

    public void setJobPostings(@NonNull List<JobPosting> jobPostings) {
        this.jobPostings = jobPostings;

        // New records, inform all the observers.
        for(DataSetObserver<JobPosting> observer : this.jobPostingsObservers){
            observer.onChanged(this.jobPostings);
        }
    }

    public void updateJobRequestRecords(@NonNull List<JobRequest> jobRequests) {
        if(this.jobRequests == null){
            jobRequests = new ArrayList<>();
        }
        if(!jobRequests.isEmpty()){
            this.jobRequests.addAll(0, jobRequests);

            // Notify the observers
            for(DataSetObserver<JobRequest> observer : this.jobRequestsObservers){
                observer.onItemRangeInserted(0, jobRequests.size());
            }
        }
    }

    public void updateJobRequestRecords(@NonNull JobRequest jobRequest) {
        if(this.jobRequests == null){
            jobRequests = new ArrayList<>();
        }
        this.jobRequests.add(0, jobRequest);

        // Notify the observers
        for(DataSetObserver<JobRequest> observer : this.jobRequestsObservers){
            observer.onItemRangeInserted(0, 1);
        }
    }

    public void updateJobPostingRecords(@NonNull List<JobPosting> jobPostings) {
        if(this.jobPostings == null){
            jobPostings = new ArrayList<>();
        }
        if(!jobPostings.isEmpty()){
            this.jobPostings.addAll(0, jobPostings);

            // Notify the observers
            for(DataSetObserver<JobPosting> observer : this.jobPostingsObservers){
                observer.onItemRangeInserted(0, jobPostings.size());
            }
        }
    }

    public void updateJobRequestRecords(@NonNull JobPosting jobPosting) {

        if(this.jobPostings == null){
            jobPostings = new ArrayList<>();
        }
        this.jobPostings.add(0, jobPosting);

        // Notify the observers
        for(DataSetObserver<JobPosting> observer : this.jobPostingsObservers){
            observer.onItemRangeInserted(0, jobPostings.size());
        }
    }

    public void unregisterJobRequestsObserver(DataSetObserver<JobRequest> jobRequestRecordsObserver) {
        jobRequestsObservers.remove(jobRequestRecordsObserver);
    }

    public void registerJobRequestsObserver(DataSetObserver<JobRequest> jobRequestRecordsObserver) {
        if(jobRequestRecordsObserver != null){
            this.jobRequestsObservers.add(jobRequestRecordsObserver);
        }
    }

    public void unregisterJobPostingsObserver(DataSetObserver<JobPosting> jobPostingRecordsObserver) {
        jobPostingsObservers.remove(jobPostingRecordsObserver);
    }

    public void registerJobPostingsObserver(DataSetObserver<JobPosting> jobPostingRecordsObserver) {
        if(jobPostingRecordsObserver != null){
            this.jobPostingsObservers.add(jobPostingRecordsObserver);
        }
    }
}
