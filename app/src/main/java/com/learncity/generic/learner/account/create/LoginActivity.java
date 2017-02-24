package com.learncity.generic.learner.account.create;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.learncity.learncity.R;
import com.learncity.util.account_management.AccountManager;
import com.learncity.util.account_management.LoginService;

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

                //Send the request
                loginService.startLoginProcess(loginTask);
                loginService.finishUp();

            }
        });

        //Load the service
        loginService = AccountManager.fetchService(this, AccountManager.LOGIN_SERVICE);
        setLoginListener();

    }

    private void setLoginListener(){
        loginService.setLoginListener(new LoginService.LoginListener() {
            @Override
            public void onLogin(LoginService.LoginEventResponse loginEventResponse) {
                Log.d(TAG, "Login successful");
            }

            @Override
            public void onLoginFailed(LoginService.LoginEventResponse loginEventResponse) {
                Log.d(TAG, "Login failed!");
                //Get the error code from Exception response
                int statusCode = loginEventResponse.getException().getStatusCode();

                if(statusCode == 400){
                    Log.d(TAG, "BadRequestException");
                    //Indicate to the user about this and take appropriate action
                }
                else if(statusCode == 401){
                    Log.d(TAG, "UnauthorizedException");
                    //Take Apt. action
                }
                else if(statusCode == 403){
                    Log.d(TAG, "ForbiddenException");
                    //Take Apt. action
                }
                else if(statusCode == 404){
                    Log.d(TAG, "NotFoundException");
                    //Take Apt. action
                }
                else if(statusCode == 409){
                    Log.d(TAG, "ConflictException");
                    //Take Apt. action
                }
                else if(statusCode == 500){
                    Log.d(TAG, "InternalServerErrorException");
                    //Take Apt. action
                }
                else if(statusCode == 503){
                    Log.d(TAG, "ServiceUnavailableException");
                    //Take Apt. action
                }

            }

            @Override
            public void onPreLogin() {

            }

            @Override
            public void onLoginServiceRefresh() {

            }
        }, LoginService.NOTIFY_UI_AUTO);
    }

    @Override
    public void onStop(){
        //Stop the AC creation service
        loginService.shutDown();
        super.onStop();
    }
}
