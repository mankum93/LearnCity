package com.learncity.generic.learner.account.create.ver0;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.json.GenericJson;
import com.learncity.backend.persistence.genericLearnerProfileApi.GenericLearnerProfileApi;
import com.learncity.backend.persistence.genericLearnerProfileApi.model.GenericLearnerProfile;
import com.learncity.backend.persistence.tutorProfileApi.TutorProfileApi;
import com.learncity.backend.persistence.tutorProfileApi.model.TutorProfile;
import com.learncity.generic.learner.account.profile.model.ver0.GenericLearnerProfileParcelable;
import com.learncity.tutor.account.profile.model.qualification.educational.ver0.TutorProfileParcelable;

import java.io.IOException;

/**
 * Created by DJ on 10/30/2016.
 */

public class NewAccountCreateOnServerAsyncTask extends AsyncTask<GenericLearnerProfileParcelable, Void, Void> {
    private static final String TAG = "NewAccountAsyncTask";

    private static AbstractGoogleJsonClient myApiService = null;

    private boolean isAccountCreationComplete = false;
    private boolean shouldAccountCreationBeRetried = false;

    public boolean shouldAccountCreationBeRetried() {
        return shouldAccountCreationBeRetried;
    }

    public boolean isAccountCreationComplete() {
        return isAccountCreationComplete;
    }

    private AccountCreationOnServerListener mAccountCreationOnServerListener;

    public static void setApiService(GenericLearnerProfileParcelable profile) {
        AbstractGoogleJsonClient.Builder builder = selectBuilder(profile)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setApplicationName("Learn City")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        // end options for devappserver


        myApiService = builder.build();
    }
    private static AbstractGoogleJsonClient.Builder selectBuilder(GenericLearnerProfileParcelable profile){
        if(profile instanceof TutorProfileParcelable){
            return new TutorProfileApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null);
        }
        return new GenericLearnerProfileApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null);
    }

    @Override
    protected Void doInBackground(GenericLearnerProfileParcelable... params) {

        //Now, get the profile info./object that needs to be pushed to the datastore
        GenericLearnerProfileParcelable profile = params[0];

        if(myApiService == null) {  // Only do this once
            setApiService(profile);
        }

        //Populate the entity object with the profile info.
        GenericJson profileEntity = populateProfileEntity(profile);

        if(isCancelled()){
            shouldAccountCreationBeRetried = true;
            mAccountCreationOnServerListener.onAccountCreationRetry();
            return null;
        }
        //Now push the info. to the database through the right service
        try{
            if(myApiService instanceof GenericLearnerProfileApi){
                ((GenericLearnerProfileApi)myApiService).insert((GenericLearnerProfile) profileEntity).execute();
                //The execute method returns an instance of inserted object so I am assuming that implies successful
                //transaction to the database
                isAccountCreationComplete = true;
            }
            else if(myApiService instanceof TutorProfileApi){
                ((TutorProfileApi)myApiService).insert((TutorProfile) profileEntity).execute();
                //The execute method returns an instance of inserted object so I am assuming that implies successful
                //transaction to the database
                isAccountCreationComplete = true;
            }

        }
        catch(IOException e){
            Log.e(TAG, "Account couldn't be created : IO Exception while performing the data-store transaction");
            e.printStackTrace();
            //The Account couldn't be created.

            //TODO: Show a Retry Dialog to retry  the account creation process
        }
        return null;

    }

    @Override
    protected void onPostExecute(Void result) {
        if(!isAccountCreationComplete){
            //1. Must have gotten an exception while performing transaction in the database.
            // OR,
            //2. This AsyncTask got cancelled and doInBackground() returned null
            // RETRY PLEASE!!!
            shouldAccountCreationBeRetried = true;
            mAccountCreationOnServerListener.onAccountCreationRetry();
        }
        else{
            //Account creation complete. Call the account creation callback
            if(mAccountCreationOnServerListener == null){
                throw new RuntimeException("Account Creation accountCreationTaskListener has not been set for account creation on the server even though the account creation has been successful");
            }
            mAccountCreationOnServerListener.onAccountCreated();
        }
    }

    private GenericJson populateProfileEntity(GenericLearnerProfileParcelable profile){
        //Populate the entity object with the profile info.

        //GenericJson is the base type for the entities generated by the client lib-generator. This means that
        //I can't have inheritance between the 2 different entities event though it makes sense to have it. In other words
        //the entity classes generated by the lib-generator will be always be separate GenericJson types which is why there
        //is redundant code here
        GenericJson profileEntity = null;

        if(profile instanceof TutorProfileParcelable){
            TutorProfileParcelable tutorProfile = (TutorProfileParcelable)profile;

            profileEntity = new TutorProfile();
            TutorProfile profileEntityTutor = (TutorProfile) profileEntity;

            profileEntityTutor.setName(tutorProfile.getName());
            profileEntityTutor.setEmailID(tutorProfile.getEmailID());
            profileEntityTutor.setPhoneNo(tutorProfile.getPhoneNo());
            profileEntityTutor.setPassword(tutorProfile.getPassword());
            profileEntityTutor.setCurrentStatus(tutorProfile.getCurrentStatus());
        }
        else{
            profileEntity = new GenericLearnerProfile();
            GenericLearnerProfile profileLearnerEntity = (GenericLearnerProfile)profileEntity;

            profileLearnerEntity.setName(profile.getName());
            profileLearnerEntity.setEmailID(profile.getEmailID());
            profileLearnerEntity.setPhoneNo(profile.getPhoneNo());
            profileLearnerEntity.setPassword(profile.getPassword());
            profileLearnerEntity.setCurrentStatus(profile.getCurrentStatus());
        }

        return profileEntity;
    }

    void setAccountCreationListener(AccountCreationOnServerListener accountCreationOnServerListener){
        mAccountCreationOnServerListener = accountCreationOnServerListener;
    }

    interface AccountCreationOnServerListener {
        void onAccountCreated();
        void onAccountCreationRetry();
    }

}
