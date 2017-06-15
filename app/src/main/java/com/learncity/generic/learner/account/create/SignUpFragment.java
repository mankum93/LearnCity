package com.learncity.generic.learner.account.create;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.learncity.backend.learnerApi.LearnerApi;
import com.learncity.backend.tutorApi.TutorApi;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learncity.R;
import com.learncity.learner.account.profile.model.LearnerProfile;
import com.learncity.learner.main.LearnerHomeActivity;
import com.learncity.tutor.account.profile.model.TeachingCredits;
import com.learncity.tutor.account.profile.model.TutorProfile;
import com.learncity.tutor.main.TutorHomeActivity;
import com.learncity.util.AbstractTextValidator;
import com.learncity.util.InputValidationHelper;
import com.learncity.util.MultiSpinner;
import com.learncity.util.account_management.impl.AccountCreationService;
import com.learncity.util.account_management.impl.AccountManager;
import com.learncity.util.account_management.impl.GAEAccountCreationTaskVer2;
import com.learncity.util.account_management.impl.SQLiteAccountCreationTaskVer2;

import java.io.IOException;

import static com.learncity.LearnCityApplication.BACKEND_ROOT_URL;
import static com.learncity.tutor.jobs.TutorsFirebaseInstanceIDService.FIREBASE_TOKEN;
import static com.learncity.tutor.jobs.TutorsFirebaseInstanceIDService.IS_FIREBASE_TOKEN_STASH_PENDING;

/**
 * Created by DJ on 2/23/2017.
 */

public abstract class SignUpFragment extends Fragment {

    private final String TAG = getTag();

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

    private GAEAccountCreationTaskVer2 serverACCreationTask;
    private SQLiteAccountCreationTaskVer2 localACCreationTask;

    private AccountCreationService accountCreationService;

    private boolean invalidInput;
    private boolean invalidPhoneNo;
    private boolean invalidPwd;
    private String INVALID_INPUT_ERROR_MSG_PREFIX = "";
    StringBuilder invalidInputText = new StringBuilder(INVALID_INPUT_ERROR_MSG_PREFIX);
    private AlertDialog invalidInputAlertDialog;
    private boolean invalidStatus;
    private boolean invalidSubjects;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fetch the AC creation service
        accountCreationService = AccountManager.fetchService(getActivity(), AccountManager.ACCOUNT_CREATION_SERVICE);
        //Set the listener on it
        setACCreationServiceListener();
        //Set the Alertdialog
        setACCreationRetryDialog();
        //Set the progress dialog
        setACCreationProgressDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        layoutInflater = inflater;

        rootView = (ViewGroup) inflateLayout(inflater, container, savedInstanceState);

        phoneNo = (EditText) rootView.findViewById(R.id.person_phoneNo);
        //Add a PhoneNumberFormattingTextWatcher to format as a Phone Number.
        phoneNo.addTextChangedListener(new AbstractTextValidator(phoneNo) {
            @Override
            public void validate(View view, String text) {
                // If not a valid phone number, set error.
                if (!InputValidationHelper.isValidIndianMobileNo(phoneNo.getText().toString())) {
                    phoneNo.setError("Phone No must start with 6, 7, 8 or 9 only and must be of 10 digits.");
                }
            }
        });

        password = (EditText) rootView.findViewById(R.id.password);
        password.addTextChangedListener(new AbstractTextValidator(password) {
            @Override
            public void validate(View view, String text) {
                if (!InputValidationHelper.isValidPassword(text)) {
                    password.setError(InputValidationHelper.PWD_ERROR_TEXT);
                }
            }
        });
        retypedPassword = (EditText) rootView.findViewById(R.id.retype_password);
        retypedPassword.addTextChangedListener(new AbstractTextValidator(retypedPassword) {
            @Override
            public void validate(View view, String text) {
                if (!InputValidationHelper.isValidPassword(text)) {
                    retypedPassword.setError(InputValidationHelper.PWD_ERROR_TEXT);
                }
            }
        });

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
                if (selectedStatus.equals("Teach")) {
                    //Show the UI conditional to being a tutor IF not already visible
                    if (!isConditionalTutorUIVisible()) {
                        showConditionalTutorUI();
                    }
                } else if (selectedStatus.equals("Learn")) {
                    //If the UI conditional to being a tutor is visible, disable it
                    if (isConditionalTutorUIVisible()) {
                        disableConditionalTutorUI();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createAccountButton = (Button) rootView.findViewById(R.id.create_account);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validate the user input first.
                validateSubmittedInput();
                if (isConditionalTutorUIVisible) {
                    validateConditionalTutorInput();
                }
                if (invalidInput) {
                    // Show an Alert Dialog regarding invalid input.
                    showInvalidInputAlertDialog();
                    // Reset the Invalid input error message(StringBuilder)
                    invalidInputText.setLength(0);
                    invalidInputText.append(INVALID_INPUT_ERROR_MSG_PREFIX);
                    return;
                }

                //Important: Remember to validate the entity before stashing it
                String selectedStatus = spinner.getSelectedItem().toString();
                profile = buildProfile(selectedStatus);

                if (selectedStatus.equals("Learn")) {
                    Log.i(TAG, "User is a Learner");

                    //Now, before getting onto finalizing the profile, make a final validation
                    profile = GenericLearnerProfile.validateGenericLearnerProfile(profile);
                } else {
                    Log.i(TAG, "User is a Tutor");

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

    protected abstract Fragment newInstance();

    protected abstract View inflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected GenericLearnerProfile buildProfile(String status) {
        if (status.equals("Learn")) {
            return new LearnerProfile.Builder(
                    GenericLearnerProfile.NAME_NULL,
                    GenericLearnerProfile.EMAIL_NULL,
                    phoneNo.getText().toString(),
                    GenericLearnerProfile.STATUS_LEARNER,
                    password.getText().toString()
            ).build();
        } else {
            return new TutorProfile.Builder(
                    GenericLearnerProfile.NAME_NULL,
                    GenericLearnerProfile.EMAIL_NULL,
                    phoneNo.getText().toString(),
                    GenericLearnerProfile.STATUS_TUTOR,
                    password.getText().toString())
                    .withTutorTypes(tutorTypes)
                    .withDisciplines(subjects)
                    .withTeachingCredits(TeachingCredits.assignInitialCredits())
                    .build();
        }
    }

    void setACCreationServiceListener() {
        accountCreationService.setAccountCreationListener(new AccountCreationService.AccountCreationListener() {
            @Override
            public void onAccountCreated() {
                Log.d(TAG, "AccountCreationService.AccountCreationListener.onAccountCreated: Account creation " +
                        "process complete.");
                // If Firebase token stash is pending, stash it
                boolean isFirebaseTokenStashPending = getActivity().getSharedPreferences("MISC", 0)
                        .getBoolean(IS_FIREBASE_TOKEN_STASH_PENDING, false);
                if (isFirebaseTokenStashPending) {
                    String token = getActivity().getSharedPreferences("MISC", 0).getString(FIREBASE_TOKEN, null);
                    if (token == null) {
                        // Even though the Stash is pending, token is null
                        Log.e(TAG, "Firebase token is scheduled to be stashed but is null. Check the token" +
                                "generation process");
                    } else {
                        // TODO: Stash the token locally
                        Log.d(TAG, "Sending the request for stashing the Firebase token to the server...\n"
                                + "Email ID: " + profile.getEmailID() + "\n" +
                                "Token: " + token);
                        // Stash the token to the server
                        if (profile.getCurrentStatus() == GenericLearnerProfile.STATUS_LEARNER) {
                            LearnerApi myApiService = new LearnerApi.Builder(AndroidHttp.newCompatibleTransport(),
                                    new AndroidJsonFactory(), null)
                                    .setRootUrl(BACKEND_ROOT_URL)
                                    .setApplicationName("Learn City")
                                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                                        @Override
                                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                            abstractGoogleClientRequest.setDisableGZipContent(true);
                                        }
                                    }).build();
                            try {
                                myApiService.updateWithFirebaseToken(token, profile.getEmailID()).execute();
                            } catch (IOException e) {
                                Log.e(TAG, "Account couldn't be updated with Firebase token: IO Exception while performing the data-store transaction");
                                e.printStackTrace();
                            }
                        } else if (profile.getCurrentStatus() == GenericLearnerProfile.STATUS_TUTOR) {
                            Log.d(TAG, "Sending the request for stashing the Firebase token to the server...\n"
                                    + "Email ID: " + profile.getEmailID() + "\n" +
                                    "Token: " + token);
                            TutorApi myApiService = new TutorApi.Builder(AndroidHttp.newCompatibleTransport(),
                                    new AndroidJsonFactory(), null)
                                    .setRootUrl(BACKEND_ROOT_URL)
                                    .setApplicationName("Learn City")
                                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                                        @Override
                                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                            abstractGoogleClientRequest.setDisableGZipContent(true);
                                        }
                                    }).build();
                            try {
                                myApiService.updateWithFirebaseToken(token, profile.getEmailID()).execute();
                            } catch (IOException e) {
                                Log.e(TAG, "Account couldn't be updated with Firebase token: IO Exception while performing the data-store transaction");
                                e.printStackTrace();
                            }
                        } else {
                            Log.wtf(TAG, "User data locally found corrupt with symptom:\n" + "USER STATUS: " + profile.getCurrentStatus());
                            // TODO: Refresh profile from the server as and when appropriate and/or investigate the issue
                        }
                    }
                } else {
                    Log.i(TAG, "Firebase token has already been stashed!");
                }

                if (profile.getCurrentStatus() == GenericLearnerProfile.STATUS_LEARNER) {
                    startActivity(
                            new Intent(getActivity(), LearnerHomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    );
                } else {
                    startActivity(
                            new Intent(getActivity(), TutorHomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    );
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

    boolean isConditionalTutorUIVisible() {
        return isConditionalTutorUIVisible;
    }

    void showConditionalTutorUI() {
        // Initialize the first time
        if (typeOfTutorMultiSpinner == null && subjectsICanTeachMultiSpinner == null) {
            if (profileFieldsContainer == null) {
                profileFieldsContainer = (ViewGroup) rootView.findViewById(R.id.profile_fields_container);
            }
            if (rootTutorConditionalLayout == null) {
                rootTutorConditionalLayout = layoutInflater.inflate(
                        R.layout.layout_conditional_tutor_ui,
                        profileFieldsContainer,
                        false);
            }
            // Add the inflated layout to the second last of the container
            profileFieldsContainer.addView(rootTutorConditionalLayout, profileFieldsContainer.getChildCount());
            typeOfTutorMultiSpinner = (MultiSpinner) rootTutorConditionalLayout.findViewById(R.id.type_of_tutor_spinner);
            subjectsICanTeachMultiSpinner = (MultiSpinner) rootTutorConditionalLayout.findViewById(R.id.subjects_taught_spinner);

            // create spinner list elements
            final ArrayAdapter adapterTypeOfTutor = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_spinner_item,
                    getResources().getStringArray(R.array.type_of_tutor));
            ArrayAdapter adapterSubjects = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_spinner_item,
                    getResources().getStringArray(R.array.list_of_disciplines));

            // Set adapter
            typeOfTutorMultiSpinner.setAdapter(adapterTypeOfTutor, false, new com.thomashaertel.widget.MultiSpinner.MultiSpinnerListener() {
                @Override
                public void onItemsSelected(boolean[] selected) {
                    // Stash the list of tutor-types somewhere
                    tutorTypes = typeOfTutorMultiSpinner.getSelectedItemsArray(String.class);
                }
            });
            subjectsICanTeachMultiSpinner.setAdapter(adapterSubjects, false, new com.thomashaertel.widget.MultiSpinner.MultiSpinnerListener() {
                @Override
                public void onItemsSelected(boolean[] selected) {
                    // Stash the list of subjects somewhere
                    subjects = subjectsICanTeachMultiSpinner.getSelectedItemsArray(String.class);
                }
            });
            typeOfTutorMultiSpinner.setInitialDisplayText("Select status...");
            subjectsICanTeachMultiSpinner.setInitialDisplayText("Select subjects...");
        } else {
            // Initialized but not visible
            rootTutorConditionalLayout.setVisibility(View.VISIBLE);
        }
        isConditionalTutorUIVisible = true;
    }

    void disableConditionalTutorUI() {
        rootTutorConditionalLayout.setVisibility(View.GONE);
        isConditionalTutorUIVisible = false;
    }

    void validateConditionalTutorInput() {
        if (typeOfTutorMultiSpinner.getSelectedItemsCount() == 0) {
            // No item selected
            Log.w(TAG, "No item selected; can't continue like this");
            invalidInputText.append("Please select at least one status to continue.").append("\n");
            invalidStatus = true;
        }
        else{
            invalidStatus = false;
        }
        if (subjectsICanTeachMultiSpinner.getSelectedItemsCount() == 0) {
            // No item selected
            Log.w(TAG, "No item selected; can't continue like this");
            invalidInputText.append("Please select at least one subject to continue.").append("\n");
            invalidSubjects = true;
        }
        else{
            invalidSubjects = false;
        }
    }

    void validateSubmittedInput() {
        // If not a valid phone number typed.

        validatePhoneNo(phoneNo.getText().toString());
        validatePwdAndRetypedPwd(password.getText().toString(), retypedPassword.getText().toString());

        if (!isValidInput()) {
            setValidInputStatus(true);
        } else {
            setValidInputStatus(false);
        }
    }

    boolean isValidInput() {
        if (invalidPwd || invalidPhoneNo || invalidStatus || invalidSubjects) {
            return false;
        }
        return true;
    }

    void setValidInputStatus(boolean status) {
        invalidInput = status;
    }

    private void validatePwdAndRetypedPwd(String pwd, String retypedPwd) {
        // Is the typed password valid ?
        if (!InputValidationHelper.isValidPassword(pwd)) {
            invalidInputText.append("The typed password is invalid.").append("\n");
            invalidPwd = true;
        } else {
            // Typed password is valid but the retyped one doesn't match the typed one.
            if (!pwd.equals(retypedPwd)) {
                Log.w(TAG, "The retyped password didn't match the typed password.");
                invalidInputText.append("The retyped password doesn't match the typed password.").append("\n");
                invalidPwd = true;
            } else {
                invalidPwd = false;
            }
        }
    }

    private void validatePhoneNo(String phoneNo) {
        // Phone No should be 10 digits exactly(Indian mobile numbers)
        if (!InputValidationHelper.isValidIndianMobileNo(phoneNo)) {
            Log.w(TAG, "Phone No length is " + phoneNo.length() + "\n"
                    + "It should be 10 characters exactly");
            invalidInputText.append("Phone No is invalid.").append("\n");
            invalidPhoneNo = true;
        } else {
            invalidPhoneNo = false;
        }
    }

    private void setACCreationRetryDialog() {
        accountCreationService.setACCreationRetryAlertDialog(new AlertDialog.Builder(getActivity())
                .setTitle("Account Creation Failed")
                .setMessage("There was a problem creating the Account")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "TaskProcessor.showRetryDialog(): " + "\n" + "MESSAGE: RETRY button clicked..." +
                                "\n" + "Thread ID: " + Thread.currentThread().getId());
                        accountCreationService.retryOnFailedAccountCreation();
                        dialog.cancel();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "TaskProcessor.showRetryDialog(): " + "\n" + "MESSAGE: CANCEL button clicked..." +
                                "\n" + "Thread ID: " + Thread.currentThread().getId());
                        accountCreationService.cancelOnFailedAccountCreation();
                        dialog.cancel();

                    }
                })
                .setCancelable(false)
                .create());
    }

    private void setACCreationProgressDialog() {

        ProgressDialog taskProcessingProgressDialog = new ProgressDialog(getActivity());
        taskProcessingProgressDialog.setIndeterminate(true);
        taskProcessingProgressDialog.setTitle("Creating Account...");
        taskProcessingProgressDialog.setCancelable(true);
        taskProcessingProgressDialog.setCanceledOnTouchOutside(false);

        accountCreationService.setAccountCreationProgressDialog(taskProcessingProgressDialog);
    }

    private void showInvalidInputAlertDialog() {
        // Remove a newline from the StringBuilder.
        invalidInputText.setLength(invalidInputText.length() - 1);

        if (invalidInputAlertDialog == null) {
            invalidInputAlertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Invalid input.")
                    .setMessage(invalidInputText.toString())
                    .setCancelable(true)
                    .create();
            invalidInputAlertDialog.show();
        } else {
            if (!invalidInputAlertDialog.isShowing()) {
                // But before showing, update the content to show.
                invalidInputAlertDialog.setMessage(invalidInputText.toString());
                invalidInputAlertDialog.show();
            } else {
                Log.w(TAG, "Invalid input Alert Dialog already showing and yet has been" +
                        "asked to show again. This behavior should be checked.");
            }
        }
    }

    @Override
    public void onPause() {
        // TODO: Cancelling the newAccountCreateOnServerAsyncTask of "Account creation". User is gonna have to reenter the info. on opening the App next time

        super.onPause();
    }

    @Override
    public void onStop() {
        // Stop the AC creation service
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Pressing back button from the home page for the first time will bring back to the
        // A/C creation page if we don't take care of it
    }

    @Override
    public void onDestroy() {
        accountCreationService.shutDown();
        super.onDestroy();
    }
}
