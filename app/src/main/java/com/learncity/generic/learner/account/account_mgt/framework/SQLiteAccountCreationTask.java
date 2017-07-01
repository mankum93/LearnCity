package com.learncity.generic.learner.account.account_mgt.framework;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.learncity.generic.learner.account.profile.database.ProfileDbHelper;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.util.account_management.AbstractTask;
import com.learncity.util.account_management.Result;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by DJ on 2/6/2017.
 */

public class SQLiteAccountCreationTask extends AbstractTask<Void> {

    private static final String TAG = "SQLiteACCreationTask";

    private GenericLearnerProfile profile;

    private Context context;

    private Result<Void> result;

    public SQLiteAccountCreationTask(Context context, GenericLearnerProfile profile){
        this.context = context;
        this.profile = profile;
    }

    @Override
    public Result<Void> performTask() {
        Log.d(TAG, "SQLiteAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Performing AC creation..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        //Get the writable database
        ProfileDbHelper helper = new ProfileDbHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.createProfileTables(db, profile.getCurrentStatus());

        try{
            //Insert into the database
            ProfileDbHelper.addProfileToDatabase(db, profile);
        }
        catch(SQLiteException sqe){
            Log.d(TAG, "SQLiteAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Profile couldn't be written to SQLite Db" +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            sqe.printStackTrace();
            //Account couldn't be created on SQLite Db
            result = Result.RESULT_FAILURE;

            return result;
        }
        finally{
            //Close the connection
            db.close();
        }

        Log.d(TAG, "SQLiteAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Profile written to SQLite Db" +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        //Account successfully created
        result = Result.RESULT_SUCCESS;

        //Broadcast the result of the AC creation process in order to retrieve it later
        EventBus.getDefault().postSticky(new AccountCreationService.ACCreationResult<GenericLearnerProfile>(TASK_COMPLETED, profile));

        return result;
    }

    @Override
    public void performCleanup() {
        Log.d(TAG, "SQLiteAccountCreationTask.performCleanup(): " + "\n" + "MESSAGE: Cleaning up SQLiteAccountCreationTask..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        profile = null;
        context = null;
        result = null;
        super.performCleanup();
    }
}
