package com.learncity.util.ver1;

/**
 * Created by DJ on 2/14/2017.
 */

public abstract class BaseAccountCreationTaskVer1 implements AccountCreationTaskVer1 {

    //Public access because of the involvement of implementor in defining the Task as well as the
    //associated accountCreationTaskListener; Getters and Setters provided as additional convenience methods
    /**Listener to provide a window to the Task execution states. */
    public AccountCreationTaskListener accountCreationTaskListener;

    //Package-private access intentionally chosen for the requirement of AccountManager ONLY
    /**States indicating the execution of the current AC creation task */
    static final int BEFORE_TASK_EXECUTION_START = 0x0025;
    static final int TASK_EXECUTION_STARTED = 0x0026;
    static final int TASK_EXECUTION_FAILED = 0x0027;
    static final int TASK_EXECUTION_COMPLETED = 0x0028;

    //Package-private access intentionally chosen for the requirement of AccountManager ONLY
    int accountCreationTaskState = BEFORE_TASK_EXECUTION_START;

    @Override
    public void initializeTask(){
        //Optional default implementation
    }

    @Override
    public void performCleanup(){
        //Optional default implementation
    }

    public final void setAccountCreationTaskListener(AccountCreationTaskListener listener){
        this.accountCreationTaskListener = listener;
    }

    public final AccountCreationTaskListener getAccountCreationTaskListener() {
        return accountCreationTaskListener;
    }
}
