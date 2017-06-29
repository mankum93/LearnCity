package com.learncity.tutor.jobs.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.learncity.tutor.jobs.model.Job;
import com.learncity.tutor.jobs.model.JobPosting;
import com.learncity.tutor.jobs.model.JobRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DJ on 6/25/2017.
 */

public class JobsDbHelper extends SQLiteOpenHelper{

    private static final String TAG = "JobsDbHelper";

    private static final int VERSION  = 1;
    public static final String DATABASE_NAME = "tutor.db";

    public JobsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Crete the respective postings and requests tables
        JobsDbHelper.createJobPostingsTable(db);
        JobsDbHelper.createJobRequestsTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void createJobRequestsTable(@NonNull SQLiteDatabase db){

        db.execSQL("CREATE TABLE IF NOT EXISTS " + JobSchema.JobRequestsTable.NAME + "(" +
                JobSchema.Jobs.cols.JOB_ID + " PRIMARY KEY "+ "," +
                JobSchema.Jobs.cols.POSTER_NAME + "," +
                JobSchema.Jobs.cols.LOCATION + " NOT NULL " + "," +
                JobSchema.Jobs.cols.SUBJECTS + " NOT NULL " + "," +
                JobSchema.JobRequestsTable.cols.TIME_REQUESTED + " NOT NULL " + ")"
        );

        Log.d(TAG, "Job Requests table created.");
    }

    public static void createJobPostingsTable(SQLiteDatabase db){

        db.execSQL("CREATE TABLE IF NOT EXISTS " + JobSchema.JobPostingsTable.NAME + "(" +
                JobSchema.Jobs.cols.JOB_ID + " PRIMARY KEY "+ "," +
                JobSchema.Jobs.cols.POSTER_NAME + "," +
                JobSchema.Jobs.cols.LOCATION + " NOT NULL " + "," +
                JobSchema.Jobs.cols.SUBJECTS + " NOT NULL " + "," +
                JobSchema.JobPostingsTable.cols.TIME_POSTED + " NOT NULL " + ")"
        );

        Log.d(TAG, "Job Postings table created.");
    }

    private static ContentValues getContentValues(Job job){

        ContentValues values = new ContentValues();

        values.put(JobSchema.Jobs.cols.JOB_ID, job.getJobId());
        values.put(JobSchema.Jobs.cols.POSTER_NAME, job.getPosterName());
        values.put(JobSchema.Jobs.cols.LOCATION, job.getLocation());
        values.put(JobSchema.Jobs.cols.SUBJECTS, job.getSubjects());

        return values;
    }

    private static ContentValues getContentValues(JobRequest jobRequest){

        ContentValues values = getContentValues((Job)jobRequest);

        values.put(JobSchema.JobRequestsTable.cols.TIME_REQUESTED, jobRequest.getRequestedTimeStamp());

        return values;
    }

    private static ContentValues getContentValues(JobPosting jobPosting){

        ContentValues values = getContentValues((Job)jobPosting);

        values.put(JobSchema.JobPostingsTable.cols.TIME_POSTED, jobPosting.getJobPostingTimeStamp());

        return values;
    }

    public static void insertJobRequestToDb(SQLiteDatabase db, JobRequest jobRequest){
        db.insert(JobSchema.JobRequestsTable.NAME, null, getContentValues(jobRequest));
    }

    public static void insertJobPostingToDb(SQLiteDatabase db, JobRequest jobPosting){
        db.insert(JobSchema.JobPostingsTable.NAME, null, getContentValues(jobPosting));
    }

    public static final int SORT_ORDER_ASC = 0x002;
    public static final int SORT_ORDER_DESC = 0x004;
    public static final int SORT_ORDER_NONE = 0x008;
    public static List<JobRequest> getAllJobRequestsFromDb(SQLiteDatabase db, int sortOrder){

        List<JobRequest> jobRequestList;


        // Cursor for the Job Requests
        Cursor cJobReqs = null;

        try{

            switch(sortOrder){

                case SORT_ORDER_ASC:
                    cJobReqs = db.rawQuery(
                            "SELECT * FROM " + JobSchema.JobRequestsTable.NAME
                                    + " ORDER BY " + JobSchema.JobRequestsTable.cols.TIME_REQUESTED + " ASC "
                            , null);
                    break;

                case SORT_ORDER_DESC:
                    cJobReqs = db.rawQuery(
                            "SELECT * FROM " + JobSchema.JobRequestsTable.NAME
                                    + " ORDER BY " + JobSchema.JobRequestsTable.cols.TIME_REQUESTED + " DESC "
                            , null);
                    break;

                case SORT_ORDER_NONE:
                    cJobReqs = db.rawQuery(
                            "SELECT * FROM " + JobSchema.JobRequestsTable.NAME
                            , null);
                    break;

                default:
                    // No such(the provided one) order valid.
                    Log.w(TAG, "The provided sort order: " + sortOrder + " is invalid.");
                    cJobReqs = db.rawQuery(
                            "SELECT * FROM " + JobSchema.JobRequestsTable.NAME
                            , null);
                    break;
            }

            if(!cJobReqs.moveToFirst()){
                // No data yet.
                Log.d(TAG, "No job request found.");
                return null;
            }

            else{
                jobRequestList = new ArrayList<>(cJobReqs.getCount());

                JobRequest jobRequest;

                do{
                    // Construct the Tutor Request
                    jobRequest = new JobRequest(
                            cJobReqs.getString(cJobReqs.getColumnIndex(JobSchema.Jobs.cols.JOB_ID)),
                            cJobReqs.getString(cJobReqs.getColumnIndex(JobSchema.Jobs.cols.POSTER_NAME)),
                            cJobReqs.getString(cJobReqs.getColumnIndex(JobSchema.Jobs.cols.LOCATION)),
                            cJobReqs.getString(cJobReqs.getColumnIndex(JobSchema.Jobs.cols.SUBJECTS)),
                            cJobReqs.getLong(cJobReqs.getColumnIndex(JobSchema.JobRequestsTable.cols.TIME_REQUESTED))
                            );

                    jobRequestList.add(jobRequest);


                }while(cJobReqs.moveToNext());
            }
        }
        finally{
            if(cJobReqs != null){
                cJobReqs.close();
            }
        }

        return jobRequestList;
    }


    public static List<JobPosting> getAllJobPostingsFromDb(SQLiteDatabase db, int sortOrder){

        List<JobPosting> jobPostingsList;


        // Cursor for the Job Requests
        Cursor cJobPostings = null;

        try{

            switch(sortOrder){

                case SORT_ORDER_ASC:
                    cJobPostings = db.rawQuery(
                            "SELECT * FROM " + JobSchema.JobPostingsTable.NAME
                                    + " ORDER BY " + JobSchema.JobPostingsTable.cols.TIME_POSTED + " ASC "
                            , null);
                    break;

                case SORT_ORDER_DESC:
                    cJobPostings = db.rawQuery(
                            "SELECT * FROM " + JobSchema.JobPostingsTable.NAME
                                    + " ORDER BY " + JobSchema.JobPostingsTable.cols.TIME_POSTED + " DESC "
                            , null);
                    break;

                case SORT_ORDER_NONE:
                    cJobPostings = db.rawQuery(
                            "SELECT * FROM " + JobSchema.JobPostingsTable.NAME
                            , null);
                    break;

                default:
                    // No such(the provided one) order valid.
                    Log.w(TAG, "The provided sort order: " + sortOrder + " is invalid.");
                    cJobPostings = db.rawQuery(
                            "SELECT * FROM " + JobSchema.JobPostingsTable.NAME
                            , null);
                    break;
            }

            if(!cJobPostings.moveToFirst()){
                // No data yet.
                Log.d(TAG, "No job postings found.");
                return null;
            }

            else{
                jobPostingsList = new ArrayList<>(cJobPostings.getCount());

                JobPosting jobPosting;

                do{
                    // Construct the Tutor Request
                    jobPosting = new JobPosting(
                            cJobPostings.getString(cJobPostings.getColumnIndex(JobSchema.Jobs.cols.JOB_ID)),
                            cJobPostings.getString(cJobPostings.getColumnIndex(JobSchema.Jobs.cols.POSTER_NAME)),
                            cJobPostings.getString(cJobPostings.getColumnIndex(JobSchema.Jobs.cols.LOCATION)),
                            cJobPostings.getString(cJobPostings.getColumnIndex(JobSchema.Jobs.cols.SUBJECTS)),
                            cJobPostings.getLong(cJobPostings.getColumnIndex(JobSchema.JobPostingsTable.cols.TIME_POSTED))
                    );

                    jobPostingsList.add(jobPosting);


                }while(cJobPostings.moveToNext());
            }
        }
        finally{
            if(cJobPostings != null){
                cJobPostings.close();
            }
        }

        return jobPostingsList;
    }
}
