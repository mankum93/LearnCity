package com.learncity.generic.learner.account.create;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
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

    private GenericLearnerProfileVer1 response;

    private LoginService.LoginDetails details;

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
        try{
            //Send the login request to the server
            response = loginApiService.login(populateLoginDetailsEntity(details)).execute();
        }
        catch(IOException ioe){
            Log.e(TAG, "There was was some problem logging in", ioe);
        }
        //Post the result for interested parties
        EventBus.getDefault().postSticky(new LoginService.ProfileResponseOnLoginEvent(response));

        if(response == null){
            return LOGIN_FAILED;
        }
        return LOGIN_SUCCESSFUL;
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
