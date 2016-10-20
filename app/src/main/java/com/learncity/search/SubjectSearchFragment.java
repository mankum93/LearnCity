package com.learncity.search;

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

/**
 * Created by DJ on 10/16/2016.
 */
public class SubjectSearchFragment extends Fragment {

    //Dummy data source for the adapter
    String[] subjects = new String[] {
            "Physics", "Chemistry",
            "Mathematics", "Computer Science",
            "Biology", "Sanskrit",
            "Physical Education", "Social Science",
            "Economics", "Humanities",
            "Arts", "English",
            "History", "Political Science",
            "Hindi", "French",
            "German", "Japanese"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.subject_search_fragment_layout, container, false);

        AppCompatMultiAutoCompleteTextView customMultiAutoCompleteTextView = (SubjectMultiAutoCompleteTextView)root.findViewById(R.id.subject_multi_auto_complete_view);

        //Initialize the adapter with dummy data and set it up
        customMultiAutoCompleteTextView.setAdapter(new SubjectSearchAdapter(getActivity(), R.layout.search_by_subject_list_item_1, subjects));

        /*Thought: After a subject is clicked/selected, it should be stored somewhere until the user presses the Search Button.
        * This "somewhere" has to be account for combination of search parameters from various search interfaces in the same
        * activity.*/
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String subjectName = (String)parent.getAdapter().getItem(position);
                //TODO: Stash this subject name at an appropriate place for later use.
            }
        };
        customMultiAutoCompleteTextView.setOnItemClickListener(listener);
        //BEWARE!: Don't forget to set the Tokenizer. The suggestions won't show without it.
        customMultiAutoCompleteTextView.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());
        return root;
    }

}

class SubjectSearchAdapter extends ArrayAdapter<String>{

    public SubjectSearchAdapter(Context context, int listLayoutId, String[] subjectNames) {
        super(context, listLayoutId, subjectNames);
    }
    @Override
    public View getView(int position, View recycleView, ViewGroup parent) {
        //Get the subject for this position
        String subjectName = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (recycleView == null) {
            //There is no view to recycle so we create a brand new one
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            recycleView = inflater.inflate(R.layout.search_by_subject_list_item_1, parent, false);
            viewHolder.subjectNameView = (TextView) recycleView.findViewById(R.id.subject_search_list_item_text_view);
            recycleView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) recycleView.getTag();
        }
        viewHolder.subjectNameView.setText(subjectName);

        return recycleView;
    }

    //Purpose of the class: To cache the views. Basically, it shall hold the references to the child views for a root view.
    //All that's needed is to refresh these child views and we are good!
    private static class ViewHolder {
        TextView subjectNameView;
        //TODO: Insert a subject icon/image with the text view
    }
}