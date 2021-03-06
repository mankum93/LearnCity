package com.learncity.learner.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.learncity.learncity.R;

/**
 * Created by DJ on 6/15/2017.
 */
public class SubjectSearchAdapter extends ArrayAdapter<String> {

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
