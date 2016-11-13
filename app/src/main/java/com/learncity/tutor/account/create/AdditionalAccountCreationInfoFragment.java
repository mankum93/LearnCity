package com.learncity.tutor.account.create;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.learncity.generic.learner.account.create.GenericLearnerNewAccountFragment;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelable;
import com.learncity.learncity.R;
import com.learncity.tutor.account.profile.model.TutorProfileParcelable;

/**
 * Created by DJ on 11/12/2016.
 */

public class AdditionalAccountCreationInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.tutor_new_account_additional_info_fragment_layout, container, false);

        //First and foremost, if skip has been pressed then SKIP to home. Remember, Skipping here
        //would imply, "NOT A TUTOR YET" and essential details must be entered later
        Button skipButton = (Button) root.findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //TODO: Create account with available info. and go to the home page of the newly created account
                //Okay. So, get the passed basic profile info. from the generic learner's account creation activity
                GenericLearnerProfileParcelable genericLearnerProfile = getActivity().getIntent().getExtras()
                        .getParcelable(GenericLearnerNewAccountFragment.GENERIC_PROFILE);
                //Now, create a normal learner's account(I mean tutor's). Create a new tutor profile, make the education
                //and occupation as null and store "this" object instead of the generic one

            }
        });

        //Okay, time to get the input from UI(All this could go to sh*t if SKIP is pressed)
        //First off the educational qualification
        //High School Institution(School) name
        EditText highSchoolInstitutionName = (EditText)root.findViewById(R.id.institution_high_school);
        //High School Duration
        EditText yearsHighSchool = (EditText) root.findViewById(R.id.years_high_school);
        EditText monthsHighSchool = (EditText) root.findViewById(R.id.months_high_school);
        EditText daysHighSchool = (EditText) root.findViewById(R.id.days_high_school);
        //Year of passing: High School
        EditText yearOfPassingHighSchool = (EditText) root.findViewById(R.id.year_of_passing_high_school);

        //Senior Secondary Institution(School) name
        EditText seniorSecInstitutionName = (EditText)root.findViewById(R.id.institution_senior_sec);
        //Senior Sec. Duration
        EditText yearsSeniorSec = (EditText) root.findViewById(R.id.years_senior_sec);
        EditText monthsSeniorSec = (EditText) root.findViewById(R.id.months_senior_sec);
        EditText daysSeniorSec = (EditText) root.findViewById(R.id.days_senior_sec);
        //Year of passing: Senior Sec.
        EditText yearOfPassingSeniorSec = (EditText) root.findViewById(R.id.year_of_passing_senior_sec);

        return root;
    }
}
class DesignationMultiAutoCompleteTextView extends AppCompatMultiAutoCompleteTextView {

    public DesignationMultiAutoCompleteTextView(Context context, AttributeSet attrs){
        super(context, attrs);
    }
}