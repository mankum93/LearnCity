package com.learncity.generic.learner.account.create.ver0;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.learncity.generic.learner.account.profile.model.ver0.GenericLearnerProfileParcelable;
import com.learncity.learncity.R;
import com.learncity.learner.main.LearnerHomeActivity;
import com.learncity.tutor.account.profile.model.qualification.educational.ver0.TutorProfileParcelable;
import com.learncity.tutor.main.TutorHomeActivity;
import com.learncity.util.MultiSpinner;


/**
 * Created by DJ on 10/30/2016.
 */

public class SignUpWithEmailFragment extends Fragment{

    public static String GENERIC_PROFILE = "com.learncity.generic.learner.account.profile.model.ver0.GenericLearnerProfileParcelable";

    public static String TAG = "SignUpWithEmailFragment";

    private NewAccountCreateOnServerAsyncTask newAccountCreateOnServerAsyncTask;
    private NewAccountCreateOnLocalDbAsyncTask newAccountCreateOnLocalDbAsyncTask;
    FrameLayout mProgressbarHolder;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    //Reserved to be used inside any callback method to post some UI updation task on the the main thread's looper
    Handler mHandler;

    private boolean isAccountCreationOnServerCompleted = false;
    private boolean isAccountCreationOnLocalDbCompleted = false;

    private boolean isConditionalTutorUIVisible;

    private EditText name;
    private EditText emailId;
    private EditText phoneNo;
    private EditText password;
    private EditText retypedPassword;
    private Spinner spinner;
    private Button createAccountButton;

    private MultiSpinner<String> typeOfTutorMultiSpinner;
    private MultiSpinner<String> subjectsICanTeachMultiSpinner;

    private GenericLearnerProfileParcelable profile;

    private ViewGroup rootView;
    private LayoutInflater layoutInflater;
    private ViewGroup profileFieldsContainer;
    private View rootTutorConditionalLayout;
    private String[] tutorTypes;
    private String[] subjects;

    public static boolean shouldAccountCreationBeRetried() {
        return NewAccountCreationActivity.mShouldAccountCreationBeRetried;
    }

    public static SignUpWithEmailFragment newInstance(){
        return new SignUpWithEmailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        //RESET: A/C creation retry is a flag set up to prevent the creation of A/C on local Db if it hasn't
        //been created on the server. Even if the A/C creation goes successful for n number of times,
        //for the (n+1)th time, it should be reset so that A/C creation can proceed normally
        NewAccountCreationActivity.mShouldAccountCreationBeRetried = false;

        super.onCreate(savedInstanceState);

        //Reserved to be used inside any callback method to post some UI updation task on the the main thread's looper
        mHandler = new Handler();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        layoutInflater = inflater;

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_sign_up_with_email, container, false);

        name = (EditText) rootView.findViewById(R.id.person_name);
        //TODO: Validate the Email ID field
        emailId = (EditText) rootView.findViewById(R.id.person_emailId);
        //TODO: Validate the Phone No field
        phoneNo = (EditText) rootView.findViewById(R.id.person_phoneNo);
        //TODO: Validate the Password/Retyped Password field
        password = (EditText) rootView.findViewById(R.id.password);
        retypedPassword = (EditText) rootView.findViewById(R.id.retype_password);

        spinner = (Spinner) rootView.findViewById(R.id.learner_tutor_status_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.learner_tutor_status, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = (String) parent.getSelectedItem();
                if(selectedStatus.equals("Teach")){
                    //Show the UI conditional to being a tutor IF not already visible
                    if(!isConditionalTutorUIVisible()){
                        showConditionalTutorUI();
                    }
                }
                else if(selectedStatus.equals("Learn")){
                    //If the UI conditional to being a tutor is visible, disable it
                    if(isConditionalTutorUIVisible()){
                        disableConditionalTutorUI();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Retrieve the ProgressBar
        mProgressbarHolder = (FrameLayout) rootView.findViewById(R.id.progressBarHolder);

        createAccountButton = (Button) rootView.findViewById(R.id.create_account);
        createAccountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //TODO: First and Foremost - Validate the input
                validateSubmittedInput();
                if(isConditionalTutorUIVisible){
                    validateConditionalTutorInput();
                }

                Log.d(TAG, "Starting the Indeterminate Progress Bar to show A/C creation process");
                //First and Foremost - Start the indeterminate progress bar till completion of A/C creation
                //on the server as well as the local Db
                inAnimation = new AlphaAnimation(0f, 1f);
                inAnimation.setDuration(200);
                mProgressbarHolder.setAnimation(inAnimation);
                mProgressbarHolder.setVisibility(View.VISIBLE);

                //First: We need to start an async newAccountCreateOnServerAsyncTask to push the profile info. to local DB
                //Second: We need to again start an async newAccountCreateOnServerAsyncTask for creation of account on the server
                //Third: We need to create an account on the server

                //Important: Remember to validate the entity before stashing it
                String selectedStatus = spinner.getSelectedItem().toString();
                if(selectedStatus.equals("Learn")){
                    Log.i(TAG, "User is a Learner");

                    //For now, our generic learner is THE learner.

                    profile = new GenericLearnerProfileParcelable(name.getText().toString(),
                            emailId.getText().toString(),
                            phoneNo.getText().toString(),
                            GenericLearnerProfileParcelable.STATUS_LEARNER,
                            password.getText().toString());
                }
                else{
                    Log.i(TAG, "User is a Tutor");

                    profile = new TutorProfileParcelable(name.getText().toString(),
                            emailId.getText().toString(),
                            phoneNo.getText().toString(),
                            GenericLearnerProfileParcelable.STATUS_TUTOR,
                            password.getText().toString());
                }
                //------------------ACCOUNT CREATION AHEAD------------------------------------------------------------

                createAccountOnServer(profile);
                createAccountOnLocalDb(profile);
            }
        });

        return rootView;
    }

    private boolean isConditionalTutorUIVisible() {
        return isConditionalTutorUIVisible;
    }

    private void showConditionalTutorUI() {
        //Initialize the first time
        if(typeOfTutorMultiSpinner == null && subjectsICanTeachMultiSpinner == null){
            profileFieldsContainer = (ViewGroup)rootView.findViewById(R.id.profile_fields_container);
            rootTutorConditionalLayout = layoutInflater.inflate(
                    R.layout.layout_conditional_tutor_ui,
                    profileFieldsContainer,
                    false);
            //Add the inflated layout to the second last of the container
            profileFieldsContainer.addView(rootTutorConditionalLayout, profileFieldsContainer.getChildCount()-2);
            typeOfTutorMultiSpinner = (MultiSpinner) rootTutorConditionalLayout.findViewById(R.id.type_of_tutor_spinner);
            subjectsICanTeachMultiSpinner = (MultiSpinner) rootTutorConditionalLayout.findViewById(R.id.subjects_taught_spinner);

            // create spinner list elements
            ArrayAdapter adapterTypeOfTutor = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_spinner_item,
                    getResources().getStringArray(R.array.type_of_tutor));
            ArrayAdapter adapterSubjects = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_spinner_item,
                    getResources().getStringArray(R.array.list_of_disciplines));

            // Set adapter
            typeOfTutorMultiSpinner.setAdapter(adapterTypeOfTutor, false, new com.thomashaertel.widget.MultiSpinner.MultiSpinnerListener() {
                @Override
                public void onItemsSelected(boolean[] selected) {
                    //Stash the list of tutor-types somewhere
                    tutorTypes = typeOfTutorMultiSpinner.getSelectedItemsArray(String.class);
                }
            });
            subjectsICanTeachMultiSpinner.setAdapter(adapterSubjects, false, new com.thomashaertel.widget.MultiSpinner.MultiSpinnerListener() {
                @Override
                public void onItemsSelected(boolean[] selected) {
                    //Stash the list of subjects somewhere
                    subjects = subjectsICanTeachMultiSpinner.getSelectedItemsArray(String.class);
                }
            });
            typeOfTutorMultiSpinner.setInitialDisplayText("Select status");
            subjectsICanTeachMultiSpinner.setInitialDisplayText("Select subjects");
        }
        else{
            //Initialized but not visible
            rootTutorConditionalLayout.setVisibility(View.VISIBLE);
        }
        isConditionalTutorUIVisible = true;
    }

    private void disableConditionalTutorUI() {
        rootTutorConditionalLayout.setVisibility(View.GONE);
        isConditionalTutorUIVisible = false;
    }

    private void validateConditionalTutorInput(){
        if(typeOfTutorMultiSpinner.getSelectedItemsCount() == 0){
            //No item selected
            Log.e(TAG, "No item selected; can't continue like this");
            //TODO: Prompt the user in the UI about this
        }
        if(subjectsICanTeachMultiSpinner.getSelectedItemsCount() == 0){
            //No item selected
            Log.e(TAG, "No item selected; can't continue like this");
            //TODO: Prompt the user in the UI about this
        }
    }

    private void validateSubmittedInput() {
        validatePhoneNo();
        validatePassword();
    }

    private void validatePassword() {
        //Retyped password didn't match the Password
        if(!password.getText().toString().equals(retypedPassword.getText().toString())){
            Log.e(TAG, "Retyped password didn't match the typed password");
            //TODO: Prompt the user in the UI about this
        }
    }

    private void validatePhoneNo() {
        //Phone No should be 10 digits exactly(Indian mobile numbers)
        if(phoneNo.length()!=10){
            Log.e(TAG, "Phone No length is " + phoneNo.length() + "\n"
                    + "It should be 10 characters exactly");
            //TODO: Prompt the user in the UI about this
        }
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
                        startActivity(new Intent(SignUpWithEmailFragment.this.getActivity(), LearnerHomeActivity.class));
                    }
                    else{
                        startActivity(new Intent(SignUpWithEmailFragment.this.getActivity(), TutorHomeActivity.class));
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
        newAccountCreateOnLocalDbAsyncTask = new NewAccountCreateOnLocalDbAsyncTask(getActivity());
        newAccountCreateOnLocalDbAsyncTask.setAccountCreationonLocalDbListener(new NewAccountCreateOnLocalDbAsyncTask.AccountCreationOnLocalDbListener() {
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
                        startActivity(new Intent(SignUpWithEmailFragment.this.getActivity(), LearnerHomeActivity.class));
                    }
                    else{
                        startActivity(new Intent(SignUpWithEmailFragment.this.getActivity(), TutorHomeActivity.class));
                    }
                }
            }
        });
        newAccountCreateOnLocalDbAsyncTask.execute(profile);
        //newAccountCreateOnLocalDbAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, profile);
    }
}


