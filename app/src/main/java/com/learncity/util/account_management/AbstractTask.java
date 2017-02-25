package com.learncity.util.account_management;


/**
 * Created by DJ on 2/19/2017.
 */

public abstract class AbstractTask<T> implements Task<T> {

    //Public access because of the involvement of implementor in defining the Task as well as the
    //associated accountCreationTaskListener; Getters and Setters provided as additional convenience methods
    /**Listener to provide a window to the Task execution states. */
    public TaskListener<T> taskListener;

    //Package-private access intentionally chosen for the requirement of AccountManager ONLY
    /**States indicating the execution of the current AC creation task */
    static final int BEFORE_TASK_EXECUTION_START = 0x0025;
    static final int TASK_EXECUTION_STARTED = 0x0026;
    static final int TASK_EXECUTION_FAILED = 0x0027;
    static final int TASK_EXECUTION_COMPLETED = 0x0028;

    //Package-private access intentionally chosen for the requirement of AccountManager ONLY
    int taskState = BEFORE_TASK_EXECUTION_START;


    /**
     * Override this method to perform any setup deemed necessary
     */
    @Override
    public void initializeTask() {
        //Default impl.
    }

    /**
     * Override this method to perform any cleanup activities with the task execution
     */
    @Override
    public void performCleanup() {
        //Default impl.
    }

    public final TaskListener<T> getTaskListener() {
        return taskListener;
    }

    public final void setTaskListener(TaskListener<T> taskListener) {
        this.taskListener = taskListener;
    }
}
