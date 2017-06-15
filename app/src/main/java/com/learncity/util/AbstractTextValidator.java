package com.learncity.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Created by DJ on 5/22/2017.
 */

public abstract class AbstractTextValidator implements TextWatcher {

    private static final String TAG_LOG = "AbstractTextValidator";

    private final View textView;

    public AbstractTextValidator(View view) {
        this.textView = view;
    }

    public abstract void validate(View view, String text);

    @Override
    final public void afterTextChanged(Editable s) {
        String text = s.toString();
        validate(textView, text);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
    
}