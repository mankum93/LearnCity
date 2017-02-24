package com.learncity.generic.learner.account.create;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learncity.R;
import com.learncity.learner.account.profile.model.LearnerProfile;

/**
 * Created by DJ on 1/21/2017.
 */

public class NewAccountCreationActivityVer4 extends AppCompatActivity{

    private static final int RC_SIGN_IN = 9001;
    private static String TAG = "AccountCreationFragment";

    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_creation_ver4);

        //Set up the listeners on the buttons
        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Launch the Login Activity
                Intent loginIntent = new Intent(NewAccountCreationActivityVer4.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
        Button signUpWithGoogleButton = (Button) findViewById(R.id.sign_up_with_Google);
        signUpWithGoogleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                initializeGoogleApiClient();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        Button signUpWithEmailButton = (Button) findViewById(R.id.sign_up_with_email);
        signUpWithEmailButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //This will lead to the full sign up page
                Intent createACWithEmail = new Intent(NewAccountCreationActivityVer4.this, SignUpActivity.class);
                createACWithEmail.putExtra(SignUpActivity.SIGN_UP_METHOD, SignUpWithEmailFragmentVer4.SIGN_UP_WITH_EMAIL);
                startActivity(createACWithEmail);
            }
        });
    }

    private void initializeGoogleApiClient() {
        //If client is already initialized then no need for it again
        if(mGoogleApiClient != null){
            Log.d(TAG, "GoogleApiClient already initialized");
            return;
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG, "Connection failed" + connectionResult);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //Below is all the info. from Google A/C successful sign in
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            //Profile creation is incomplete w.r.t the compulsory fields right now
            GenericLearnerProfile profile = new LearnerProfile
                    .Builder(GenericLearnerProfile.validateName(personName),
                    GenericLearnerProfile.validateEmailId(personEmail),
                    GenericLearnerProfile.PHONE_NO_NULL,
                    GenericLearnerProfile.STATUS_UNDEFINED,
                    GenericLearnerProfile.PASSWORD_NULL)
                    .build();

            Intent createACWithGoogle = new Intent(this, SignUpActivity.class);
            createACWithGoogle.putExtra(SignUpActivity.SIGN_UP_METHOD, SignUpWithGoogleAccountFragmentVer4.SIGN_UP_WITH_GOOGLE);
            createACWithGoogle.putExtra(SignUpWithGoogleAccountFragmentVer4.EXTRAS_GENERIC_PROFILE_INCOMPLETE, profile);

            startActivity(createACWithGoogle);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
            //The person doesn't have a Google Account even though he clicked on for Sign-Up through Google
            //In this case, we will do nothing but he or she shall have to click on the
            //"Sign Up with Email"
        }
    }
}
