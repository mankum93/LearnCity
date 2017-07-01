package com.learncity.learner.search.ver0;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learncity.learncity.R;
import com.learncity.learner.search.QualificationMultiAutoCompleteTextView;
import com.learncity.learner.search.QualificationSearchAdapter;

/**
 * Created by DJ on 10/16/2016.
 */
public class QualificationSearchFragment extends Fragment {

    private AppCompatMultiAutoCompleteTextView customMultiAutoCompleteTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.layout_qualification_search, container, false);

        customMultiAutoCompleteTextView = (QualificationMultiAutoCompleteTextView)root.findViewById(R.id.qualification_multi_auto_complete_view);

        //Initialize the adapter with dummy data and set it up
        customMultiAutoCompleteTextView.setAdapter(new QualificationSearchAdapter(getActivity(),
                R.layout.search_by_qualification_list_item_1,
                getResources().getStringArray(R.array.type_of_tutor)));

        /*Thought: After a subject is clicked/selected, it should be stored somewhere until the user presses the Search Button.
        * This "somewhere" has to be account for combination of search parameters from various search interfaces in the same
        * activity.*/

        //BEWARE!: Don't forget to set the Tokenizer. The suggestions won't show without it.
        customMultiAutoCompleteTextView.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());
        return root;
    }

    public AppCompatMultiAutoCompleteTextView getCustomMultiAutoCompleteTextView() {
        return customMultiAutoCompleteTextView;
    }
}

