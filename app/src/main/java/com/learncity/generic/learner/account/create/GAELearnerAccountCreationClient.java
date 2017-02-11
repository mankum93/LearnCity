package com.learncity.generic.learner.account.create;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.json.GenericJson;
import com.learncity.backend.persistence.genericLearnerProfileVer1Api.GenericLearnerProfileVer1Api;
import com.learncity.backend.persistence.genericLearnerProfileVer1Api.model.GenericLearnerProfileVer1;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelableVer1;

import java.io.IOException;

/**
 * Created by DJ on 2/5/2017.
 */

public class GAELearnerAccountCreationClient implements AccountCreationClient {

    private static final String TAG = "GAEACCreationClient";

    private static GenericLearnerProfileVer1Api myApiService;

    private GenericLearnerProfileParcelableVer1 profile;

    private GenericLearnerProfileVer1 profileEntity;

    private AccountCreationClientListener clientListener;

    public GAELearnerAccountCreationClient(GenericLearnerProfileParcelableVer1 profile){
        this.profile = profile;
    }
    @Override
    public void prepareClient() {
        Log.d(TAG, "GAELearnerAccountCreationClient.prepareClient(): " + "\n" + "Client: Preparing client GenericLearnerProfileVer1Api..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        if(myApiService == null){
            setApiService();
        }
        populateProfileEntity(profile);
        clientListener.onClientPrepared();
    }

    @Override
    public void sendRequest() {

        Log.d(TAG, "EMAIL: " + profileEntity.getEmailID() + "");
        try{
            Log.d(TAG, "GAELearnerAccountCreationClient.sendRequest(): " + "\n" + "MESSAGE: Sending Request for persistence..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            myApiService.insert(profileEntity).execute();
        }
        catch(IOException e){
            Log.e(TAG, "Account couldn't be created : IO Exception while performing the data-store transaction");
            e.printStackTrace();
            //Callback for the failed request
            clientListener.onRequestFailed();
            return;
        }
        clientListener.onRequestSuccessful();
    }

    @Override
    public void performCleanup() {
        Log.d(TAG, "GAELearnerAccountCreationClient.performCleanup(): " + "\n" + "MESSAGE: Cleaning up GAELearnerAccountCreationClient..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        profile = null;
        profileEntity = null;
        clientListener = null;
        myApiService = null;
    }

    public void setClientListener(AccountCreationClientListener listener){
        clientListener = listener;
    }

    private void setApiService(){
        myApiService = new GenericLearnerProfileVer1Api.Builder(AndroidHttp.newCompatibleTransport(),
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
                }).build();
    }

    private void populateProfileEntity(GenericLearnerProfileParcelableVer1 profile){
        //Populate the entity object with the profile info.

        profileEntity = new GenericLearnerProfileVer1();

        profileEntity.setName(profile.getName());
        profileEntity.setEmailID(profile.getEmailID());
        profileEntity.setPhoneNo(profile.getPhoneNo());
        profileEntity.setPassword(profile.getPassword());
        profileEntity.setCurrentStatus(profile.getCurrentStatus());
    }
}
