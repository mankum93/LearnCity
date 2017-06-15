package com.learncity.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DJ on 5/22/2017.
 */

public abstract class AbstractTextValidatorDeprecated implements TextWatcher {

    private static final String TAG_LOG = "AbstractTextValidator";

    /**
     * Name tags
     */
    public static final String TAG_NAME = "TAG_NAME";
    public static final String TAG_FIRST_NAME = "TAG_FIRST_NAME";
    public static final String TAG_MIDDLE_NAME = "TAG_MIDDLE_NAME";
    public static final String TAG_LAST_NAME = "TAG_LAST_NAME";

    /**
     * Common credentials tags
     */
    public static final String TAG_PHONE_NO = "TAG_PHONE_NO";
    public static final String TAG_EMAIL = "TAG_EMAIL";
    public static final String TAG_PASSWORD = "TAG_PASSWORD";
    public static final String TAG_RETYPED_PASSWORD = "TAG_RETYPED_PASSWORD";

    /**
     * Address tags.
     */
    public static final String TAG_ADDRESS = "TAG_ADDRESS";
    public static final String TAG_ADDRESS_FIRST_LINE = "TAG_ADDRESS_FIRST_LINE";
    public static final String TAG_ADDRESS_SECOND_LINE = "TAG_ADDRESS_SECOND_LINE";
    public static final String TAG_CITY = "TAG_CITY";
    public static final String TAG_STATE = "TAG_STATE";
    public static final String TAG_COUNTY = "TAG_COUNTY";
    public static final String TAG_COUNTRY = "TAG_COUNTRY";
    public static final String TAG_POSTAL_CODE = "TAG_POSTAL_CODE";


    private Map<String, View> viewRecordsByTags;

    /**
     * Associate a View with a TAG. If there is a View already associated with
     * the provided TAG, it will be replaced with the provided View.
     *
     * @param TAG : The TAG(unique String ID) to associate with the View.
     * @param view : The view to be associated with the TAG.
     */
    public void addTagWithView(String TAG, View view){
        if(viewRecordsByTags == null){
            viewRecordsByTags = new HashMap<>();
        }
        viewRecordsByTags.put(TAG, view);
    }

    public View getViewByTag(String TAG){
        if(viewRecordsByTags == null){
            Log.w(TAG_LOG, "The TAG, " + TAG + " is not registered with any View.");
            return null;
        }
        else{
            return viewRecordsByTags.get(TAG);
        }
    }

    protected abstract void validate(String text);

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();
        validate(text);
    }
}
