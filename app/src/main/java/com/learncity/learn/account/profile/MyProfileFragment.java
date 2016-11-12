package com.learncity.learn.account.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learncity.learncity.R;

/**
 * Created by DJ on 10/22/2016.
 */

public class MyProfileFragment extends Fragment {

    public static MyProfileFragment newInstance(){
        return new MyProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.myprofile_fragment_layout, container, false);

        //Populate the view with profile data now.

        return root;
    }
}
