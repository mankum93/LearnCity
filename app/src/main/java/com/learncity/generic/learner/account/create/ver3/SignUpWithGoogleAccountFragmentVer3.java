package com.learncity.generic.learner.account.create.ver3;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.learncity.util.account_management.impl.GAEAccountCreationTaskVer2;
import com.learncity.util.account_management.impl.SQLiteAccountCreationTaskVer2;
import com.learncity.generic.learner.account.create.ver1.SignUpWithGoogleAccountActivityVer1;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learncity.R;
import com.learncity.learner.account.profile.model.LearnerProfile;
import com.learncity.learner.main.LearnerHomeActivity;
import com.learncity.tutor.account.profile.model.TutorProfile;
import com.learncity.tutor.main.TutorHomeActivity;
import com.learncity.util.MultiSpinner;
import com.learncity.util.account_management.impl.AccountCreationService;
import com.learncity.util.account_management.impl.AccountManager;

/**
 * Created by DJ on 10/30/2016.
 */

public class SignUpWithGoogleAccountFragmentVer3 extends Fragment{

    public static String TAG = "SignUpWithGoogleFrag";

    private GenericLearnerProfile profileFromGoogleAccount;

    private boolean isConditionalTutorUIVisible;

    private EditText phoneNo;
    private EditText password;
    private EditText retypedPassword;
    private Spinner spinner;
    private Button createAccountButton;

    private MultiSpinner<String> typeOfTutorMultiSpinner;
    private MultiSpinner<String> subjectsICanTeachMultiSpinner;

    private GenericLearnerProfile profile;

    private ViewGroup rootView;

    private LayoutInflater layoutInflater;
    private ViewGroup profileFieldsContainer;
    private View rootTutorConditionalLayout;

    private String[] tutorTypes;
    private String[] subjects;

    GAEAccountCreationTaskVer2 serverACCreationTask;
    SQLiteAccountCreationTaskVer2 localACCreationTask;

    private AccountCreationService accountCreationService;

    public static SignUpWithGoogleAccountFragmentVer3 newInstance(){
        return new SignUpWithGoogleAccountFragmentVer3();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        profileFromGoogleAccount = getActivity().getIntent().getParcelableExtra(SignUpWithGoogleAccountActivityVer1.EXTRAS_GENERIC_PROFILE_INCOMPLETE);
        //Fetch the AC creation service
        accountCreationService = AccountManager.fetchService(getActivity(), AccountManager.ACCOUNT_CREATION_SERVICE);
        //Set the listener on it
        setACCreationServiceListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        layoutInflater = inflater;

        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_sign_up_with_google_ver2, container, false);

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

        createAccountButton = (Button) rootView.findViewById(R.id.create_account);
        createAccountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //TODO: First and Foremost - Validate the input
                validateSubmittedInput();
                if(isConditionalTutorUIVisible){
                    validateConditionalTutorInput();
                }

                //First: We need to start an async newAccountCreateOnServerAsyncTask to push the profile info. to local DB
                //Second: We need to again start an async newAccountCreateOnServerAsyncTask for creation of account on the server
                //Third: We need to create an account on the server

                //Important: Remember to validate the entity before stashing it
                String selectedStatus = spinner.getSelectedItem().toString();
                if(selectedStatus.equals("Learn")){
                    Log.i(TAG, "User is a Learner");

                    //For now, our generic learner is THE learner.

                    profile = new LearnerProfile.Builder(
                            profileFromGoogleAccount.getName(),
                            profileFromGoogleAccount.getEmailID(),
                            phoneNo.getText().toString(),
                            GenericLearnerProfile.STATUS_LEARNER,
                            password.getText().toString()
                    ).build();
                    //Now, before getting onto finalizing the profile, make a final validation
                    profile = GenericLearnerProfile.validateGenericLearnerProfile(profile);
                }
                else{
                    Log.i(TAG, "User is a Tutor");

                    profile = new TutorProfile.Builder(
                            profileFromGoogleAccount.getName(),
                            profileFromGoogleAccount.getEmailID(),
                            phoneNo.getText().toString(),
                            GenericLearnerProfile.STATUS_TUTOR,
                            password.getText().toString())
                            .withTutorTypes(tutorTypes)
                            .withDisciplines(subjects)
                            .build();
                    profile = TutorProfile.validateTutorProfile((TutorProfile) profile);
                }

                //------------------ACCOUNT CREATION AHEAD------------------------------------------------------------

                //Configure the AC creation tasks
                serverACCreationTask = new GAEAccountCreationTaskVer2(profile);
                localACCreationTask = new SQLiteAccountCreationTaskVer2(getContext(), profile);

                accountCreationService.startAccountCreation(serverACCreationTask, localACCreationTask);
                accountCreationService.finishUp();
            }
        });

        return rootView;
    }
    private void setACCreationServiceListener(){
        accountCreationService.setAccountCreationListener(new AccountCreationService.AccountCreationListener() {
            @Override
            public void onAccountCreated() {
                if(profile.getCurrentStatus() == GenericLearnerProfile.STATUS_LEARNER){
                    startActivity(new Intent(SignUpWithGoogleAccountFragmentVer3.this.getActivity(), LearnerHomeActivity.class));
                }
                else{
                    startActivity(new Intent(SignUpWithGoogleAccountFragmentVer3.this.getActivity(), TutorHomeActivity.class));
                }
            }

            @Override
            public void onAccountCreationFailed() {
            }

            @Override
            public void onPreAccountCreation() {

            }

            @Override
            public void onAccountCreationServiceRefresh() {

            }
        });
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
        //TODO: Cancelling the newAccountCreateOnServerAsyncTask of "Account creation". User is gonna have to reenter the info. on opening the App next time

        super.onPause();
    }
    @Override
    public void onStop(){
        //Stop the AC creation service
        accountCreationService.shutDown();
        super.onStop();
    }
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}

