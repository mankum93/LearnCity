package com.learncity.generic.learner.account.create.ver0;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.learncity.generic.learner.account.profile.database.ver0.ProfileDbHelper;
import com.learncity.generic.learner.account.profile.model.ver0.GenericLearnerProfileParcelable;

/**
 * Created by DJ on 2/2/2017.
 */
public class NewAccountCreateOnLocalDbAsyncTask extends AsyncTask<GenericLearnerProfileParcelable, Void, Void> {

    private static String TAG = "NewAccountOnLocalDb";
    private Context context;
    private AccountCreationOnLocalDbListener mAccountCreationOnLocalDbListener;
    private boolean isAccountCreationComplete = false;

    public boolean isAccountCreationComplete() {
        return isAccountCreationComplete;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public NewAccountCreateOnLocalDbAsyncTask(Context context){
        this.context = context;
    }

    @Override
    public Void doInBackground(GenericLearnerProfileParcelable... params){
        if(context == null){
            throw new RuntimeException("Context to be passed during Db creation is null");
        }
        //If the A/C has NOT been created on the server, don't mind creating it on the local Db either
        if(SignUpWithEmailFragment.shouldAccountCreationBeRetried()){
            Log.d(TAG, "NewAccountCreateOnLocalDbAsyncTask - A/C creation on the local Db has been stopped due to"
            + "it being unable of being created on the server");
            return null;
        }
        //Get the data to be inserted
        GenericLearnerProfileParcelable profile = params[0];
        //Get the writable database
        SQLiteDatabase db = new ProfileDbHelper(context, profile.getCurrentStatus()).getWritableDatabase();
        //Insert into the database
        ProfileDbHelper.addProfileToDatabase(db, profile);
        //Close the connection
        db.close();
        //Account creation complete
        isAccountCreationComplete = true;
        return null;
    }
    @Override
    public void onPostExecute(Void v){
        if(isAccountCreationComplete){
            if(mAccountCreationOnLocalDbListener == null){
                throw new RuntimeException("Account Creation accountCreationTaskListener has not been set for account creation on the local Db even though the account creation has been successful");
            }
            mAccountCreationOnLocalDbListener.onAccountCreated();
        }
        else{
            //If the account has been created just on the server and not in the Db then that has already
            //been taken care of - Account shall have to be recreated both on the server as well as the local Db
        }
    }

    void setAccountCreationonLocalDbListener(AccountCreationOnLocalDbListener accountCreationOnLocalDbListener){
        mAccountCreationOnLocalDbListener = accountCreationOnLocalDbListener;
    }

    interface AccountCreationOnLocalDbListener {
        void onAccountCreated();
    }
}
