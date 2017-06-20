package com.learncity.learner.search.ver0;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.learncity.learncity.R;
import com.learncity.learner.search.SubjectMultiAutoCompleteTextView;
import com.learncity.learner.search.SubjectSearchAdapter;

/**
 * Created by DJ on 10/16/2016.
 */
public class SubjectSearchFragment extends Fragment {


    private AppCompatMultiAutoCompleteTextView customMultiAutoCompleteTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_subject_search, container, false);

        customMultiAutoCompleteTextView = (SubjectMultiAutoCompleteTextView)root.findViewById(R.id.subject_multi_auto_complete_view);

        //Initialize the adapter with dummy data and set it up
        customMultiAutoCompleteTextView.setAdapter(new SubjectSearchAdapter(getActivity(),
                R.layout.search_by_subject_list_item_1,
                getResources().getStringArray(R.array.list_of_disciplines)));

        /*Thought: After a subject is clicked/selected, it should be stored somewhere until the user presses the Search Button.
        * This "somewhere" has to account for combination of search parameters from various search interfaces in the same
        * activity.*/

        //BEWARE!: Don't forget to set the Tokenizer. The suggestions won't show without it.
        customMultiAutoCompleteTextView.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());
        return root;
    }

    public AppCompatMultiAutoCompleteTextView getCustomMultiAutoCompleteTextView() {
        return customMultiAutoCompleteTextView;
    }
}

