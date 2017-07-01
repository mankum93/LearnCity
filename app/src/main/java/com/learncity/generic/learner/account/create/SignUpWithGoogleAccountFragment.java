package com.learncity.generic.learner.account.create;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learncity.R;

/**
 * Created by DJ on 10/30/2016.
 */

public class SignUpWithGoogleAccountFragment extends SignUpFragment{

    public static String TAG = "SignUpWithGoogleFrag";

    public static final String SIGN_UP_WITH_GOOGLE = "SIGN_UP_WITH_GOOGLE";
    public static String EXTRAS_GENERIC_PROFILE_INCOMPLETE = "GENERIC_PROFILE_WITH_UNDEFINED_STATUS";

    private GenericLearnerProfile profileFromGoogleAccount;

    public SignUpWithGoogleAccountFragment newInstance(){
        return new SignUpWithGoogleAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        profileFromGoogleAccount = getActivity().getIntent().getParcelableExtra(EXTRAS_GENERIC_PROFILE_INCOMPLETE);

    }

    @Override
    protected View inflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up_with_google, container, false);
    }

    @Override
    public GenericLearnerProfile buildProfile(String status){
        GenericLearnerProfile profile = super.buildProfile(status);
        profile.setName(profileFromGoogleAccount.getName());
        profile.setEmailID(profileFromGoogleAccount.getEmailID());

        return  profile;
    }

}


