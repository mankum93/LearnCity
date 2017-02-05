package com.learncity.generic.learner.account.create;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.json.GenericJson;
import com.learncity.backend.persistence.genericLearnerProfileVer1Api.GenericLearnerProfileVer1Api;
import com.learncity.backend.persistence.genericLearnerProfileVer1Api.model.GenericLearnerProfileVer1;
import com.learncity.backend.persistence.tutorProfileVer1Api.TutorProfileVer1Api;
import com.learncity.backend.persistence.tutorProfileVer1Api.model.TutorProfileVer1;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelableVer1;
import com.learncity.tutor.account.profile.model.TutorProfileParcelableVer1;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by DJ on 10/30/2016.
 */

public class NewAccountCreateOnServerAsyncTaskVer1 extends AsyncTask<GenericLearnerProfileParcelableVer1, Void, Void> {
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

    public static void setApiService(GenericLearnerProfileParcelableVer1 profile) {
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
    private static AbstractGoogleJsonClient.Builder selectBuilder(GenericLearnerProfileParcelableVer1 profile){
        if(profile instanceof TutorProfileParcelableVer1){
            return new TutorProfileVer1Api.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null);
        }
        return new GenericLearnerProfileVer1Api.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null);
    }

    @Override
    protected Void doInBackground(GenericLearnerProfileParcelableVer1... params) {

        //Now, get the profile info./object that needs to be pushed to the datastore
        GenericLearnerProfileParcelableVer1 profile = params[0];

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
            if(myApiService instanceof GenericLearnerProfileVer1Api){
                ((GenericLearnerProfileVer1Api)myApiService).insert((GenericLearnerProfileVer1) profileEntity).execute();
                //The execute method returns an instance of inserted object so I am assuming that implies successful
                //transaction to the database
                isAccountCreationComplete = true;
            }
            else if(myApiService instanceof TutorProfileVer1Api){
                ((TutorProfileVer1Api)myApiService).insert((TutorProfileVer1) profileEntity).execute();
                //The execute method returns an instance of inserted object so I am assuming that implies successful
                //transaction to the database
                isAccountCreationComplete = true;
            }

        }
        catch(IOException e){
            Log.e(TAG, "Account couldn't be created : IO Exception while performing the data-store transaction");
            e.printStackTrace();
            //The Account couldn't be created.
            isAccountCreationComplete = false;
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
                throw new RuntimeException("Account Creation listener has not been set for account creation on the server even though the account creation has been successful");
            }
            mAccountCreationOnServerListener.onAccountCreated();
        }
    }

    private GenericJson populateProfileEntity(GenericLearnerProfileParcelableVer1 profile){
        //Populate the entity object with the profile info.

        //GenericJson is the base type for the entities generated by the client lib-generator. This means that
        //I can't have inheritance between the 2 different entities event though it makes sense to have it. In other words
        //the entity classes generated by the lib-generator will be always be separate GenericJson types which is why there
        //is redundant code here
        GenericJson profileEntity = null;

        if(profile instanceof TutorProfileParcelableVer1){
            TutorProfileParcelableVer1 tutorProfile = (TutorProfileParcelableVer1)profile;

            profileEntity = new TutorProfileVer1();
            TutorProfileVer1 profileEntityTutor = (TutorProfileVer1) profileEntity;

            profileEntityTutor.setName(tutorProfile.getName());
            profileEntityTutor.setEmailID(tutorProfile.getEmailID());
            profileEntityTutor.setPhoneNo(tutorProfile.getPhoneNo());
            profileEntityTutor.setPassword(tutorProfile.getPassword());
            profileEntityTutor.setCurrentStatus(tutorProfile.getCurrentStatus());
            profileEntityTutor.setTutorTypes(Arrays.asList(tutorProfile.getTutorTypes()));
            profileEntityTutor.setDisciplines(Arrays.asList(tutorProfile.getDisciplines()));
        }
        else{
            profileEntity = new GenericLearnerProfileVer1();
            GenericLearnerProfileVer1 profileLearnerEntity = (GenericLearnerProfileVer1)profileEntity;

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
