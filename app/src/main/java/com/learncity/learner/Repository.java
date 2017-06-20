package com.learncity.learner;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.learncity.learner.search.database.LearnerDbHelper;
import com.learncity.learner.search.model.request.RequestRecord;
import com.learncity.learner.search.model.request.TutorRequestRecord;
import com.learncity.util.DataSetObserver;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by DJ on 6/17/2017.
 */

public class Repository {

    private static final String TAG = Repository.class.getSimpleName();

    /**
     * Serves as a central Tutor request records repo that can be retrieved by components on demand.
     */
    private List<TutorRequestRecord> tutorRequestRecords;

    private List<DataSetObserver> tutorRequestRecordsObservers = new LinkedList<>();

    public SQLiteDatabase db;
    public LearnerDbHelper helper;

    private Context context;

    private static Repository repoInstance;

    public Repository(@NonNull Context context) {

        this.context = context;

        helper = new LearnerDbHelper(context);
        db = helper.getWritableDatabase();

        // Load the Tutor request records from the Db.
        tutorRequestRecords = LearnerDbHelper.getAllTutorRequestRecordsFromDb(db, LearnerDbHelper.SORT_ORDER_DESC);
    }

    public static Repository getRepository(@NonNull Context context){

        if(repoInstance == null){

            if(context == null){
                Log.e(TAG, "Context can't be null.");
                return null;
            }
            repoInstance = new Repository(context);
        }
        else{
            // Refresh context.
            if(context != null){
                repoInstance.context = context;
            }
        }
        return repoInstance;
    }

    public static Repository getRepository(){

        return repoInstance;
    }

    public List<TutorRequestRecord> getTutorRequestRecords() {
        return tutorRequestRecords;
    }

    public void setTutorRequestRecords(List<TutorRequestRecord> tutorRequestRecords) {
        this.tutorRequestRecords = tutorRequestRecords;
    }

    public void updateTutorRequestRecords(List<TutorRequestRecord> tutorRequestRecords) {
        if(this.tutorRequestRecords == null){
            tutorRequestRecords = new LinkedList<>();
        }
        if(tutorRequestRecords != null && !tutorRequestRecords.isEmpty()){
            this.tutorRequestRecords.addAll(0, tutorRequestRecords);

            // Notify the observers
            for(DataSetObserver observer : this.tutorRequestRecordsObservers){
                observer.onItemRangeInserted(0, tutorRequestRecords.size());
            }
        }
    }

    public void updateTutorRequestRecords(TutorRequestRecord tutorRequestRecord) {
        if(this.tutorRequestRecords == null){
            tutorRequestRecords = new LinkedList<>();
        }
        if(tutorRequestRecord != null){
            this.tutorRequestRecords.add(0, tutorRequestRecord);

            // Notify the observers
            for(DataSetObserver observer : this.tutorRequestRecordsObservers){
                observer.onItemRangeInserted(0, 1);
            }
        }
    }

    public void unregisterTutorRequestRecordsObserver(DataSetObserver tutorRequestRecordsObserver) {
        tutorRequestRecordsObservers.remove(tutorRequestRecordsObserver);
    }

    public void registerTutorRequestRecordsObserver(DataSetObserver tutorRequestRecordsObserver) {
        if(tutorRequestRecordsObserver != null){
            this.tutorRequestRecordsObservers.add(tutorRequestRecordsObserver);
        }
    }
}
