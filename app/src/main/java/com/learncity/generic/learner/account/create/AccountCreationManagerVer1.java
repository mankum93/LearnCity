package com.learncity.generic.learner.account.create;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.learncity.generic.learner.account.create.ver0.SQLiteAccountCreationTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.learncity.generic.learner.account.create.BaseAccountCreationTask.TASK_EXECUTION_COMPLETED;
import static com.learncity.generic.learner.account.create.BaseAccountCreationTask.TASK_EXECUTION_FAILED;
import static com.learncity.generic.learner.account.create.BaseAccountCreationTask.TASK_EXECUTION_STARTED;

/**
 * Created by DJ on 2/5/2017.
 */

public class AccountCreationManagerVer1 {

    private static final String TAG = "AccountCreationManager1";

    /**Debug Mode has been enable default for the developer. It allows for the developer to test
     * rigorously for any invalid condition/behavior. Developer is encouraged to write their test code internally
     * within Debug mode to ensure robustness of code.*/
    private final boolean DEBUG_MODE = false;

    public static final int NOTIFY_UI_AUTO = 0x0010;
    public static final int NOTIFY_UI_CUSTOM = 0x0011;
    public static final int NOTIFY_UI_DEFAULT = NOTIFY_UI_AUTO;

    /**This flag indicates if user wants to implement their own dialogs UI for AC creation checkpoints or
     * prefer the default implementation. List of valid UI flags:
     * {NOTIFY_UI_AUTO, NOTIFY_UI_CUSTOM}
     * It defaults to the Auto mode
     * */
    private int accountCreationUIFlag = NOTIFY_UI_DEFAULT;

    /**States indicating the Global AC creation*/
    private static final int BEFORE_ACCOUNT_CREATION_START = 0x0020;
    private static final int ACCOUNT_CREATION_STARTED = 0x0021;
    private static final int ACCOUNT_CREATION_FAILED = 0x0022;
    private static final int ACCOUNT_CREATION_COMPLETED = 0x0023;

    private int accountCreationState = BEFORE_ACCOUNT_CREATION_START;

    private static AccountCreationManagerVer1 accountCreationManager;

    private Context context;

    private AccountCreationListener accountCreationListener;

    private AlertDialog alertDialogACCreationRetry;

    private ProgressDialog accountCreationProgressDialog;

    private final Object lockForRetryInternal = new Object();
    private boolean informedAboutRetryInternal = false;

    private Handler mUIHandler;

    private boolean shouldAccountCreationBeRetried;

    /*Caution: Because the ConcurrentLinkedQueue doesn't permit null elements, the logic is written considering
    * that. Changing this implementation would probably have implications*/
    private ConcurrentLinkedQueue<BaseAccountCreationTask> tasksQueue = new ConcurrentLinkedQueue<BaseAccountCreationTask>();

    private BaseAccountCreationTask currentTaskInExecution;

    private AccountCreationWorkerThread workerThread;

    private boolean finishUp = false;
    private boolean shouldFinishUp = false;

    private boolean shouldWaitForNewTasks = true;
    private final Object lockForNewTasks = new Object();

    private boolean informedAboutRetryTaskExternal = false;
    private final Object lockForRetryTaskExternal = new Object();

    private boolean readyForFinishUp = false;
    private final Object lockForFinishUp = new Object();

    private Thread tasksProcessor;

    private volatile int currentTasksPendingExecution;


    private AccountCreationManagerVer1(@NonNull Context context){

        //TODO: Insert checks for the flag
        this.context = context;

        if(mUIHandler == null){
            mUIHandler = new Handler(context.getMainLooper());
        }
        workerThread = new AccountCreationWorkerThread("AccountCreationManager");

    }

    /*Its quite possible that this method is called more than once. More so, its quite possible
     *that there may be 2 different contexts involved. Even though we are returning the same instance,
     * we must update the instance with the given context*/
    public static AccountCreationManagerVer1 getAccountCreationManager(@NonNull Context context){

        if(accountCreationManager == null){
            return accountCreationManager = new AccountCreationManagerVer1(context);
        }
        else{
            //Refresh the context
            if(accountCreationManager.context != context){
                accountCreationManager.context = context;
            }
        }
        return accountCreationManager;
    }

    /**This method initiates the processing of Account creation tasks*/
    //Has to be called from main thread or some other thread only, not the queue processor thread
    public void startAccountCreation(BaseAccountCreationTask... tasks){

        //If this is the initial call for AC creation, then only we shall proceed straight
        //Otherwise, queue the new tasks
        if(accountCreationState != BEFORE_ACCOUNT_CREATION_START){
            queueTasks(tasks);
            //Return here because this has been called from main thread the control to this method
            //shall be passed the other way - by waking it up from queueTasks() method
            return;
        }
        //Update the pending tasks count
        synchronized (this){
            currentTasksPendingExecution += tasks.length;
        }
        //Set the initial tasks
        tasksQueue.addAll(Arrays.asList(tasks));

        //Global AC creation state
        accountCreationState = ACCOUNT_CREATION_STARTED;

        //Start the progress dialog here
        showACCreationProgressDialog();

        tasksProcessor = new Thread(new Runnable() {
            @Override
            public void run() {
                //Local var for task management
                BaseAccountCreationTask task;

                //Register the bus
                if(!EventBus.getDefault().isRegistered(AccountCreationManagerVer1.this)){
                    Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Registering AccountManager instance with EventBus..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    EventBus.getDefault().register(AccountCreationManagerVer1.this);
                }

                //Before starting the AC creation process, call the callback for any pre-task if there is one
                if(accountCreationListener != null){
                    Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onPreAccountCreation()..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    accountCreationListener.onPreAccountCreation();
                }

                //First task IF its not null or empty
                task = tasksQueue.poll();

                //Retrieve the task from the queue
                while(!finishUp){
                    //task being null implies empty queue
                    if(task == null){
                        //Indication has been given already
                        if(shouldFinishUp){
                            while(!readyForFinishUp){
                                //Wait for it
                                synchronized(lockForFinishUp){
                                    try{
                                        lockForFinishUp.wait();
                                    }
                                    catch(InterruptedException ie){
                                        //Think of something
                                    }

                                }
                            }
                            //There are no more tasks to be processed ATM and we have been ordered to finish up as well
                            finishUp = true;
                            break;
                        }
                        //Wait till further tasks are furnished
                        while(shouldWaitForNewTasks){
                            synchronized (lockForNewTasks){
                                try{
                                    lockForNewTasks.wait();
                                }catch(InterruptedException ie){
                                    //Think of something
                                }
                            }
                        }
                        shouldWaitForNewTasks = true;
                    }
                    if(finishUp == true){
                        continue;
                    }
                    //Process the task
                    Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Starting the processing of current task: "+ task +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    performAccountCreation(task);
                    //Get new task
                    task = tasksQueue.poll();
                }
                //Should finish up now
                finishUpAccountCreation();
            }
        });
        tasksProcessor.start();
    }

    private void performAccountCreation(final BaseAccountCreationTask task){

        if(task == null){
            throw new NullPointerException("Can't process a null task!");
        }
        //Update the AC creation states for the task
        task.accountCreationTaskState = TASK_EXECUTION_STARTED;

        //Get the worker thread to traverse the tasks and enqueue execution
        //Initialize the worker thread
        if(workerThread == null){
            workerThread = new AccountCreationWorkerThread("AccountCreationManager");
        }
        if(!workerThread.isAlive()){
            Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Starting worker thread to initiate the AC creation process..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            workerThread.start();
        }
        workerThread.prepareHandler();

        workerThread.execute(new Runnable() {
            @Override
            public void run() {

                int taskStatusCode;

                currentTaskInExecution = task;

                //Start the AC creation process i.e, call the methods for the given task
                task.initializeTask();
                //Any exception if it forms relevance is expected to be handled by the client in the corresponding task
                //Basis this assumption, the returned AC creation code is being directly looked up for
                taskStatusCode = task.performAccountCreation();

                if(taskStatusCode == ACCOUNT_CREATION_COMPLETED){
                    Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Task successfully executed!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Update the AC creation state
                    task.accountCreationTaskState = TASK_EXECUTION_COMPLETED;

                    //As soon as the AC gets created, make sure to execute client's code
                    if(task.accountCreationTaskListener != null){
                        Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Calling AccountCreationTaskListener.onAccountCreated()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        task.accountCreationTaskListener.onAccountCreated();
                    }
                    Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Cleaning up the completed task; Calling BaseAccountCreationTask.onPerformCleanup()" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Perform cleanup on the task
                    task.performCleanup();

                    //Update the pending tasks count; Check if we have been asked to finish up
                    synchronized (AccountCreationManagerVer1.this){
                        currentTasksPendingExecution--;
                    }
                    if(shouldFinishUp && currentTasksPendingExecution == 0){
                        finishUp = true;
                        //Notify up the waiting thread
                        if(tasksProcessor.getState() == Thread.State.WAITING){
                            readyForFinishUp = true;
                            synchronized (lockForFinishUp){
                                lockForFinishUp.notify();
                            }
                        }
                    }

                }
                else if(taskStatusCode == ACCOUNT_CREATION_FAILED){
                    Log.e(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Task execution failed!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Update the AC creation state
                    task.accountCreationTaskState = TASK_EXECUTION_FAILED;
                    Thread t;
                    /*The accountCreationTaskListener callback must be called in a separate thread because
                    * further up just after this we have to wait ASAP be it for the internal retry dialog
                    * or the external one(if the user provides one - in which case the user shall inform us about it
                    * and we shall resume then or if the user asks us to wrap it up here then be it)*/
                    if(task.accountCreationTaskListener != null){
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreationFailed()..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                task.accountCreationTaskListener.onAccountCreationFailed();
                            }
                        });
                        t.start();
                    }
                    switch(accountCreationUIFlag){
                        case NOTIFY_UI_AUTO:
                            Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Requesting dismissal of AC creation progress Dialog..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Dismiss the already started AC creation progress dialog
                            dismissACCreationProgressDialog();
                            Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Requesting show of Retry Dialog after failed AC creation..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Have to show a default retry dialog
                            showRetryDialog();
                            //Have to wait on the main thread for the response
                            Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Trying to acquire the lockForRetryInternal lock..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            synchronized(lockForRetryInternal){
                                Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: lockForRetryInternal lock acquired." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                while(!informedAboutRetryInternal){
                                    try{
                                        Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Suspending this thread until notified by user's response to the retry dialog..." +
                                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                                        lockForRetryInternal.wait();
                                    }
                                    catch(InterruptedException ie){
                                        //TODO: Cancelling the object creation process that is going on currently and showing a retry
                                    }
                                }
                                Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Resuming this thread after being notified by user's response to the retry dialog..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                            }
                            break;
                        case NOTIFY_UI_CUSTOM:
                            //The caller is expected to implement their own dialog
                            /*Since the AC creation has been failed, I am gonna wait for 10 seconds
                             * before I terminate here*/
                            Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Trying to acquire the lockForRetryServerExternal lock..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            synchronized (lockForRetryTaskExternal){
                                Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: lockForRetryServerExternal lock acquired." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                while(!informedAboutRetryTaskExternal){
                                    try{
                                        Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Suspending this thread for MAX 10 sec. until notified by the user..." +
                                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                                        lockForRetryTaskExternal.wait(10*1000);
                                    }
                                    catch(InterruptedException ie){
                                        //TODO: Cancelling the object creation process that is going on currently and showing a retry
                                    }
                                    //Safety: In case 10 sec have passed and no reply - Get out of here forcefully
                                    informedAboutRetryTaskExternal = true;
                                }
                                Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Resuming this thread after being notified by user..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                            }
                            break;
                    }
                    //Either case, time to take up further action
                    if(shouldAccountCreationBeRetried){
                        //Not resetting these boolean fields can cause a TSUNAMI of infinite requests to server
                        informedAboutRetryInternal = false;
                        informedAboutRetryTaskExternal = false;

                        if(accountCreationUIFlag == NOTIFY_UI_AUTO){
                            Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Requesting show of AC Creation Progress Dialog..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Start the AC Creation animation again
                            showACCreationProgressDialog();
                        }

                        Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Retrying the server AC creation..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        //Retry it
                        performAccountCreation(task);
                    }
                    else{
                        //Too bad - User wants to cancel the after failed AC creation attempt...

                        //Update the AC creation state
                        accountCreationState = ACCOUNT_CREATION_FAILED;

                        //Perform cleanup on the task
                        task.performCleanup();

                        if(accountCreationListener != null){
                            Log.d(TAG, "AccountCreationManager.performAccountCreation(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreationFailed()..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            accountCreationListener.onAccountCreationFailed();
                        }
                        //Ready to finish up
                        readyForFinishUp = true;
                        finishUp = true;

                        //Clear out the worker thread queue
                        workerThread.getHandlerInstance().removeCallbacksAndMessages(null);

                        //Notify up the waiting thread
                        if(tasksProcessor.getState() == Thread.State.WAITING){
                            synchronized (lockForFinishUp){
                                lockForFinishUp.notify();
                            }
                        }
                    }
                }
                else{
                    throw new InvalidFlagException("Invalid return code. Check the list of valid flags.");
                }

            }
        });

    }

    public void queueTask(BaseAccountCreationTask task){
        if(shouldFinishUp){
            throw new RuntimeException("You can't enqueue new tasks because you have already asked for a finish up");
        }
        if(task == null){
            //No use waking up the thread
            return;
        }
        //Update the pending tasks count
        synchronized (this){
            currentTasksPendingExecution++;
        }
        tasksQueue.add(task);
        //Wake up the thread if its waiting
        if(tasksProcessor.getState() == Thread.State.WAITING){
            shouldWaitForNewTasks = false;
            lockForNewTasks.notify();
        }
    }
    public void queueTasks(BaseAccountCreationTask... tasks){
        if(shouldFinishUp){
            throw new RuntimeException("You can't enqueue new tasks because you have already asked for a finish up");
        }
        if(tasks == null){
            //No use waking up the thread
            return;
        }
        //Update the pending tasks count
        synchronized (this){
            currentTasksPendingExecution += tasks.length;
        }
        tasksQueue.addAll(Arrays.asList(tasks));
        //Wake up the thread if its waiting
        if(tasksProcessor.getState() == Thread.State.WAITING){
            shouldWaitForNewTasks = false;
            lockForNewTasks.notify();
        }
    }

    /**This method asks the AccountCreationManager to finish up with the tasks currently enqueued and accept no new tasks further*/
    public void finishUp(){
        //The thread is probably waiting or in the middle of a task - Let it finish first then. Leave an indication here thus.
        shouldFinishUp = true;
        shouldWaitForNewTasks = false;
    }

    private void finishUpAccountCreation(){
        Log.d(TAG, "AccountCreationManager.finishUpAccountCreation(): " + "\n" + "MESSAGE: Requesting dismissal of the AC creation progress dialog..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        switch(accountCreationUIFlag){
            case NOTIFY_UI_AUTO:
                dismissACCreationProgressDialog();
                break;
            case NOTIFY_UI_CUSTOM:
                //The caller is expected to implement their own dialog
                break;
        }
        //At this point, we are sure that the AC creation process has truly completed.
        //Lets call the callback for that if there is one.
        if(accountCreationState != ACCOUNT_CREATION_FAILED){
            accountCreationState = ACCOUNT_CREATION_COMPLETED;
            if(accountCreationListener != null){
                Log.d(TAG, "AccountCreationManager.finishUpAccountCreation(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreated()..." +
                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                accountCreationListener.onAccountCreated();
            }
        }

        //Now, lets perform global cleanup
        performCleanup();
    }

    /**This method can be invoked in case the current task in execution has failed. Users need not call ths method
     * in case of NOTIFY_UI_AUTO mode because the Retry and Progress dialogs are internally handled in this mode
     * and Account creation automatically retries as per the user's response to those dialogs.
     *
     * Call this method ONLY if you are using the NOTIFY_UI_CUSTOM mode; Calling this method in NOTIFY_UI_AUTO mode
     * has NO EFFECT*/
    public void retryOnFailedAccountCreation(){

        Log.d(TAG, "AccountCreationManager.retryOnFailedAccountCreation(): " + "\n" + "MESSAGE: retryAccountCreation() called..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        //This method would only be valid call if AC creation has failed
        if(currentTaskInExecution.accountCreationTaskState != TASK_EXECUTION_FAILED){
            throw new RuntimeException("There is no failed task there to be retried");
        }

        //Even though its Auto, the person has still called this method. Nevermind, we shall ignore
        if(accountCreationUIFlag == NOTIFY_UI_AUTO){
            //Do nothing.
            return;
        }
        else{
            Log.d(TAG, "AccountCreationManager.retryOnFailedAccountCreation(): " + "\n" + "MESSAGE: RETRY button clicked..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            shouldAccountCreationBeRetried = true;

            //Retry the Dialog interface for Positive button
            alertDialogACCreationRetry.cancel();

            synchronized (lockForRetryTaskExternal){
                Log.d(TAG, "AccountCreationManager.retryOnFailedAccountCreation(): " + "\n" + "MESSAGE: notifying the waiting thread with the lock <lockForRetryTaskExternal>..." +
                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                informedAboutRetryTaskExternal = true;
                lockForRetryTaskExternal.notify();
            }
        }
    }

    /**This method can be invoked in case the current task in execution has failed. Users need not call ths method
     * in case of NOTIFY_UI_AUTO mode because the Retry and Progress dialogs are internally handled in this mode
     * and Account creation automatically retries as per the user's response to those dialogs.
     *
     * Call this method ONLY if you are using the NOTIFY_UI_CUSTOM mode; Calling this method in NOTIFY_UI_AUTO mode
     * has NO EFFECT*/
    public void cancelOnFailedAccountCreation(){

        Log.d(TAG, "AccountCreationManager.cancelOnFailedAccountCreation(): " + "\n" + "MESSAGE: retryAccountCreation() called..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        //This method would only be valid call if AC creation has failed
        if(currentTaskInExecution.accountCreationTaskState != TASK_EXECUTION_FAILED){
            throw new RuntimeException("There is no task to be cancelled");
        }

        //Even though its Auto, the person has still called this method. Nevermind, we shall ignore
        if(accountCreationUIFlag == NOTIFY_UI_AUTO){
            //Do nothing.
            return;
        }
        else{
            Log.d(TAG, "AccountCreationManager.cancelOnFailedAccountCreation(): " + "\n" + "MESSAGE: CANCEL button clicked..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            shouldAccountCreationBeRetried = false;

            //Cancel the Dialog interface for Negative button
            alertDialogACCreationRetry.cancel();

            synchronized (lockForRetryTaskExternal){
                Log.d(TAG, "AccountCreationManager.cancelOnFailedAccountCreation(): " + "\n" + "MESSAGE: notifying the waiting thread with the lock <lockForRetryTaskExternal>..." +
                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                informedAboutRetryTaskExternal = true;
                lockForRetryTaskExternal.notify();
            }
        }
    }

    private void performCleanup(){

        Log.d(TAG, "AccountCreationManager.performCleanup(): " + "\n" + "MESSAGE: Cleaning up globally..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        alertDialogACCreationRetry = null;

        if(workerThread != null){
            workerThread.quit();
            workerThread = null;
        }

        if(!EventBus.getDefault().isRegistered(this)){
            Log.d(TAG, "AccountCreationManager.performDefaultPostServerAccountCreationTasks(): " + "\n" + "MESSAGE: Unregistering the AccountCreationManager from EventBus..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            EventBus.getDefault().unregister(this);
        }

        tasksQueue = null;
        currentTaskInExecution = null;

        accountCreationListener = null;

        tasksProcessor = null;

        //NOTE: The cleanup of accountCreationProgressDialog and accountCreationManager instances are being scheduled in the main thread
        // because dismissal of the progress dialog might have not yet finished
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                accountCreationProgressDialog = null;
                mUIHandler = null;
                context = null;
                accountCreationManager = null;
            }
        });
    }
    /**Set this listener to listen to overall Account creation process. That includes a callback after all the
     * tasks have finished successfully; a callback in case a task fails any is intentionally made to cancel by the caller
     * and a callback for performing any activity before the start of AC creation. NOTE: This activity shall be performed in
     * a background thread*/
    public void setAccountCreationListener(AccountCreationListener accountCreationListener, int flagUI) {

        //If the account creation has started, you can't set the flag/accountCreationTaskListener now
        if(accountCreationState == ACCOUNT_CREATION_STARTED){
            throw new IllegalStateException("Account creation has started. You can't set the accountCreationTaskListener/flag in the middle");
        }
        checkUIFlagValidity(flagUI);
        //Since AC creation hasn't begun yet, user can set the flag and accountCreationTaskListener as many times as they want
        this.accountCreationUIFlag = flagUI;
        this.accountCreationListener = accountCreationListener;
    }

    public void setAlertDialogACCreationRetry(AlertDialog alertDialogACCreationRetry) {
        //If the account creation has started, you can't set the flag/accountCreationTaskListener now
        if(accountCreationState == ACCOUNT_CREATION_STARTED){
            throw new IllegalStateException("Account creation has started. You can't set the Alert Dialog in the middle");
        }
        this.alertDialogACCreationRetry = alertDialogACCreationRetry;

        //Set the default conifg.
        this.alertDialogACCreationRetry.setCancelable(true);
    }

    public void setAccountCreationProgressDialog(ProgressDialog accountCreationProgressDialog) {
        //If the account creation has started, you can't set the flag/accountCreationTaskListener now
        if(accountCreationState == ACCOUNT_CREATION_STARTED){
            throw new IllegalStateException("Account creation has started. You can't set the Progress Dialog in the middle");
        }
        this.accountCreationProgressDialog = accountCreationProgressDialog;

        //Set the default conifg.
        this.accountCreationProgressDialog.setIndeterminate(true);
        this.accountCreationProgressDialog.setCancelable(true);
    }

    /**Check if the UI flag belongs to the set of valid ones*/
    private void checkUIFlagValidity(int accountCreationUIFlag){
        switch(accountCreationUIFlag){
            case NOTIFY_UI_AUTO:
                //Pass
                break;
            case NOTIFY_UI_CUSTOM:
                //Pass
                break;
            default:
                //Fail
                throw new InvalidFlagException("Invalid Account UI flag. Check the list of valid flags.");
        }
    }

    private void showRetryDialog(){
        //If dialog already showing, no use invoking it again
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if(alertDialogACCreationRetry == null){
                    alertDialogACCreationRetry = new AlertDialog.Builder(context)
                            .setTitle("Account creation failed")
                            .setMessage("There was a problem connecting to the Network. Check your connection.")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "AccountCreationManager.showRetryDialog(): " + "\n" + "MESSAGE: RETRY button clicked..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());

                                    dialog.cancel();

                                    shouldAccountCreationBeRetried = true;

                                    //Inform the guy waiting to know the user's response
                                    EventBus.getDefault().post(shouldAccountCreationBeRetried);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "AccountCreationManager.showRetryDialog(): " + "\n" + "MESSAGE: CANCEL button clicked..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                    dialog.cancel();

                                    shouldAccountCreationBeRetried = false;
                                    //Indication that the Account creation has been unsuccessful and should be retried

                                    //Inform the guy waiting to know the user's response
                                    EventBus.getDefault().post(shouldAccountCreationBeRetried);
                                }
                            }).create();
                }
                if(!alertDialogACCreationRetry.isShowing()){
                    Log.d(TAG, "AccountCreationManager.showRetryDialog(): " + "\n" + "MESSAGE: Showing the AC Creation Retry dialog..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    alertDialogACCreationRetry.show();
                }
                else{
                    if(DEBUG_MODE){
                        throw new RuntimeException("Retry Dialog already showing - Check for redundant invocation by the second caller");
                    }
                }

            }
        });

    }

    private void showACCreationProgressDialog(){
        //If dialog already in progress, no use invoking it again
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if(accountCreationProgressDialog == null){
                    accountCreationProgressDialog = new ProgressDialog(context);
                    accountCreationProgressDialog.setIndeterminate(true);
                    accountCreationProgressDialog.setTitle("Creating Account");
                    accountCreationProgressDialog.setCancelable(true);
                }
                if(!accountCreationProgressDialog.isShowing()){
                    Log.d(TAG, "AccountCreationManager.showRetryDialog(): " + "\n" + "MESSAGE: Showing the AC creation progress dialog..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    accountCreationProgressDialog.show();
                }
                else{
                    if(DEBUG_MODE){
                        throw new RuntimeException("Dialog already in progress - Check for redundant invocation by the second caller");
                    }
                }
            }
        });
    }

    private void dismissACCreationProgressDialog(){
        //If dialog already dismissed, no use dismissing it again
        if(accountCreationProgressDialog.isShowing()){
            Log.d(TAG, "AccountCreationManager.showRetryDialog(): " + "\n" + "MESSAGE: Dismissing the AC creation progress dialog..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    accountCreationProgressDialog.dismiss();
                    accountCreationProgressDialog.cancel();
                }
            });
        }
        else{
            if(DEBUG_MODE){
                throw new RuntimeException("Dialog already been dismissed - Check for redundant dismissal by the second caller");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    private void shouldAccountCreationBeRetried(Boolean shouldAccountCreationBeRetried){
        //TODO: Remove the boolean argument in exchange of a specialized event. Secondly, the use of EventBus can be avoided or embraced in code like this
        informedAboutRetryInternal = true;
        synchronized(lockForRetryInternal){
            this.shouldAccountCreationBeRetried = shouldAccountCreationBeRetried;
            lockForRetryInternal.notifyAll();
        }
    }

    public static boolean isAccountAlreadyExistingOnThisDevice(Context context) {
        return SQLiteAccountCreationTask.isExistingUserAccount(context);
    }

    /**
     * This interface shall be implemented by anyone wanting to get a window into the AC creation states
     * and perform tasks deemed necessary by them at those points.
     */
    public interface AccountCreationListener {
        void onAccountCreated();
        void onAccountCreationFailed();
        void onPreAccountCreation();
    }

    class InvalidFlagException extends RuntimeException{
        public InvalidFlagException(String msg){
            super(msg);
        }
    }

}
