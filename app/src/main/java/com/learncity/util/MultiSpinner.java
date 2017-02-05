package com.learncity.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SpinnerAdapter;

import com.learncity.learncity.R;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by DJ on 2/2/2017.
 */

public class MultiSpinner<T> extends com.thomashaertel.widget.MultiSpinner {

    private int selectedItemsCount = 0;
    private List<T> selectedItemsList;
    private T[] selectedItemsArray;
    private Class<T> type;

    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context context, AttributeSet attr) {
        this(context, attr, R.attr.spinnerStyle);
    }

    public MultiSpinner(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
    }

    public int getSelectedItemsCount(){
        boolean selectedItems[] = getSelected();

        for(boolean b: selectedItems){
            if(b == true){
                selectedItemsCount++;
            }
        }
        return selectedItemsCount;
    }

    public List<T> getSelectedItemsList(Class<T> type){
        return selectedItemsList = Arrays.asList(getSelectedItemsArray(type));
    }

    @SuppressWarnings({"unchecked"})
    public T[] getSelectedItemsArray(Class<T> type){

        //Without Adapter, there would be no source of data available, would there?
        if(getAdapter() == null){
            throw new RuntimeException("No data to retrieve", new RuntimeException("Adapter has not been set"));
        }
        SpinnerAdapter adapter = getAdapter();
        boolean selectedItems[] = getSelected();

        this.type = type;
        selectedItemsArray = (T[])Array.newInstance(type, selectedItems.length);

        for(int i=0; i<selectedItems.length; i++){
            if(selectedItems[i] == true){
                selectedItemsArray[i] = (T)adapter.getItem(i);
            }
        }
        return selectedItemsArray;
    }

    public void setInitialDisplayText(String initialDisplayText){
        //Initialization of the boolean state array depends on the adapter being set or not
        if(getAdapter() == null){
            throw new RuntimeException("Initial state boolean array is uninitialized", new RuntimeException("Adapter has not been set"));
        }
        boolean initialUntouchedState[] = getSelected();
        setDefaultText(initialDisplayText);
        setSelected(initialUntouchedState);
    }

}
