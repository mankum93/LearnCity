package com.learncity.generic.learner.account.create;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.learncity.learncity.R;
import com.learncity.util.account_management.impl.AccountManager;
import com.learncity.util.account_management.impl.LoginService;
import com.learncity.util.account_management.Result;
import com.learncity.util.account_management.Task;
import com.learncity.util.account_management.impl.GAELoginTask;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by DJ on 2/17/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";

    private EditText emailID;
    private EditText password;
    private Button loginButton;
    private LoginService.LoginDetails loginDetails;

    private LoginService loginService;
    private GAELoginTask loginTask;

    private AlertDialog loginRetryAlertDialog;
    private ProgressDialog loginProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //Get the fields
        emailID = (EditText) findViewById(R.id.emailID);
        password = (EditText) findViewById(R.id.password);

        loginButton = (Button) findViewById(R.id.login_button);
        //Set the listener on the Login Button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Perform the login task here

                //Extract the data
                loginDetails = new LoginService.LoginDetails(emailID.getText().toString(), password.getText().toString());

                //Initialize/Update the task
                loginTask = new GAELoginTask(loginDetails);
                //Set the task listener
                setLoginTaskListener();

                //Send the request
                loginService.startLoginProcess(loginTask);
                loginService.finishUp();

            }
        });

        //Load the service
        loginService = AccountManager.fetchService(this, AccountManager.LOGIN_SERVICE);
        loginService.setUIFlag(LoginService.NOTIFY_UI_CUSTOM);

    }

    private void setLoginTaskListener(){
        loginTask.setTaskListener(new Task.TaskListener<LoginService.LoginEventResponse>() {
            @Override
            public void onPreTaskExecute() {
                //Start the initial progress dialog
                showProgressDialog();
            }

            @Override
            public void onTaskCompleted(Result<LoginService.LoginEventResponse> resultCompletion) {
                Log.d(TAG, "Login successful");
                //Get the profile
                //Dismiss the progress dialog
                dismissProgressDialog();
            }

            @Override
            public void onTaskFailed(Result<LoginService.LoginEventResponse> resultFailure) {
                Log.d(TAG, "Login failed!");
                dismissProgressDialog();

                IOException failureException = resultFailure.getResponseData()[0].getException();

                String msg = "There was a problem connecting to the Network. Please " +
                        "check your network";

                if(failureException instanceof SocketTimeoutException){
                    msg = "There was a problem connecting to the Network. Please " +
                            "check your network";
                }
                else if(failureException instanceof GoogleJsonResponseException){
                    //Get the error code from Exception response
                    int statusCode = ((GoogleJsonResponseException)(failureException)).getStatusCode();

                    if(statusCode == 400){
                        Log.d(TAG, "BadRequestException");
                        //Indicate to the user about this and take appropriate action
                        msg = "There was some problem with the request";
                    }
                    else if(statusCode == 401){
                        Log.d(TAG, "UnauthorizedException");
                        msg = "Incorrect Login details - Check the EmailID and/or Password";
                    }
                    else if(statusCode == 403){
                        Log.d(TAG, "ForbiddenException");
                        msg = "Access forbidden";
                    }
                    else if(statusCode == 404){
                        Log.d(TAG, "NotFoundException");
                        msg = "Account doesn't exist with the given Email ID";
                    }
                    else if(statusCode == 409){
                        Log.d(TAG, "ConflictException");
                        //TODO: Take apt. action
                    }
                    else if(statusCode == 500){
                        Log.d(TAG, "InternalServerErrorException");
                        msg = "Service unavailable. Try again";
                    }
                    else if(statusCode == 503){
                        Log.d(TAG, "ServiceUnavailableException");
                        msg = "Service unavailable. Try again";
                    }
                }
                showRetryDialog(msg);
            }
        });
    }

    private void showRetryDialog(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //If dialog already showing, no use invoking it again
                if(loginRetryAlertDialog == null){
                    loginRetryAlertDialog = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Login Failed")
                            .setMessage(msg)
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "TaskProcessor.showRetryDialog(): " + "\n" + "MESSAGE: RETRY button clicked..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());

                                    dialog.cancel();
                                    showProgressDialog();

                                    loginService.retryOnFailedLogin();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "TaskProcessor.showRetryDialog(): " + "\n" + "MESSAGE: CANCEL button clicked..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                    dialog.cancel();

                                    loginService.cancelOnFailedLogin();
                                }
                            })
                            .setCancelable(false)
                            .create();
                }
                if(!loginRetryAlertDialog.isShowing()){
                    Log.d(TAG, "TaskProcessor.showRetryDialog(): " + "\n" + "MESSAGE: Showing the Retry dialog..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    loginRetryAlertDialog.show();
                }
            }
        });

    }

    private void showProgressDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //If dialog already in progress, no use invoking it again
                if(loginProgressDialog == null){
                    loginProgressDialog = new ProgressDialog(LoginActivity.this);
                    loginProgressDialog.setIndeterminate(true);
                    loginProgressDialog.setTitle("Logging in...");
                    loginProgressDialog.setCancelable(true);
                    loginProgressDialog.setCanceledOnTouchOutside(false);
                }
                if(!loginProgressDialog.isShowing()){
                    Log.d(TAG, "TaskProcessor.showRetryDialog(): " + "\n" + "MESSAGE: Showing the progress dialog..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    loginProgressDialog.show();
                }
            }
        });

    }

    private void dismissProgressDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //If dialog already dismissed, no use dismissing it again
                if(loginProgressDialog.isShowing()){
                    Log.d(TAG, "AccountCreationManager.showRetryDialog(): " + "\n" + "MESSAGE: Dismissing the progress dialog..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    loginProgressDialog.dismiss();
                    loginProgressDialog.cancel();
                }
            }
        });
    }

    @Override
    public void onStop(){
        //Stop the AC creation service
        loginService.shutDown();
        super.onStop();
    }
}
