package com.learncity.generic.learner.account.create.ver1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.learncity.generic.learner.account.profile.database.ProfileDbHelperVer1;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.util.ver1.BaseAccountCreationTaskVer1;

/**
 * Created by DJ on 2/6/2017.
 */

public class SQLiteAccountCreationTaskVer1 extends BaseAccountCreationTaskVer1 {

    private static final String TAG = "SQLiteACCreationTask";

    private GenericLearnerProfile profile;

    private Context context;

    private int returnCode;

    public SQLiteAccountCreationTaskVer1(Context context, GenericLearnerProfile profile){
        this.context = context;
        this.profile = profile;
    }

    @Override
    public int performAccountCreation() {
        Log.d(TAG, "SQLiteAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Performing AC creation..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        //Get the writable database
        SQLiteDatabase db = new ProfileDbHelperVer1(context, profile.getCurrentStatus()).getWritableDatabase();
        try{
            //Insert into the database
            ProfileDbHelperVer1.addProfileToDatabase(db, profile);
        }
        catch(SQLiteException sqe){
            Log.d(TAG, "SQLiteAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Profile couldn't be written to SQLite Db" +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            sqe.printStackTrace();
            //Account couldn't be created on SQLite Db
            returnCode = ACCOUNT_CREATION_FAILED;

            return returnCode;
        }
        finally{
            //Close the connection
            db.close();
        }

        Log.d(TAG, "SQLiteAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Profile written to SQLite Db" +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        //Account successfully created
        returnCode = ACCOUNT_CREATION_COMPLETED;

        return returnCode;
    }

    @Override
    public void performCleanup() {
        Log.d(TAG, "SQLiteAccountCreationTask.performCleanup(): " + "\n" + "MESSAGE: Cleaning up SQLiteAccountCreationTask..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        profile = null;
        context = null;
        accountCreationTaskListener = null;
    }
}
