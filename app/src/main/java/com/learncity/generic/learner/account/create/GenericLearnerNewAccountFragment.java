package com.learncity.generic.learner.account.create;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelable;
import com.learncity.learn.account.create.NewLearnerAccountCreateAsyncTask;
import com.learncity.learncity.R;
import com.learncity.tutor.account.create.AdditionalAccountCreationInfoActivity;

/**
 * Created by DJ on 10/30/2016.
 */

public class GenericLearnerNewAccountFragment extends Fragment {

    public static String GENERIC_PROFILE = "com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelable";

    public static String TAG = "NewAccountFragment";
    public static GenericLearnerNewAccountFragment newInstance(){
        return new GenericLearnerNewAccountFragment();
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

        Button createAccountButton = (Button) root.findViewById(R.id.create_account);
        createAccountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //Important: Remember to validate the entity before stashing it
                String selectedStatus = spinner.getSelectedItem().toString();
                GenericLearnerProfileParcelable profile = new GenericLearnerProfileParcelable(name.getText().toString(),
                        emailId.getText().toString(),
                        phoneNo.getText().toString(),
                        password.getText().toString(),
                        selectedStatus);

                //Revised plan: Through this activity, we are aiming to populate a generic profile interface which collects
                //the necessary details for existence of an account. Then, next screen is gonna be "optionally" asking
                //for necessary tutor details which can be filled later depending on what was chosen on the account creation
                //activity. OR, it will lead directly lead to learner's profile otherwise

                if(selectedStatus == "Learn"){
                    Log.i(TAG, "User is a Learner");
                    //For now, our generic learner is THE learner so we need to do a few things here:
                    //First: We need to start an async task to push the profile info. to local DB
                    //Second: We need to again start an async task for creation of account on the server
                    //Third: We need to create an account on the server

                    //Create Account from this info. on the server with a profile bean.
                    //This is the point where we need are going to communicate with backend.
                    //TODO: Create an account on the server
                    new NewLearnerAccountCreateAsyncTask().execute(profile);

                    //Now that we have enough info. for an account/profile creation, lets populate it into a Profile object
                    //and write it to the Google Datastore. Don't forget though to queue the "Store this info." in the local
                    //SQL Lite DB. This would be used up for quick account validation instead of contacting server every frgiggin
                    //single time for profile population in the UI.
                    //TODO: Push the object into the local database as per the above comment
                }
                else{
                    Log.i(TAG, "User wants to Tutor/Learn");
                    //Collect additional profile details from the user
                    Intent i = new Intent(getActivity(), AdditionalAccountCreationInfoActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelable(GENERIC_PROFILE, profile);
                    startActivity(i.putExtras(b));
                }
            }
        });

        return root;
    }
}
