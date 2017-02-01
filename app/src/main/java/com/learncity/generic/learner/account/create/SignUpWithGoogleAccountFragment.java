package com.learncity.generic.learner.account.create;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelable;
import com.learncity.learncity.R;
import com.learncity.learner.main.LearnerHomeActivity;
import com.learncity.tutor.account.profile.TutorProfileParcelable;
import com.learncity.tutor.main.TutorHomeActivity;

/**
 * Created by DJ on 10/30/2016.
 */

public class SignUpWithGoogleAccountFragment extends Fragment{

    public static String GENERIC_PROFILE = "com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelable";

    public static String TAG = "SignUpWithEmailFragment";

    private NewAccountCreateOnServerAsyncTask newAccountCreateOnServerAsyncTask;
    private NewAccountCreateLocalDbAsyncTask newAccountCreateLocalDbAsyncTask;
    FrameLayout mProgressbarHolder;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    //Reserved to be used inside any callback method to post some UI updation task on the the main thread's looper
    Handler mHandler;

    private boolean isAccountCreationOnServerCompleted = false;
    private boolean isAccountCreationOnLocalDbCompleted = false;

    GenericLearnerProfileParcelable profileFromGoogleAccount;

    public static boolean shouldAccountCreationBeRetried() {
        return NewAccountCreationActivity.mShouldAccountCreationBeRetried;
    }

    public static SignUpWithGoogleAccountFragment newInstance(){
        return new SignUpWithGoogleAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        //RESET: A/C creation retry is a flag set up to prevent the creation of A/C on local Db if it hasn't
        //been created on the server. Even if the A/C creation goes successful for n number of times,
        //for the (n+1)th time, it should be reset so that A/C creation can proceed normally
        NewAccountCreationActivity.mShouldAccountCreationBeRetried = false;

        super.onCreate(savedInstanceState);

        profileFromGoogleAccount = getActivity().getIntent().getParcelableExtra(SignUpWithGoogleAccountActivity.EXTRA_GENERIC_PROFILE_WITH_UNDEFINED_STATUS);

        //Reserved to be used inside any callback method to post some UI updation task on the the main thread's looper
        mHandler = new Handler();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_sign_up_with_google, container, false);

        //TODO: Validate the Phone No field
        final EditText phoneNo = (EditText) root.findViewById(R.id.person_phoneNo);
        //TODO: Validate the Password/Retyped Password field
        final EditText password = (EditText) root.findViewById(R.id.password);
        final EditText retypedPassword = (EditText) root.findViewById(R.id.retype_password);

        final Spinner spinner = (Spinner) root.findViewById(R.id.learner_tutor_status_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.learner_tutor_status, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //Retrieve the ProgressBar
        mProgressbarHolder = (FrameLayout)root.findViewById(R.id.progressBarHolder);

        Button createAccountButton = (Button) root.findViewById(R.id.create_account);
        createAccountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Log.d(TAG, "Starting the Indeterminate Progress Bar to show A/C creation process");
                //First and Foremost - Start the indeterminate progress bar till completion of A/C creation
                //on the server as well as the local Db
                inAnimation = new AlphaAnimation(0f, 1f);
                inAnimation.setDuration(200);
                mProgressbarHolder.setAnimation(inAnimation);
                mProgressbarHolder.setVisibility(View.VISIBLE);

                //Revised plan: Through this activity, we are aiming to populate a generic profile interface which collects
                //the necessary details for existence of an account. Then, next screen is gonna be "optionally" asking
                //for necessary tutor details which can be filled later depending on what was chosen on the account creation
                //activity. OR, it will lead directly lead to learner's profile otherwise

                //Important: Remember to validate the entity before stashing it
                String selectedStatus = spinner.getSelectedItem().toString();
                if(selectedStatus.equals("Learn")){
                    Log.i(TAG, "User is a Learner");

                    //For now, our generic learner is THE learner so we need to do a few things here:
                    //First: We need to start an async newAccountCreateOnServerAsyncTask to push the profile info. to local DB
                    //Second: We need to again start an async newAccountCreateOnServerAsyncTask for creation of account on the server
                    //Third: We need to create an account on the server

                    GenericLearnerProfileParcelable profile = new GenericLearnerProfileParcelable(profileFromGoogleAccount.getName(),
                            profileFromGoogleAccount.getEmailID(),
                            phoneNo.getText().toString(),
                            GenericLearnerProfileParcelable.STATUS_LEARNER,
                            password.getText().toString());

                    //------------------ACCOUNT CREATION AHEAD------------------------------------------------------------
                    //This is the point where we are going to communicate with backend.

                    //Following call creates an account on the server
                    createAccountOnServer(profile);

                    //Now that we have enough info. for an account/profile creation, lets populate it into a Profile object
                    //and write it to the Google Datastore. Don't forget though to queue the "Store this info." in the local
                    //SQL Lite DB. This would be used up for quick account validation instead of contacting server every frgiggin
                    //single time for profile population in the UI.

                    //Following call creates an account on the local Db
                    createAccountOnLocalDb(profile);
                }
                else{

                    /*
                    Log.i(TAG, "User wants to Tutor/Learn");
                    //Collect additional profile details from the user
                    GenericLearnerProfileParcelable profile = new TutorProfileParcelable(name.getText().toString(),
                            emailId.getText().toString(),
                            phoneNo.getText().toString(),
                            Integer.parseInt(selectedStatus),
                            password.getText().toString());
                    Intent i = new Intent(getActivity(), AdditionalAccountCreationInfoActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelable(GENERIC_PROFILE, profile);
                    startActivity(i.putExtras(b));
                    */
                    Log.i(TAG, "User is a Tutor");
                    //We need to do a few things here:
                    //First: We need to start an async newAccountCreateOnServerAsyncTask to push the profile info. to local DB
                    //Second: We need to again start an async newAccountCreateOnServerAsyncTask for creation of account on the server
                    //Third: We need to create an account on the server

                    GenericLearnerProfileParcelable profile = new TutorProfileParcelable(profileFromGoogleAccount.getName(),
                            profileFromGoogleAccount.getEmailID(),
                            phoneNo.getText().toString(),
                            GenericLearnerProfileParcelable.STATUS_TUTOR,
                            password.getText().toString());

                    //------------------ACCOUNT CREATION AHEAD------------------------------------------------------------
                    //This is the point where we are going to communicate with backend.

                    //Following call creates an account on the server
                    createAccountOnServer(profile);

                    //Now that we have enough info. for an account/profile creation, lets populate it into a Profile object
                    //and write it to the Google Datastore. Don't forget though to queue the "Store this info." in the local
                    //SQL Lite DB. This would be used up for quick account validation instead of contacting server every frgiggin
                    //single time for profile population in the UI.

                    //Following call creates an account on the local Db
                    createAccountOnLocalDb(profile);

                }
            }
        });

        return root;
    }
    @Override
    public void onPause(){
        //Cancelling the newAccountCreateOnServerAsyncTask of "Account creation". User is gonna have to reenter the info. on opening the App next time
        if(newAccountCreateOnServerAsyncTask != null){
            if(!newAccountCreateOnServerAsyncTask.isCancelled()){
                newAccountCreateOnServerAsyncTask.cancel(true);
                //TODO: Ensure that if this is an untimely cancellation then it will have to be recorded and also other implications must be taken care of
                //For consistency purposes, I propose the following:
                //Assumption: The server transaction takes considerably more than the transaction on the local Db
                //Therefore, if after cancelling the AsyncTask, it is past the point of detecting the cancellation
                //(through the cancellation check condition put in doInBackground()) then the transaction on the server
                //will end up getting completed. If the transaction on the server gets completed then the transaction on
                //the local Db has to be completed.
            }
        }
        super.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
        //Pressing back button from the home page for the first time will bring back to the
        //A/C creation page if we don't take care of it
        if(isAccountCreationOnLocalDbCompleted && isAccountCreationOnServerCompleted){
            getActivity().finish();
        }
    }
    private void createAccountOnServer(final GenericLearnerProfileParcelable profile){
        newAccountCreateOnServerAsyncTask = new NewAccountCreateOnServerAsyncTask();
        newAccountCreateOnServerAsyncTask.setAccountCreationListener(new NewAccountCreateOnServerAsyncTask.AccountCreationOnServerListener() {
            @Override
            public void onAccountCreated() {
                Log.d(TAG, "A/C successfully created on the server");
                //Stop the indeterminate progress bar
                isAccountCreationOnServerCompleted = true;
                if(isAccountCreationOnServerCompleted && isAccountCreationOnLocalDbCompleted){
                    Log.d(TAG, "Stopping the Indeterminate progress bar that was started to demonstrate the A/C creation process started");
                    outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    mProgressbarHolder.setAnimation(outAnimation);
                    mProgressbarHolder.setVisibility(View.GONE);

                    //Show the user the Account Home interface
                    if(profile.getCurrentStatus()== GenericLearnerProfileParcelable.STATUS_LEARNER){
                        startActivity(new Intent(SignUpWithGoogleAccountFragment.this.getActivity(), LearnerHomeActivity.class));
                    }
                    else{
                        startActivity(new Intent(SignUpWithGoogleAccountFragment.this.getActivity(), TutorHomeActivity.class));
                    }
                }
            }
            @Override
            public void onAccountCreationRetry(){
                //Indication that the Account creation has been unsuccessful and should be retried
                //Set a flag inside the Activity to store this indication;
                NewAccountCreationActivity.mShouldAccountCreationBeRetried = true;
            }
        });
        newAccountCreateOnServerAsyncTask.execute(profile);
        //newAccountCreateOnServerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, profile);
    }
    private void createAccountOnLocalDb(final GenericLearnerProfileParcelable profile){
        newAccountCreateLocalDbAsyncTask = new NewAccountCreateLocalDbAsyncTask(getActivity());
        newAccountCreateLocalDbAsyncTask.setAccountCreationonLocalDbListener(new NewAccountCreateLocalDbAsyncTask.AccountCreationOnLocalDbListener() {
            @Override
            public void onAccountCreated() {
                Log.d(TAG, "A/C successfully created on the the local Db");
                //Stop the indeterminate progress bar
                isAccountCreationOnLocalDbCompleted = true;
                if(isAccountCreationOnServerCompleted && isAccountCreationOnLocalDbCompleted){
                    Log.d(TAG, "Stopping the Indeterminate progress bar that was started to demonstrate the A/C creation process started");
                    outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    mProgressbarHolder.setAnimation(outAnimation);
                    mProgressbarHolder.setVisibility(View.GONE);

                    //Show the user the Account Home interface
                    if(profile.getCurrentStatus()== GenericLearnerProfileParcelable.STATUS_LEARNER){
                        startActivity(new Intent(SignUpWithGoogleAccountFragment.this.getActivity(), LearnerHomeActivity.class));
                    }
                    else{
                        startActivity(new Intent(SignUpWithGoogleAccountFragment.this.getActivity(), TutorHomeActivity.class));
                    }
                }
            }
        });
        newAccountCreateLocalDbAsyncTask.execute(profile);
        //newAccountCreateLocalDbAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, profile);
    }
}


