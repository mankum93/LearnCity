package com.learncity.learn.account.create;


import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelable;
import com.learncity.persistence.profileApi.ProfileApi;
import com.learncity.persistence.profileApi.model.Profile;

import java.io.IOException;

/**
 * Created by DJ on 10/30/2016.
 */

public class NewLearnerAccountCreateAsyncTask extends AsyncTask<GenericLearnerProfileParcelable, Void, Void> {
    private static final String TAG = "NewAccountAsyncTask";

    private static ProfileApi myApiService = null;


    @Override
    protected Void doInBackground(GenericLearnerProfileParcelable... params) {
        if(myApiService == null) {  // Only do this once
            ProfileApi.Builder builder = new ProfileApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
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

        //Now, get the profile info./object that needs to be pushed to the datastore
        GenericLearnerProfileParcelable profile = params[0];

        //Populate the entity object with the profile info.
        Profile profileEntity = new Profile();
        populateProfileEntity(profile, profileEntity);

        //Now push the info. to the database
        try{
            myApiService.insert(profileEntity).execute();
        }
        catch(IOException e){
            Log.e(TAG, "IO Exception while performing the datastore transaction");
        }
        return null;

    }

    @Override
    protected void onPostExecute(Void result) {
        //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }

    private void populateProfileEntity(GenericLearnerProfileParcelable profile, Profile profileEntity){
        profileEntity.setName(profile.getName());
        profileEntity.setEmailID(profile.getEmailID());
        profileEntity.setPhoneNo(profile.getPhoneNo());
        profileEntity.setPassword(profile.getPassword());
        profileEntity.setCurrentStatus(profile.getCurrentStatus());
    }
}
