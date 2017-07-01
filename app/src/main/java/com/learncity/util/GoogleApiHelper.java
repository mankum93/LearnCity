package com.learncity.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by DJ on 7/1/2017.
 */

public final class GoogleApiHelper {

    public static final String STATE_RESOLVING_ERROR = "resolving_error";
    // Request code to use when launching the resolution activity
    public static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    public static final String DIALOG_ERROR = "dialog_error";

    /* A fragment to display an error dialog */
    public static abstract class ErrorDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }
    }

    public static class ErrorDialogFragmentWithCallback extends ErrorDialogFragment{

        private OnDialogDismissedListener onDialogDismissedListener;

        @Override
        public void onDismiss(DialogInterface dialog) {
            if(this.onDialogDismissedListener != null){
                this.onDialogDismissedListener.onDialogDismissed();
            }
        }

        public void setOnDialogDismissedListener(OnDialogDismissedListener onDialogDismissedListener) {
            this.onDialogDismissedListener = onDialogDismissedListener;
        }

        public void removeOnDialogDismissedListener() {
            this.onDialogDismissedListener = null;
        }
    }

    public static interface OnDialogDismissedListener{

        void onDialogDismissed();
    }
}
