package com.learncity.generic.learner.account.create;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
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
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learncity.R;
import com.learncity.learner.account.profile.model.LearnerProfile;
import com.learncity.util.GoogleApiHelper;

import static com.learncity.generic.learner.account.create.NewAccountCreationActivity.SignUpPageTutorContentFragment.TUTOR_CONTENT_DISCIPLINES;
import static com.learncity.generic.learner.account.create.NewAccountCreationActivity.SignUpPageTutorContentFragment.TUTOR_CONTENT_PLACEHOLDER;
import static com.learncity.generic.learner.account.create.NewAccountCreationActivity.SignUpPageTutorContentFragment.TUTOR_CONTENT_TUTOR_TYPES;
import static com.learncity.util.GoogleApiHelper.STATE_RESOLVING_ERROR;

/**
 * Created by DJ on 1/21/2017.
 */

public class NewAccountCreationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static String TAG = "AccountCreationFragment";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final int RC_SIGN_IN = 9001;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    private GoogleApiClient mGoogleApiClient;

    private ViewPager tutorContentPager;
    private TutorSignUpPageContentPagerAdapter signUpPageContentPagerAdapter;
    private GoogleApiAvailability googleAPI;
    private boolean signUpWithGoogleButtonClicked;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_creation);

        // Checking if there has been a previous unfinsihed attempt for error resolution - in
        // which case let it complete
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        // Preemptive initialization of GoogleApiClient
        initializeGoogleApiClient();

        // Wire up the buttons to their listeners
        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Launch the Login Activity
                Intent loginIntent = new Intent(NewAccountCreationActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
        Button signUpWithGoogleButton = (Button) findViewById(R.id.sign_up_with_Google);
        signUpWithGoogleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                signUpWithGoogleButtonClicked = true;

                if(!mGoogleApiClient.isConnected()){
                    mGoogleApiClient.connect();
                }
            }
        });
        Button signUpWithEmailButton = (Button) findViewById(R.id.sign_up_with_email);
        signUpWithEmailButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //This will lead to the full sign up page
                Intent createACWithEmail = new Intent(NewAccountCreationActivity.this, SignUpActivity.class);
                createACWithEmail.putExtra(SignUpActivity.SIGN_UP_METHOD, SignUpWithEmailFragment.SIGN_UP_WITH_EMAIL);
                startActivity(createACWithEmail);
            }
        });

        // ViewPager for the Tutor related content
        tutorContentPager = (ViewPager) findViewById(R.id.tutor_content_pager);
        signUpPageContentPagerAdapter = new TutorSignUpPageContentPagerAdapter(getSupportFragmentManager());
        tutorContentPager.setAdapter(signUpPageContentPagerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {

        mGoogleApiClient.disconnect();

        super.onStop();
    }

    private boolean areGooglePlayServicesAvailable() {
        googleAPI = GoogleApiAvailability.getInstance();
        int errorCode = googleAPI.isGooglePlayServicesAvailable(getApplicationContext());
        if (errorCode != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(errorCode)) {
                googleAPI.getErrorDialog(this, errorCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
                //errorUnresolvable = false;
            }
            else{
                // See ya user :(
                //errorUnresolvable = true;
            }

            return false;
        }
        //errorUnresolvable = false;
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    private void initializeGoogleApiClient() {
        //If client is already initialized then no need for it again

        if (mGoogleApiClient == null) {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .requestId()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected to the Google Play Services");

        // Google Play services must be installed and
        // GoogleApiClient must be connected to proceed

        if(signUpWithGoogleButtonClicked){
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
        signUpWithGoogleButtonClicked = false;
    }

    @Override
    public void onConnectionSuspended(int i) {

        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "Connection failed" + connectionResult);
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(NewAccountCreationActivity.this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        GoogleApiHelper.ErrorDialogFragmentWithCallback dialogFragment = new GoogleApiHelper.ErrorDialogFragmentWithCallback();

        dialogFragment.setOnDialogDismissedListener(new GoogleApiHelper.OnDialogDismissedListener() {
            @Override
            public void onDialogDismissed() {
                mResolvingError = false;
            }
        });

        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }

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
            createACWithGoogle.putExtra(SignUpActivity.SIGN_UP_METHOD, SignUpWithGoogleAccountFragment.SIGN_UP_WITH_GOOGLE);
            createACWithGoogle.putExtra(SignUpWithGoogleAccountFragment.EXTRAS_GENERIC_PROFILE_INCOMPLETE, profile);

            startActivity(createACWithGoogle);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
            //The person doesn't have a Google Account even though he clicked on for Sign-Up through Google
            //In this case, we will do nothing but he or she shall have to click on the
            //"Sign Up with Email"
        }
    }
    //-------------------------------------------------------------------------------------------------------------------
    public static class TutorSignUpPageContentPagerAdapter extends FragmentPagerAdapter{
        public TutorSignUpPageContentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return SignUpPageTutorContentFragment.newInstance(TUTOR_CONTENT_TUTOR_TYPES);
                case 1:
                    return SignUpPageTutorContentFragment.newInstance(TUTOR_CONTENT_DISCIPLINES);
            }
            // Default case
            return SignUpPageTutorContentFragment.newInstance(TUTOR_CONTENT_PLACEHOLDER);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
    //--------------------------------------------------------------------------------------------------------------------

    public static class SignUpPageTutorContentFragment extends Fragment {

        private static final String TAG = "TutorContentFragment";

        public static final String ARG_TUTOR_CONTENT = "tutor_content";
        /**Fragments arguments for Tutor content to be displayed on the Sign Up Page*/
        public static final String TUTOR_CONTENT_TUTOR_TYPES = "tutor_types";   // Serial Order: 1
        public static final String TUTOR_CONTENT_DISCIPLINES = "disciplines";   // Serial Order: 2
        public static final String TUTOR_CONTENT_PLACEHOLDER = "placeholder_content";

        private AppCompatImageView imageView;

        public static SignUpPageTutorContentFragment newInstance(String tutorContentType){
            Bundle b = new Bundle();

            if(tutorContentType == null || tutorContentType.equals(TUTOR_CONTENT_PLACEHOLDER)){
                Log.w(TAG, "tutorContentType is null: Cannot return a fragment for the Tutor content." +
                        "\n" + "For a presentable solution, returning a Fragment based on a placeholder content." +
                        "\n" + "Ensure that you intended for a placeholder indeed.");
                // TODO: Create an image resource for the placeholder and put an argument for it here
            }
            else if(tutorContentType.equals(TUTOR_CONTENT_TUTOR_TYPES)){
                b.putInt(ARG_TUTOR_CONTENT, R.drawable.tutor_types_word_cloud);
            }
            else{
                b.putInt(ARG_TUTOR_CONTENT, R.drawable.sign_up_page_subjects);
            }

            SignUpPageTutorContentFragment fragment = new SignUpPageTutorContentFragment();
            fragment.setArguments(b);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(
                    R.layout.fragment_sign_up_page_tutor_content, container, false);

            imageView = (AppCompatImageView) rootView.findViewById(R.id.tutor_content_image_view);
            imageView.setBackgroundDrawable(
                    ContextCompat.getDrawable(getActivity(), getArguments().getInt(ARG_TUTOR_CONTENT)));

            return rootView;
        }
    }
}
