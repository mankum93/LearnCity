package com.learncity.generic.learner.account.create.ver0;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.learncity.util.ver1.AccountCreationManagerVer1;
import com.learncity.util.ver1.BaseAccountCreationTaskVer1;

/**
 * Created by DJ on 2/16/2017.
 */

public class AccountCreationService {

    public static final int NOTIFY_UI_AUTO = AccountCreationManagerVer1.NOTIFY_UI_AUTO;
    public static final int NOTIFY_UI_CUSTOM = AccountCreationManagerVer1.NOTIFY_UI_CUSTOM;
    public static final int NOTIFY_UI_DEFAULT = AccountCreationManagerVer1.NOTIFY_UI_DEFAULT;

    private AccountCreationManagerVer1 accountCreationManager;
    private Context context;

    private static AccountCreationService accountCreationService;

    public void refreshContext(Context context){
        //Refresh the context
        if(this.context != context){
            this.context = context;
            this.accountCreationManager = getAccountCreationManager(context);
        }
    }

    private AccountCreationService(Context context) {
        this.context = context;
        //Initialize the AccountCreationManager
        accountCreationManager = getAccountCreationManager(context);
    }

    public static AccountCreationService getAccountCreationService(@NonNull Context context){

        if(accountCreationService == null){
            return accountCreationService = new AccountCreationService(context);
        }
        else{
            //Refresh the context
            if(accountCreationService.context != context){
                accountCreationService.context = context;
                accountCreationService.accountCreationManager = getAccountCreationManager(context);
            }
        }
        return accountCreationService;
    }


    public static AccountCreationManagerVer1 getAccountCreationManager(@NonNull Context context) {
        return AccountCreationManagerVer1.getAccountCreationManager(context);
    }

    public void queueTasks(BaseAccountCreationTaskVer1... tasks) {
        accountCreationManager.queueTasks(tasks);
    }

    public void queueTask(BaseAccountCreationTaskVer1 task) {
        accountCreationManager.queueTask(task);
    }

    /**
     * @param accountCreationListener Set this listener to listen to overall Account creation process. That includes a callback after all the
     * tasks have finished successfully; a callback in case a task fails any is intentionally made to cancel by the caller
     * and a callback for performing any activity before the start of AC creation. NOTE: This activity shall be performed in
     * a background thread
     * @param flagUI This flag determines if you want to use the provided Retry/Progress dialogs UI or want to provide
     * your own*/
    public void setAccountCreationListener(AccountCreationManagerVer1.AccountCreationListener accountCreationListener, int flagUI) {
        accountCreationManager.setAccountCreationListener(accountCreationListener, flagUI);
    }

    /**This method initiates the processing of Account creation tasks
     * @param tasks The tasks that are expected to be an AC creation tasks*/
    public void startAccountCreation(BaseAccountCreationTaskVer1... tasks) {
        accountCreationManager.startAccountCreation(tasks);
    }

    /**This method can be invoked in case the current task in execution has failed. Users need not call ths method
     * in case of NOTIFY_UI_AUTO mode because the Retry and Progress dialogs are internally handled in this mode
     * and Account creation automatically retries as per the user's response to those dialogs.
     *
     * Call this method ONLY if you are using the NOTIFY_UI_CUSTOM mode; Calling this method in NOTIFY_UI_AUTO mode
     * has NO EFFECT*/
    public void retryOnFailedAccountCreation() {
        accountCreationManager.retryOnFailedAccountCreation();
    }

    /**This method can be invoked in case the current task in execution has failed. Users need not call ths method
     * in case of NOTIFY_UI_AUTO mode because the Retry and Progress dialogs are internally handled in this mode
     * and Account creation automatically retries as per the user's response to those dialogs.
     *
     * Call this method ONLY if you are using the NOTIFY_UI_CUSTOM mode; Calling this method in NOTIFY_UI_AUTO mode
     * has NO EFFECT*/
    public void cancelOnFailedAccountCreation() {
        accountCreationManager.cancelOnFailedAccountCreation();
    }

    public void setAccountCreationProgressDialog(ProgressDialog accountCreationProgressDialog) {
        accountCreationManager.setAccountCreationProgressDialog(accountCreationProgressDialog);
    }

    public void setAlertDialogACCreationRetry(AlertDialog alertDialogACCreationRetry) {
        accountCreationManager.setAlertDialogACCreationRetry(alertDialogACCreationRetry);
    }

    /**This method asks the AccountCreationManager to finish up with the tasks currently enqueued and accept no new tasks further*/
    public void finishUp() {
        accountCreationManager.finishUp();
    }

}
