package com.learncity.util.account_management.impl;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import com.learncity.backend.tutor.tutorApi.TutorApi;
import com.learncity.backend.tutor.tutorApi.model.TutorProfileVer1;
import com.learncity.util.account_management.AccountCreationClient;
import com.learncity.tutor.account.profile.model.TutorProfile;

import java.io.IOException;


/**
 * Created by DJ on 2/7/2017.
 */

public class GAETutorAccountCreationClient implements AccountCreationClient {

    private static final String TAG = "GAEACCreationClient";

    private static TutorApi myApiService;

    private TutorProfile profile;

    private TutorProfileVer1 profileEntity;

    private AccountCreationClientListener clientListener;

    public GAETutorAccountCreationClient(TutorProfile profile){
        this.profile = profile;
    }

    @Override
    public void prepareClient() {
        Log.d(TAG, "GAETutorAccountCreationClient.prepareClient(): " + "\n" + "Client: Preparing client TutorProfileVer1Api..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        if(myApiService == null){
            setApiService();
        }

        profileEntity = TutorProfile.populateProfileEntity(profile, profileEntity);

        clientListener.onClientPrepared();
    }

    @Override
    public void sendRequest() {

        Log.d(TAG, "Profile: " + profileEntity);
        try{
            Log.d(TAG, "GAETutorAccountCreationClient.sendRequest(): " + "\n" + "MESSAGE: Sending Request for persistence..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            myApiService.insertTutorAccount(profileEntity).execute();
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
        Log.d(TAG, "GAETutorAccountCreationClient.performCleanup(): " + "\n" + "MESSAGE: Cleaning up GAETutorAccountCreationClient..." +
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
        myApiService = new TutorApi.Builder(AndroidHttp.newCompatibleTransport(),
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
}
