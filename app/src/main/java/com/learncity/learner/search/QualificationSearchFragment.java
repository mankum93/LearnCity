package com.learncity.learner.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.learncity.learncity.R;

/**
 * Created by DJ on 10/16/2016.
 */
public class QualificationSearchFragment extends Fragment {

    private AppCompatMultiAutoCompleteTextView customMultiAutoCompleteTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_qualification_search, container, false);

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

class QualificationSearchAdapter extends ArrayAdapter<String> {

    public QualificationSearchAdapter(Context context, int listLayoutId, String[] qualificationNames) {
        super(context, listLayoutId, qualificationNames);
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
            recycleView = inflater.inflate(R.layout.search_by_qualification_list_item_1, parent, false);
            viewHolder.qualificationNameView = (TextView) recycleView.findViewById(R.id.qualification_search_list_item_text_view);
            recycleView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) recycleView.getTag();
        }
        viewHolder.qualificationNameView.setText(subjectName);

        return recycleView;
    }

    //Purpose of the class: To cache the views. Basically, it shall hold the references to the child views for a root view.
    //All that's needed is to refresh these child views and we are good!
    private static class ViewHolder {
        TextView qualificationNameView;
        //TODO: Insert a subject icon/image with the text view
    }
}
class QualificationMultiAutoCompleteTextView extends AppCompatMultiAutoCompleteTextView{

    public QualificationMultiAutoCompleteTextView(Context context, AttributeSet attrs){
        super(context, attrs);
    }
}