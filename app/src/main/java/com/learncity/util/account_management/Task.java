package com.learncity.util.account_management;

/**
 * Created by DJ on 2/6/2017.
 */

public interface Task {

    /**Task completion status indicators*/
    int TASK_FAILED = 0x0022;
    int TASK_COMPLETED = 0x0023;

    /**Override this method to perform any setup deemed necessary*/
    void initializeTask();

    /**Override this method to carry out the task.
     * @return Returns the task completion status out of the following codes:
     * {TASK_FAILED, TASK_COMPLETED}*/
    int performTask();

    /**Override this method to perform any cleanup activities with the task execution*/
    void performCleanup();


    interface TaskListener {
        void onTaskCompleted();
        void onTaskFailed();
    }
}
