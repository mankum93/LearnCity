package com.learncity.tutor.jobs;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.learncity.backend.learnerApi.LearnerApi;
import com.learncity.backend.tutorApi.TutorApi;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.util.account_management.impl.AccountManager;

import java.io.IOException;

import static com.learncity.LearnCityApplication.BACKEND_ROOT_URL;

/**
 * Created by DJ on 3/19/2017.
 */

public class TutorsFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FbInstanceIDService";

    public static final String IS_FIREBASE_TOKEN_STASH_PENDING = "IS_FIREBASE_TOKEN_STASH_PENDING";
    public static final String FIREBASE_TOKEN = "FIREBASE_TOKEN";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to GAE backend.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        GenericLearnerProfile profile = AccountManager.getAccountDetails(this);
        AccountManager.userDeviceFirebaseToken = token;
        if(profile == null){
            // The Account is does not exist locally for some reason. Ideally,
            // it should exist. It is possible that this is a new Account creation and
            // this service has been running since the Boot or App process start so
            // AC creation hasn't taken place just yet.
            // Other possibility might be that AC creation locally was unsuccessful or
            // user data has been wiped off
            // So, in this case, stash the token with the Account Manager for now(done).
            getSharedPreferences("MISC", 0)
                    .edit()
                    .putBoolean(IS_FIREBASE_TOKEN_STASH_PENDING, true)
                    .putString(FIREBASE_TOKEN, token)
                    .apply();
        }
        else{
            // TODO: Stash the token locally
            Log.d(TAG, "Sending the request for stashing the Firebase token to the server...");

            if(profile.getCurrentStatus() == GenericLearnerProfile.STATUS_LEARNER){
                Log.d(TAG, "Sending the request for stashing the Firebase token to the server...\n"
                        + "Email ID: " + profile.getEmailID() + "\n" +
                        "Token: " + token);
                LearnerApi myApiService = new LearnerApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl(BACKEND_ROOT_URL)
                        .setApplicationName("Learn City")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        }).build();
                try{
                    myApiService.updateWithFirebaseToken(token, profile.getEmailID()).execute();
                }catch(IOException e){
                    Log.e(TAG, "Account couldn't be updated with Firebase token: IO Exception while performing the data-store transaction");
                    e.printStackTrace();
                }
            }
            else if(profile.getCurrentStatus() == GenericLearnerProfile.STATUS_TUTOR){
                Log.d(TAG, "Sending the request for stashing the Firebase token to the server...\n"
                        + "Email ID: " + profile.getEmailID() + "\n" +
                        "Token: " + token);
                TutorApi myApiService = new TutorApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl(BACKEND_ROOT_URL)
                        .setApplicationName("Learn City")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        }).build();
                try{
                    myApiService.updateWithFirebaseToken(token, profile.getEmailID()).execute();
                }catch(IOException e){
                    Log.e(TAG, "Account couldn't be updated with Firebase token: IO Exception while performing the data-store transaction");
                    e.printStackTrace();
                }
            }
            else{
                Log.wtf(TAG, "User data locally found corrupt with symptom:\n" + "USER STATUS: " + profile.getCurrentStatus());
                // TODO: Refresh profile from the server as and when appropriate and/or investigate the issue
            }
        }
    }
}
