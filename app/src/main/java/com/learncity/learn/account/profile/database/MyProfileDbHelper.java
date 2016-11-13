package com.learncity.learn.account.profile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;

import static com.learncity.learn.account.profile.database.MyProfileDbSchema.MyProfileTable;

/**
 * Created by DJ on 10/23/2016.
 */

public class MyProfileDbHelper extends SQLiteOpenHelper {

    private static final int VERSION  = 1;
    private static final String DATABASE_NAME = "myProfileBase.db";

    public MyProfileDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + MyProfileTable.NAME + "(" +
                MyProfileTable.cols.NAME + "," +
                MyProfileTable.cols.EMAIL_ID + "," +
                MyProfileTable.cols.PHONE_NO + "," +
                MyProfileTable.cols.IMAGE_PATH + "," +
                MyProfileTable.cols.CURRENT_STATUS + "," +
                MyProfileTable.cols.PASSWORD + ")"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
    private static ContentValues getContentValues(GenericLearnerProfile myProfile){

        ContentValues values = new ContentValues();
        values.put(MyProfileTable.cols.NAME, myProfile.getName());
        values.put(MyProfileTable.cols.EMAIL_ID, myProfile.getEmailID());
        values.put(MyProfileTable.cols.PHONE_NO, myProfile.getPhoneNo());
        values.put(MyProfileTable.cols.IMAGE_PATH, myProfile.getImagePath());
        values.put(MyProfileTable.cols.CURRENT_STATUS, myProfile.getCurrentStatus());
        values.put(MyProfileTable.cols.PASSWORD, myProfile.getPassword());

        return values;
    }

    public static void addProfileToDatabase(SQLiteDatabase database, GenericLearnerProfile profile){
        ContentValues values = getContentValues(profile);

        database.insert(MyProfileTable.NAME, null, values);

    }

    public static void updateProfileInDatabase(SQLiteDatabase database, GenericLearnerProfile profile){

        //Assumption is that the Email ID will be unique for the accounts.
        String emailId = profile.getEmailID();
        ContentValues values = getContentValues(profile);
        database.update(MyProfileTable.NAME, values,
                MyProfileTable.cols.EMAIL_ID + "=?", new String[]{emailId});
    }
}
