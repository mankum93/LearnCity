package com.learncity.generic.learner.account.create;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learncity.R;
import com.learncity.util.account_management.impl.AccountCreationService;
import com.learncity.util.account_management.impl.AccountManager;


/**
 * Created by DJ on 10/30/2016.
 */

public class SignUpWithEmailFragmentVer4 extends SignUpFragment{

    public static String TAG = "SignUpWithEmailFragment";

    public static final String SIGN_UP_WITH_EMAIL = "SIGN_UP_WITH_EMAIL";

    private EditText name;
    private EditText emailId;

    private ViewGroup rootView;

    private AccountCreationService accountCreationService;

    public SignUpWithEmailFragmentVer4 newInstance(){
        return new SignUpWithEmailFragmentVer4();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Fetch the AC creation service
        accountCreationService = AccountManager.fetchService(getActivity(), AccountManager.ACCOUNT_CREATION_SERVICE);
        //Set the listener on it
        setACCreationServiceListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup)super.onCreateView(inflater, container, savedInstanceState);

        name = (EditText) rootView.findViewById(R.id.person_name);
        //TODO: Validate the Email ID field
        emailId = (EditText) rootView.findViewById(R.id.person_emailId);

        return rootView;
    }

    @Override
    protected View inflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up_with_email_ver2, container, false);
    }

    @Override
    public GenericLearnerProfile buildProfile(String status){
        GenericLearnerProfile profile = super.buildProfile(status);
        profile.setName(name.getText().toString());
        profile.setEmailID(emailId.getText().toString());

        return  profile;
    }
}


