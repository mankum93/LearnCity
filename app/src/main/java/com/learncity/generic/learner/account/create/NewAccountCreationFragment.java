package com.learncity.generic.learner.account.create;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelable;
import com.learncity.learncity.R;

/**
 * Created by DJ on 1/21/2017.
 */

public class NewAccountCreationFragment extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    private static String TAG = "AccountCreationFragment";

    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.fragment_new_account_creation, container, false);

        //Set up the listeners on the buttons
        Button loginButton = (Button) root.findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
        Button signUpWithGoogleButton = (Button) root.findViewById(R.id.sign_up_with_Google);
        signUpWithGoogleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestProfile()
                        .requestId()
                        .build();
                mGoogleApiClient = new GoogleApiClient.Builder(NewAccountCreationFragment.this.getActivity())
                        .enableAutoManage(NewAccountCreationFragment.this.getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Log.e(TAG, "Connection failed" + connectionResult);
                            }
                        })
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        Button signUpWithEmailButton = (Button) root.findViewById(R.id.sign_up_with_email);
        signUpWithEmailButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //This will lead to the full sign up page
                Intent i = new Intent(getActivity(), SignUpWithEmailActivity.class);
                startActivity(i);
            }
        });


        return root;
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

            GenericLearnerProfileParcelable profile = new GenericLearnerProfileParcelable(personName, personEmail, null, GenericLearnerProfileParcelable.STATUS_UNDEFINED, null);
            Intent createACWithGoogle = new Intent(this.getActivity(), SignUpWithGoogleAccountActivity.class);
            createACWithGoogle.putExtra(SignUpWithGoogleAccountActivity.EXTRA_GENERIC_PROFILE_WITH_UNDEFINED_STATUS, profile);

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