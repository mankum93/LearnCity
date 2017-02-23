package com.learncity.generic.learner.account.create;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.learncity.learncity.R;
import com.learncity.util.account_management.LoginService;

/**
 * Created by DJ on 2/17/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText emailID;
    private EditText password;
    private Button loginButton;

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
                LoginService.LoginDetails loginDetails = new LoginService.LoginDetails(emailID.getText().toString(), password.getText().toString());

            }
        });
    }
}
