package com.learncity.generic.learner.account.create;


import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learncity.R;
import com.learncity.util.AbstractTextValidator;
import com.learncity.util.InputValidationHelper;
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

    private boolean invalidName;
    private boolean invalidEmailId;

    private ViewGroup rootView;

    public SignUpWithEmailFragmentVer4 newInstance(){
        return new SignUpWithEmailFragmentVer4();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup)super.onCreateView(inflater, container, savedInstanceState);

        name = (EditText) rootView.findViewById(R.id.person_name);
        //TODO: Validate the Email ID field
        emailId = (EditText) rootView.findViewById(R.id.person_emailId);
        // To support Email input type.
        emailId.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        //Set a TextWatcher for validating input
        emailId.addTextChangedListener(new AbstractTextValidator(emailId) {
            @Override
            public void validate(View view, String text) {
                // If not a valid email, set error.
                if(!InputValidationHelper.isValidEmail(text)){
                    emailId.setError("Not a valid Email ID.");
                }
            }
        });

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

    @Override
    void validateSubmittedInput(){
        validateName(name.getText().toString());
        validateEmailId(emailId.getText().toString());
        super.validateSubmittedInput();
    }

    @Override
    boolean isValidInput(){
        if(invalidName || invalidEmailId || !super.isValidInput()){
            return false;
        }
        return true;
    }

    private void validateName(String name){
        if(InputValidationHelper.isNullOrEmpty(name)){
            invalidInputText.append("Name can't be left blank.").append("\n");
            invalidName = true;
        }
        else{
            invalidName = false;
        }
    }

    private void validateEmailId(String emailID){
        if(InputValidationHelper.isNullOrEmpty(emailID)){
            invalidInputText.append("Email Id can't be left blank.").append("\n");
            invalidEmailId = true;
        }
        else{
            invalidEmailId = false;
        }
    }
}


