package com.learncity.generic.learner.account.create.ver1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.learncity.generic.learner.account.profile.database.ProfileDbHelperVer1;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;

/**
 * Created by DJ on 2/2/2017.
 */
public class NewAccountCreateOnLocalDbAsyncTaskVer1 extends AsyncTask<GenericLearnerProfile, Void, Void> {

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

    public NewAccountCreateOnLocalDbAsyncTaskVer1(Context context){
        this.context = context;
    }

    @Override
    public Void doInBackground(GenericLearnerProfile... params){
        if(context == null){
            throw new RuntimeException("Context to be passed during Db creation is null");
        }
        //If the A/C has NOT been created on the server, don't mind creating it on the local Db either
        if(SignUpWithEmailFragmentVer1.shouldAccountCreationBeRetried()){
            Log.d(TAG, "NewAccountCreateOnLocalDbAsyncTask - A/C creation on the local Db has been stopped because"
            + "it couldn't be created on the server");
            return null;
        }
        //Get the data to be inserted
        GenericLearnerProfile profile = params[0];
        //Get the writable database
        ProfileDbHelperVer1 helper = new ProfileDbHelperVer1(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.createProfileTables(db, profile.getCurrentStatus());
        //Insert into the database
        ProfileDbHelperVer1.addProfileToDatabase(db, profile);
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
