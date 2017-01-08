package com.learncity.generic.learner.account.create;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.json.GenericJson;
import com.learncity.backend.persistence.genericLearnerProfileApi.GenericLearnerProfileApi;
import com.learncity.backend.persistence.genericLearnerProfileApi.model.GenericLearnerProfile;
import com.learncity.backend.persistence.tutorProfileApi.TutorProfileApi;
import com.learncity.backend.persistence.tutorProfileApi.model.TutorProfile;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelable;
import com.learncity.learncity.R;
import com.learncity.generic.learner.account.profile.database.ProfileDbHelper;
import com.learncity.learner.main.HomeActivity;
import com.learncity.tutor.account.profile.model.TutorProfileParcelable;

import java.io.IOException;

/**
 * Created by DJ on 10/30/2016.
 */

public class NewAccountFragment extends Fragment{

    public static String GENERIC_PROFILE = "com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelable";

    public static String TAG = "NewAccountFragment";

    private NewAccountCreateOnServerAsyncTask newAccountCreateOnServerAsyncTask;
    private NewAccountCreateLocalDbAsyncTask newAccountCreateLocalDbAsyncTask;
    FrameLayout mProgressbarHolder;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    //Reserved to be used inside any callback method to post some UI updation task on the the main thread's looper
    Handler mHandler;

    private boolean isAccountCreationOnServerCompleted = false;
    private boolean isAccountCreationOnLocalDbCompleted = false;

    //This has to be made static in order to be accessible to the A/C creation AsyncTasks so
    //that they can know about the A/C creation retrial process
    private static boolean mShouldAccountCreationBeRetried = false;

    public static boolean shouldAccountCreationBeRetried() {
        return mShouldAccountCreationBeRetried;
    }

    public static NewAccountFragment newInstance(){
        return new NewAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        //RESET: A/C creation retry is a flag set up to prevent the creation of A/C on local Db if it hasn't
        //been created on the server. Even if the A/C creation goes successful for n number of times,
        //for the (n+1)th time, it should be reset so that A/C creation can proceed normally
        mShouldAccountCreationBeRetried = false;

        super.onCreate(savedInstanceState);

        //Reserved to be used inside any callback method to post some UI updation task on the the main thread's looper
        mHandler = new Handler();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.new_account_fragment_layout, container, false);

        final EditText name = (EditText) root.findViewById(R.id.person_name);
        //TODO: Validate the Email ID field
        final EditText emailId = (EditText) root.findViewById(R.id.person_emailId);
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

                    GenericLearnerProfileParcelable profile = new GenericLearnerProfileParcelable(name.getText().toString(),
                            emailId.getText().toString(),
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

                    GenericLearnerProfileParcelable profile = new TutorProfileParcelable(name.getText().toString(),
                            emailId.getText().toString(),
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
    private void createAccountOnServer(GenericLearnerProfileParcelable profile){
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
                    startActivity(new Intent(NewAccountFragment.this.getActivity(), HomeActivity.class));
                }
            }
            @Override
            public void onAccountCreationRetry(){
                //Indication that the Account creation has been unsuccessful and should be retried
                //Set a flag inside the Activity to store this indication;
                mShouldAccountCreationBeRetried = true;
            }
        });
        newAccountCreateOnServerAsyncTask.execute(profile);
        //newAccountCreateOnServerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, profile);
    }
    private void createAccountOnLocalDb(GenericLearnerProfileParcelable profile){
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
                    startActivity(new Intent(NewAccountFragment.this.getActivity(), HomeActivity.class));
                }
            }
        });
        newAccountCreateLocalDbAsyncTask.execute(profile);
        //newAccountCreateLocalDbAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, profile);
    }
}

/**
 * Created by DJ on 10/30/2016.
 */

class NewAccountCreateOnServerAsyncTask extends AsyncTask<GenericLearnerProfileParcelable, Void, Void> {
    private static final String TAG = "NewAccountAsyncTask";

    private static AbstractGoogleJsonClient myApiService = null;

    private boolean isAccountCreationComplete = false;
    private boolean shouldAccountCreationBeRetried = false;

    public boolean shouldAccountCreationBeRetried() {
        return shouldAccountCreationBeRetried;
    }

    public boolean isAccountCreationComplete() {
        return isAccountCreationComplete;
    }

    private AccountCreationOnServerListener mAccountCreationOnServerListener;

    public static void setApiService(GenericLearnerProfileParcelable profile) {
        AbstractGoogleJsonClient.Builder builder = selectBuilder(profile)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setApplicationName("Learn City")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        // end options for devappserver


        myApiService = builder.build();
    }
    private static AbstractGoogleJsonClient.Builder selectBuilder(GenericLearnerProfileParcelable profile){
        if(profile instanceof TutorProfileParcelable){
            return new TutorProfileApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null);
        }
        return new GenericLearnerProfileApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null);
    }

    @Override
    protected Void doInBackground(GenericLearnerProfileParcelable... params) {

        //Now, get the profile info./object that needs to be pushed to the datastore
        GenericLearnerProfileParcelable profile = params[0];

        if(myApiService == null) {  // Only do this once
            setApiService(profile);
        }

        //Populate the entity object with the profile info.
        GenericJson profileEntity = populateProfileEntity(profile);

        if(isCancelled()){
            shouldAccountCreationBeRetried = true;
            mAccountCreationOnServerListener.onAccountCreationRetry();
            return null;
        }
        //Now push the info. to the database through the right service
        try{
            if(myApiService instanceof GenericLearnerProfileApi){
                ((GenericLearnerProfileApi)myApiService).insert((GenericLearnerProfile) profileEntity).execute();
                //The execute method returns an instance of inserted object so I am assuming that implies successful
                //transaction to the database
                isAccountCreationComplete = true;
            }
            else if(myApiService instanceof TutorProfileApi){
                ((TutorProfileApi)myApiService).insert((TutorProfile) profileEntity).execute();
                //The execute method returns an instance of inserted object so I am assuming that implies successful
                //transaction to the database
                isAccountCreationComplete = true;
            }

        }
        catch(IOException e){
            Log.e(TAG, "Account couldn't be created : IO Exception while performing the data-store transaction");
            e.printStackTrace();
            //The Account couldn't be created.

            //TODO: Show a Retry Dialog to retry  the account creation process
        }
        return null;

    }

    @Override
    protected void onPostExecute(Void result) {
        if(!isAccountCreationComplete){
            //1. Must have gotten an exception while performing transaction in the database.
            // OR,
            //2. This AsyncTask got cancelled and doInBackground() returned null
            // RETRY PLEASE!!!
            shouldAccountCreationBeRetried = true;
            mAccountCreationOnServerListener.onAccountCreationRetry();
        }
        else{
            //Account creation complete. Call the account creation callback
            if(mAccountCreationOnServerListener == null){
                throw new RuntimeException("Account Creation listener has not been set for account creation on the server even though the account creation has been successful");
            }
            mAccountCreationOnServerListener.onAccountCreated();
        }
    }

    private GenericJson populateProfileEntity(GenericLearnerProfileParcelable profile){
        //Populate the entity object with the profile info.

        //GenericJson is the base type for the entities generated by the client lib-generator. This means that
        //I can't have inheritance between the 2 different entities event though it makes sense to have it. In other words
        //the entity classes generated by the lib-generator will be always be separate GenericJson types which is why there
        //is redundant code here
        GenericJson profileEntity = null;

        if(profile instanceof TutorProfileParcelable){
            TutorProfileParcelable tutorProfile = (TutorProfileParcelable)profile;

            profileEntity = new TutorProfile();
            TutorProfile profileEntityTutor = (TutorProfile) profileEntity;

            profileEntityTutor.setName(tutorProfile.getName());
            profileEntityTutor.setEmailID(tutorProfile.getEmailID());
            profileEntityTutor.setPhoneNo(tutorProfile.getPhoneNo());
            profileEntityTutor.setPassword(tutorProfile.getPassword());
            profileEntityTutor.setCurrentStatus(tutorProfile.getCurrentStatus());
        }
        else{
            profileEntity = new GenericLearnerProfile();
            GenericLearnerProfile profileLearnerEntity = (GenericLearnerProfile)profileEntity;

            profileLearnerEntity.setName(profile.getName());
            profileLearnerEntity.setEmailID(profile.getEmailID());
            profileLearnerEntity.setPhoneNo(profile.getPhoneNo());
            profileLearnerEntity.setPassword(profile.getPassword());
            profileLearnerEntity.setCurrentStatus(profile.getCurrentStatus());
        }

        return profileEntity;
    }

    void setAccountCreationListener(AccountCreationOnServerListener accountCreationOnServerListener){
        mAccountCreationOnServerListener = accountCreationOnServerListener;
    }

    interface AccountCreationOnServerListener {
        void onAccountCreated();
        void onAccountCreationRetry();
    }

}


class NewAccountCreateLocalDbAsyncTask extends AsyncTask<GenericLearnerProfileParcelable, Void, Void>{

    private static String TAG = "NewAccountOnLocalDb";
    private Context context;
    private AccountCreationOnLocalDbListener mAccountCreationOnLocalDbListener;
    private boolean isAccountCreationComplete = false;

    public boolean isAccountCreationComplete() {
        return isAccountCreationComplete;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public NewAccountCreateLocalDbAsyncTask(Context context){
        this.context = context;
    }

    @Override
    public Void doInBackground(GenericLearnerProfileParcelable... params){
        if(context == null){
            throw new RuntimeException("Context to be passed during Db creation is null");
        }
        //If the A/C has NOT been created on the server, don't mind creating it on the local Db either
        if(NewAccountFragment.shouldAccountCreationBeRetried()){
            Log.d(TAG, "NewAccountCreateLocalDbAsyncTask - A/C creation on the local Db has been stopped due to"
            + "it being unable of being created on the server");
            return null;
        }
        //Get the data to be inserted
        GenericLearnerProfileParcelable profile = params[0];
        //Get the writable database
        SQLiteDatabase db = new ProfileDbHelper(context, profile.getCurrentStatus()).getWritableDatabase();
        //Insert into the database
        ProfileDbHelper.addProfileToDatabase(db, profile);

        //Account creation complete
        isAccountCreationComplete = true;
        return null;
    }
    @Override
    public void onPostExecute(Void v){
        if(isAccountCreationComplete){
            if(mAccountCreationOnLocalDbListener == null){
                throw new RuntimeException("Account Creation listener has not been set for account creation on the local Db even though the account creation has been successful");
            }
            mAccountCreationOnLocalDbListener.onAccountCreated();
        }
        else{
            //If the account has been created just on the server and not in the Db then that has already
            //been taken care of - Account shall have to be recreated both on the server as well as the local Db
        }
    }

    void setAccountCreationonLocalDbListener(AccountCreationOnLocalDbListener accountCreationOnLocalDbListener){
        mAccountCreationOnLocalDbListener = accountCreationOnLocalDbListener;
    }

    interface AccountCreationOnLocalDbListener {
        void onAccountCreated();
    }
}