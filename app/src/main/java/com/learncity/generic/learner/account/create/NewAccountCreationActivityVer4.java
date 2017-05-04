package com.learncity.generic.learner.account.create;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
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

import static com.learncity.generic.learner.account.create.NewAccountCreationActivityVer4.SignUpPageTutorContentFragment.TUTOR_CONTENT_DISCIPLINES;
import static com.learncity.generic.learner.account.create.NewAccountCreationActivityVer4.SignUpPageTutorContentFragment.TUTOR_CONTENT_PLACEHOLDER;
import static com.learncity.generic.learner.account.create.NewAccountCreationActivityVer4.SignUpPageTutorContentFragment.TUTOR_CONTENT_TUTOR_TYPES;

/**
 * Created by DJ on 1/21/2017.
 */

public class NewAccountCreationActivityVer4 extends AppCompatActivity{

    private static String TAG = "AccountCreationFragment";

    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    private static final int RC_SIGN_IN = 9001;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    GoogleApiClient mGoogleApiClient;
    private ViewPager tutorContentPager;
    private TutorSignUpPageContentPagerAdapter signUpPageContentPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_creation_ver4);

        // Checking if there has been a previous unfinsihed attempt for error resolution - in
        // which case let it complete
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

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

        // ViewPager for the Tutor related content
        tutorContentPager = (ViewPager) findViewById(R.id.tutor_content_pager);
        signUpPageContentPagerAdapter = new TutorSignUpPageContentPagerAdapter(getSupportFragmentManager());
        tutorContentPager.setAdapter(signUpPageContentPagerAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
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
                        if (mResolvingError) {
                            // Already attempting to resolve an error.
                            return;
                        } else if (connectionResult.hasResolution()) {
                            try {
                                mResolvingError = true;
                                connectionResult.startResolutionForResult(NewAccountCreationActivityVer4.this, REQUEST_RESOLVE_ERROR);
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
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((NewAccountCreationActivityVer4) getActivity()).onDialogDismissed();
        }
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
