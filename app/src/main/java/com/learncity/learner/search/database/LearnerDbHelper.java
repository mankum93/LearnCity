package com.learncity.learner.search.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.learncity.learner.search.model.request.TutorRequestRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by DJ on 6/17/2017.
 */

public class LearnerDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "LearnerDbHelper";

    private static final int VERSION  = 1;
    public static final String DATABASE_NAME = "learner.db";

    public LearnerDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Request records table.
        LearnerDbHelper.createTutorRequestRecordsTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void createTutorRequestRecordsTable(SQLiteDatabase db){

        db.execSQL("CREATE TABLE IF NOT EXISTS " + LearnerDbSchema.TutorRequestRecordsTable.NAME + "(" +
                LearnerDbSchema.RequestRecordsTable.cols.TO + " PRIMARY KEY "+ "," +
                LearnerDbSchema.RequestRecordsTable.cols.REQUEST_TYPE + "," +
                LearnerDbSchema.RequestRecordsTable.cols.TIMESTAMP + " NOT NULL UNIQUE " + "," +
                LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_NAME + " NOT NULL " + "," +
                LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_LOCATION + "," +
                LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_RATING + "," +
                LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_SUBJECTS + "," +
                LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_TYPE + " NOT NULL " + ")"
        );

        Log.d(TAG, "Tutor Request records table created.");
    }

    private static ContentValues getContentValues(TutorRequestRecord record){

        ContentValues values = new ContentValues();

        values.put(LearnerDbSchema.RequestRecordsTable.cols.TO, record.getTo().toString());
        values.put(LearnerDbSchema.RequestRecordsTable.cols.REQUEST_TYPE, record.getRequestType());
        values.put(LearnerDbSchema.RequestRecordsTable.cols.TIMESTAMP, record.getTimeStamp());

        values.put(LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_NAME, record.getTutorName());
        values.put(LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_LOCATION, record.getTutorLocation());
        values.put(LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_RATING, record.getTutorRating());
        values.put(LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_SUBJECTS, record.getSubjects());
        values.put(LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_TYPE, record.getTutorTypes());

        return values;
    }

    public static void insertTutorRequestRecordToDatabase(SQLiteDatabase database, TutorRequestRecord record){
        database.insert(LearnerDbSchema.TutorRequestRecordsTable.NAME, null, getContentValues(record));
    }

    public static final int SORT_ORDER_ASC = 0x002;
    public static final int SORT_ORDER_DESC = 0x004;
    public static final int SORT_ORDER_NONE = 0x008;
    public static List<TutorRequestRecord> getAllTutorRequestRecordsFromDb(SQLiteDatabase db, int sortOrder){


        List<TutorRequestRecord> tutorRequestRecordList;


        // Cursor for the Tutor Request records
        Cursor cReqRds = null;

        try{

            switch(sortOrder){

                case SORT_ORDER_ASC:
                    cReqRds = db.rawQuery(
                            "SELECT * FROM " + LearnerDbSchema.TutorRequestRecordsTable.NAME
                                    + " ORDER BY " + LearnerDbSchema.RequestRecordsTable.cols.TIMESTAMP + " ASC "
                            , null);
                    break;

                case SORT_ORDER_DESC:
                    cReqRds = db.rawQuery(
                            "SELECT * FROM " + LearnerDbSchema.TutorRequestRecordsTable.NAME
                                    + " ORDER BY " + LearnerDbSchema.RequestRecordsTable.cols.TIMESTAMP + " DESC "
                            , null);
                    break;

                case SORT_ORDER_NONE:
                    cReqRds = db.rawQuery(
                            "SELECT * FROM " + LearnerDbSchema.TutorRequestRecordsTable.NAME
                            , null);
                    break;

                default:
                    // No such(the provided one) order valid.
                    Log.w(TAG, "The provided sort order: " + sortOrder + " is invalid.");
                    cReqRds = db.rawQuery(
                            "SELECT * FROM " + LearnerDbSchema.TutorRequestRecordsTable.NAME
                            , null);
                    break;
            }

            if(!cReqRds.moveToFirst()){
                // No data yet.
                Log.d(TAG, "No message record found.");
                return null;
            }

            else{
                tutorRequestRecordList = new LinkedList<>();

                TutorRequestRecord record;

                do{
                    // Construct the Tutor Request
                    record = TutorRequestRecord.Builder.newInstance(
                            UUID.fromString(cReqRds.getString(cReqRds.getColumnIndex(LearnerDbSchema.RequestRecordsTable.cols.TO))),
                            cReqRds.getLong(cReqRds.getColumnIndex(LearnerDbSchema.RequestRecordsTable.cols.TIMESTAMP)),
                            cReqRds.getInt(cReqRds.getColumnIndex(LearnerDbSchema.RequestRecordsTable.cols.REQUEST_TYPE)),
                            cReqRds.getString(cReqRds.getColumnIndex(LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_NAME)),
                            cReqRds.getString(cReqRds.getColumnIndex(LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_SUBJECTS)),
                            cReqRds.getString(cReqRds.getColumnIndex(LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_TYPE)))
                            .withTutorLocation(cReqRds.getString(cReqRds.getColumnIndex(LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_LOCATION)))
                            .withTutorRating(cReqRds.getInt(cReqRds.getColumnIndex(LearnerDbSchema.TutorRequestRecordsTable.cols.TUTOR_RATING)))
                            .build();

                    tutorRequestRecordList.add(record);


                }while(cReqRds.moveToNext());
            }
        }
        finally{
            if(cReqRds != null){
                cReqRds.close();
            }
        }

        return tutorRequestRecordList;

    }
}
