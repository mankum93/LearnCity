package com.learncity.generic.learner.account.create;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.learncity.loginApi.LoginApi;
import com.learncity.loginApi.model.GenericLearnerProfileVer1;
import com.learncity.loginApi.model.LoginDetails;
import com.learncity.util.account_management.AbstractTask;
import com.learncity.util.account_management.LoginService;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import static com.learncity.util.account_management.LoginService.LOGIN_FAILED;
import static com.learncity.util.account_management.LoginService.LOGIN_SUCCESSFUL;

/**
 * Created by DJ on 2/19/2017.
 */

public class GAELoginTask extends AbstractTask {

    private static final String TAG = "LoginService";

    private static LoginApi loginApiService;

    private GenericLearnerProfileVer1 profileResponse;

    private LoginService.LoginDetails details;

    private LoginService.LoginEventResponse loginEventResponse;

    private int returnCode;

    public GAELoginTask(LoginService.LoginDetails details) {
        this.details = details;
    }

    @Override
    public void initializeTask(){
        //Setup of service
        if(loginApiService == null){
            setApiService();
        }
    }
    /**
     * Override this method to carry out the task.
     *
     * @return Returns the task completion status out of the following codes:
     * LoginService.{LOGIN_FAILED, LOGIN_SUCCESSFUL}
     */
    @Override
    public int performTask() {
        Log.d(TAG, "GAELoginTask.performTask(): " + "\n" + "MESSAGE: Sending request for Login..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        try{
            //Send the login request to the server
            profileResponse = loginApiService.login(populateLoginDetailsEntity(details)).execute();
        }
        catch(GoogleJsonResponseException e){
            Log.e(TAG, "There was was some problem logging in", e);
            //Prepare a proper response
            loginEventResponse = new LoginService.LoginEventResponse(e);
            returnCode = LOGIN_FAILED;

            //Post the result for interested parties
            EventBus.getDefault().postSticky(loginEventResponse);

            return returnCode;
        }
        catch(IOException ioe){
            Log.e(TAG, "There was was some problem logging in", ioe);
            returnCode = LOGIN_FAILED;

            return returnCode;
        }
        finally {
            Log.d(TAG, "Profile Response: " + profileResponse);
        }


        //Prepare a proper response
        loginEventResponse = new LoginService.LoginEventResponse(profileResponse);
        //Post the result for interested parties
        EventBus.getDefault().postSticky(loginEventResponse);

        if(profileResponse == null){
            throw new RuntimeException("Profile response shouldn't be null. Check the logic for HTTP response in GAELoginTask");
            //return LOGIN_FAILED;
        }
        return LOGIN_SUCCESSFUL;
    }

    @Override
    public void performCleanup(){
        loginApiService = null;
        loginEventResponse = null;
        profileResponse = null;
        details = null;
    }

    private LoginDetails populateLoginDetailsEntity(LoginService.LoginDetails details){
        LoginDetails loginDetailsEntity = new LoginDetails();
        loginDetailsEntity.setEmailID(details.getEmailID());
        loginDetailsEntity.setPassword(details.getPassword());

        return loginDetailsEntity;
    }

    private void setApiService(){
        loginApiService = new LoginApi.Builder(AndroidHttp.newCompatibleTransport(),
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
