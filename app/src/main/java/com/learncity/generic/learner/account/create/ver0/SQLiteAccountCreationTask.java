package com.learncity.generic.learner.account.create.ver0;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.util.Log;

import com.learncity.generic.learner.account.profile.database.ProfileDbHelperVer1;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.util.ver0.AccountCreationTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * Created by DJ on 2/6/2017.
 */

public class SQLiteAccountCreationTask implements AccountCreationTask {

    private static final String TAG = "SQLiteACCreationTask";

    private SQLiteAccountCreationHandlerThread handlerThread;

    private GenericLearnerProfile profile;

    private Context context;

    private AccountCreationTaskListener listener;

    private Handler leftOverHandler;

    public SQLiteAccountCreationTask(Context context, GenericLearnerProfile profile){
        this.context = context;
        this.profile = profile;
    }

    private void initializeTask() {
        //Register with the EventBus to receive the left over handler if any
        //EventBus.getDefault().register(this);

        //Get the handler thread instance if there is any
        leftOverHandler = EventBus.getDefault().getStickyEvent(Handler.class);

        if(leftOverHandler == null){
            handlerThread = new SQLiteAccountCreationHandlerThread("SQLiteAccountCreationTask");
        }
        Log.d(TAG, "SQLiteAccountCreationTask.initializeTask(): " + "\n" + "MESSAGE: SQLiteAccountCreationTask initialized" +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
    }

    @Override
    public void performAccountCreation() {
        Log.d(TAG, "SQLiteAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Performing AC creation..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        initializeTask();
        if(leftOverHandler != null){
            leftOverHandler.post(new Runnable() {
                @Override
                public void run() {
                    executeTask();
                }
            });
        }
        else{
            if(!handlerThread.isAlive()){
                handlerThread.start();
            }
            handlerThread.prepareHandler();

            handlerThread.execute(new Runnable() {
                @Override
                public void run() {
                    executeTask();
                }
            });
        }
    }

    private void executeTask(){
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
            listener.onAccountCreationFailed();
            return;
        }
        finally{
            //Close the connection
            db.close();
        }

        Log.d(TAG, "SQLiteAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Profile written to SQLite Db" +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        //Account successfully created
        listener.onAccountCreated();
    }

    @Override
    public void cancelAccountCreation() {

    }

    @Override
    public void performCleanup() {
        Log.d(TAG, "SQLiteAccountCreationTask.performCleanup(): " + "\n" + "MESSAGE: Cleaning up SQLiteAccountCreationTask..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        //Remove the sticky object
        if(leftOverHandler != null){
            EventBus.getDefault().removeStickyEvent(leftOverHandler);
            leftOverHandler.getLooper().quit();
        }

        profile = null;
        listener = null;
        context = null;
        leftOverHandler = null;
    }

    public void setAccountCreationListener(AccountCreationTaskListener listener) {
        this.listener = listener;
    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    private void receiveLeftOverHandler(Handler handler){
        leftOverHandler = handler;
    }



    private static boolean isExistingDatabase(Context context, String databaseName) {

        File dbPath = context.getDatabasePath(databaseName);
        if(dbPath != null){
            return true;
        }
        return false;
    }

    public static boolean isExistingUserAccount(Context context){
        return isExistingDatabase(context, ProfileDbHelperVer1.DATABASE_NAME);
    }
}
