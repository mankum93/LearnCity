package com.learncity.generic.learner.account.create;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.learncity.backend.persistence.tutorProfileVer1Api.TutorProfileVer1Api;
import com.learncity.backend.persistence.tutorProfileVer1Api.model.TutorProfileVer1;
import com.learncity.tutor.account.profile.model.TutorProfile;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by DJ on 2/7/2017.
 */

public class GAETutorAccountCreationClient implements AccountCreationClient {

    private static final String TAG = "GAEACCreationClient";

    private static TutorProfileVer1Api myApiService;

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
        populateProfileEntity(profile);

        clientListener.onClientPrepared();
    }

    @Override
    public void sendRequest() {
        try{
            Log.d(TAG, "GAETutorAccountCreationClient.sendRequest(): " + "\n" + "MESSAGE: Sending Request for persistence..." +
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
        myApiService = new TutorProfileVer1Api.Builder(AndroidHttp.newCompatibleTransport(),
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

    private void populateProfileEntity(TutorProfile tutorProfile){
        //Populate the entity object with the profile info.

        profileEntity = new TutorProfileVer1();

        profileEntity.setName(tutorProfile.getName());
        profileEntity.setEmailID(tutorProfile.getEmailID());
        profileEntity.setPhoneNo(tutorProfile.getPhoneNo());
        profileEntity.setPassword(tutorProfile.getPassword());
        profileEntity.setCurrentStatus(tutorProfile.getCurrentStatus());
        profileEntity.setTutorTypes(Arrays.asList(tutorProfile.getTutorTypes()));
        profileEntity.setDisciplines(Arrays.asList(tutorProfile.getDisciplines()));
    }
}
