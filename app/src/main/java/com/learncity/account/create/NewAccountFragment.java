package com.learncity.account.create;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.learncity.account.profile.model.MyProfile;
import com.learncity.learncity.R;

/**
 * Created by DJ on 10/30/2016.
 */

public class NewAccountFragment extends Fragment {
    public static NewAccountFragment newInstance(){
        return new NewAccountFragment();
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

        //Now that we have enough info. for an account/profile creation, lets populate it into a Profile object
        //and write it to the Google Datastore. Don't forget though to queue the "Store this info." in the local
        //SQL Lite DB. This would be used up for quick account validation instead of contacting server every frgiggin
        //single time for profile population in the UI.
        //TODO: Push the object into the local database as per the above comment

        Button createAccountButton = (Button) root.findViewById(R.id.create_account);
        createAccountButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Create Account from this info. on the server with a profile bean.
                //This is the point where we need are going to communicate with backend.
                //TODO: Create account on the server

                //Important: Remember to validate the entity before stashing it
                MyProfile profile = new MyProfile(name.getText().toString(),
                        emailId.getText().toString(),
                        phoneNo.getText().toString(),
                        password.getText().toString(),
                        spinner.getSelectedItem().toString());

                new NewAccountCreateAsyncTask().execute(profile);
            }
        });

        return root;
    }
}
