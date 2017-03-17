package com.learncity.generic.learner.account.create;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learncity.generic.learner.account.create.ver1.SignUpWithGoogleAccountActivityVer1;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learncity.R;

/**
 * Created by DJ on 10/30/2016.
 */

public class SignUpWithGoogleAccountFragmentVer4 extends SignUpFragment{

    public static String TAG = "SignUpWithGoogleFrag";

    public static final String SIGN_UP_WITH_GOOGLE = "SIGN_UP_WITH_GOOGLE";
    public static String EXTRAS_GENERIC_PROFILE_INCOMPLETE = "GENERIC_PROFILE_WITH_UNDEFINED_STATUS";

    private GenericLearnerProfile profileFromGoogleAccount;

    private ViewGroup rootView;

    public SignUpWithGoogleAccountFragmentVer4 newInstance(){
        return new SignUpWithGoogleAccountFragmentVer4();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        profileFromGoogleAccount = getActivity().getIntent().getParcelableExtra(SignUpWithGoogleAccountActivityVer1.EXTRAS_GENERIC_PROFILE_INCOMPLETE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = (ViewGroup)super.onCreateView(inflater, container, savedInstanceState);

        return rootView;
    }

    @Override
    protected View inflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up_with_google_ver2, container, false);
    }

    @Override
    public GenericLearnerProfile buildProfile(String status){
        GenericLearnerProfile profile = super.buildProfile(status);
        profile.setName(profileFromGoogleAccount.getName());
        profile.setEmailID(profileFromGoogleAccount.getEmailID());

        return  profile;
    }

}


