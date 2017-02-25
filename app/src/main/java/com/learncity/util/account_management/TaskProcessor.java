package com.learncity.util.account_management;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.learncity.util.account_management.Task.TASK_COMPLETED;
import static com.learncity.util.account_management.Task.TASK_FAILED;

/**
 * Created by DJ on 2/5/2017.
 */

/**@author Manish Kumar Sharma
 *
 * A general purpose Task Processor which processes the tasks in a serial manner with mechanism
 * to ask for a Retry in case a task fails and show a ProgressDialog for the task execution progress. The
 * AlertDialog for Retry and ProgressDialog for progress can be custom provided too.
 * It also facilitates a window into the Overall Task processing of the queues tasks as well caller can set listener
 * on the individual tasks for control at the level of individual tasks.
 * It uses ConcurrentLinkedQueue to queue the tasks for processing so the addition of tasks is a thread safe process*/
public class TaskProcessor {

    private static final String TAG = "TaskProcessor";

    /**Debug Mode has been enable default for the developer. It allows for the developer to test
     * rigorously for any invalid condition/behavior. Developer is encouraged to write their test code internally
     * within Debug mode to ensure robustness of code.*/
    private final boolean DEBUG_MODE = false;

    public static final int NOTIFY_UI_AUTO = 0x0010;
    public static final int NOTIFY_UI_CUSTOM = 0x0011;
    public static final int NOTIFY_UI_DEFAULT = NOTIFY_UI_AUTO;

    /**This flag indicates if user wants to implement their own dialogs UI for task processing checkpoints or
     * prefer the default implementation. List of valid UI flags:
     * {NOTIFY_UI_AUTO, NOTIFY_UI_CUSTOM}
     * It defaults to the Auto mode
     * */
    private int notifyUIFlag = NOTIFY_UI_DEFAULT;

    /**States indicating the overall tasks processing*/
    private static final int BEFORE_OVERALL_TASKS_PROCESSING_START = 0x0020;
    private static final int OVERALL_TASKS_PROCESSING_STARTED = 0x0021;
    private static final int OVERALL_TASKS_PROCESSING_FAILED = 0x0022;
    private static final int OVERALL_TASKS_PROCESSING_COMPLETED = 0x0023;

    private int overallTaskProcessingState = BEFORE_OVERALL_TASKS_PROCESSING_START;

    //private static TaskProcessor taskProcessorInstance;

    private Context context;

    /**This listener provides callbacks for Overall task processing session*/
    private TaskProcessorListener taskProcessorListener;

    private AlertDialog taskProcessingRetryAlertDialog;

    private ProgressDialog taskProcessingProgressDialog;

    private final Object lockForRetryInternal = new Object();
    private boolean informedAboutRetryInternal = false;

    private Handler mUIHandler;

    /**As the name indicates; this is set after the user chooses to Retry or Cancel through the Retry Dialog*/
    private boolean shouldTaskProcessingBeRetried = false;

    /*Caution: Because the ConcurrentLinkedQueue doesn't permit null elements, the logic is written considering
    * that. Changing this implementation would probably have implications*/
    private ConcurrentLinkedQueue<AbstractTask> tasksQueue = new ConcurrentLinkedQueue<AbstractTask>();

    //To have a handle to the current task in execution
    private AbstractTask currentTaskInExecution;

    //Handler thread that drives the tasks queued
    private TaskProcessingWorkerThread workerThread;

    /**The flag used by the Task processor to check for confirmation to quit right at that point and
     * perform cleanup*/
    private boolean finishUp = false;

    /**Indicative that as of now finishUp() has been requested*/
    private boolean shouldFinishUp = false;

    /**Indicative that as of now shutDown() has been requested.*/
    private boolean shouldShutDown = false;

    private boolean shouldWaitForNewTasks = true;
    private final Object lockForNewTasks = new Object();

    private boolean informedAboutRetryTaskExternal = false;
    private final Object lockForRetryTaskExternal = new Object();

    private Thread tasksProcessor;

    /**Tells the user of TaskProcessor if the task processor is still valid for processing. This flag will be set
     * at the end - Ultimate failure and cancellation or ultimate success.*/
    private boolean isValid = true;
    private Result result;

    private void prepareForReuse(){
        overallTaskProcessingState = BEFORE_OVERALL_TASKS_PROCESSING_START;

        informedAboutRetryInternal = false;

        shouldTaskProcessingBeRetried = false;

        tasksQueue = new ConcurrentLinkedQueue<AbstractTask>();

        finishUp = false;
        shouldFinishUp = false;

        shouldWaitForNewTasks = true;

        informedAboutRetryTaskExternal = false;
    }

    public TaskProcessor(@NonNull Context context){

        this.context = context;

        if(mUIHandler == null){
            mUIHandler = new Handler(context.getMainLooper());
        }
        workerThread = new TaskProcessingWorkerThread("TaskProcessor");

    }

    //TODO: Ensure that context doesn't get refreshed while any UI is showing using previous context
    public void refreshContext(@NonNull Context context){
        if(context == null){
            throw new NullPointerException("Context cannot be null");
        }
        this.context = context;
    }

    /*Its quite possible that this method is called more than once. More so, its quite possible
     *that there may be 2 different contexts involved. Even though we are returning the same instance,
     * we must update the instance with the given context*/
    /*
    public static TaskProcessor getTaskProcessor(@NonNull Context context){

        if(taskProcessorInstance == null){
            return taskProcessorInstance = new TaskProcessor(context);
        }
        else{
            //Refresh the context
            if(taskProcessorInstance.context != context){
                taskProcessorInstance.context = context;
            }
        }
        return taskProcessorInstance;
    }
    */

    /**This method initiates the processing of tasks*/
    //Has to be called from main thread or some other thread only, not the queue processor thread
    public void startTasksProcessing(AbstractTask... tasks){

        //If the TaskProcessor has already been used and disposed, any fresh requests to the same instance
        //assume that the state set by other parties has remained state and they want the TaskProcessor
        //to act on the same state as last time. The parties are free to overwrite the state they have set before
        if(overallTaskProcessingState == OVERALL_TASKS_PROCESSING_COMPLETED
                || overallTaskProcessingState == OVERALL_TASKS_PROCESSING_FAILED){
            prepareForReuse();
        }

        //If this is the initial call for task processing, then only we shall proceed straight
        //Otherwise, queue the new tasks
        if(overallTaskProcessingState != BEFORE_OVERALL_TASKS_PROCESSING_START){
            queueTasks(tasks);
            //Return here because this has been called from main thread the control to this method
            //shall be passed the other way - by waking it up from queueTasks() method
            return;
        }
        //Set the initial tasks
        tasksQueue.addAll(Arrays.asList(tasks));

        //Overall Task processing state
        overallTaskProcessingState = OVERALL_TASKS_PROCESSING_STARTED;

        //Start the progress dialog here
        if(notifyUIFlag == NOTIFY_UI_AUTO){
            Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Requesting show of  Progress Dialog..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            //Start the task processing animation again
            showTaskProcessingProgressDialog();
        }

        tasksProcessor = new Thread(new Runnable() {
            @Override
            public void run() {
                //Local var for task management
                AbstractTask task;

                //Before starting the current task processing, call the callback for any pre-task if there is one
                if(taskProcessorListener != null){
                    Log.d(TAG, "TaskProcessor.startTasksProcessing(): " + "\n" + "MESSAGE: Calling TaskProcessorListener.onPreTaskProcessingStart()..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    taskProcessorListener.onPreTaskProcessingStart();
                }

                //Retrieve the task from the queue
                while(!finishUp){
                    //Get new task
                    task = tasksQueue.poll();
                    //task being null implies empty queue
                    if(task == null){
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
                        if(finishUp){
                            break;
                        }
                        if(shouldFinishUp){
                            continue;
                        }
                        //Notice this var is not set by the notifier for the "Wait for more tasks" wait because
                        //that would imply that the notifier is done with it once and for all and don't want to
                        //the processor to be take more requests further BUT even the caller can't be ensure about that
                        //so on being notified by the caller, we set it back to "true" to automatically enable it wait
                        //next time when the tasks are taken off the queue
                        shouldWaitForNewTasks = true;
                    }
                    //Process the task
                    Log.d(TAG, "TaskProcessor.startTasksProcessing(): " + "\n" + "MESSAGE: Starting the processing of current task: "+ task +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    performTaskProcessing(task);
                }
                //Should finish up now
                finishUpTaskProcessing();
            }
        });
        tasksProcessor.start();
    }

    private void performTaskProcessing(final AbstractTask task){

        if(task == null){
            throw new NullPointerException("Can't process a null task!");
        }
        //Update the state for the task
        task.taskState = AbstractTask.TASK_EXECUTION_STARTED;

        //Get the worker thread to traverse the tasks and enqueue execution

        //Initialize the worker thread
        if(workerThread == null){
            workerThread = new TaskProcessingWorkerThread("TaskProcessorWorkerThread");
        }
        if(!workerThread.isAlive()){
            Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Starting worker thread to initiate the AC creation process..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            workerThread.start();
        }
        workerThread.prepareHandler();

        workerThread.execute(new Runnable() {
            @Override
            public void run() {

                int taskExecutionStatusCode;

                currentTaskInExecution = task;

                //Start the task processing i.e, call the methods for the given task
                //Initialize the task - only once per task
                task.initializeTask();
                //Any exception if it forms relevance is expected to be handled by the client in the corresponding task
                //Basis this assumption, the returned AC creation code is being directly looked up for
                do{
                    //As soon as the task gets processed, make sure to execute client's code
                    if(task.taskListener != null){
                        Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Calling TaskListener.onPreTaskExecute()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        task.taskListener.onPreTaskExecute();
                    }
                    result = task.performTask();
                    taskExecutionStatusCode = result.getResultCode();

                    if(taskExecutionStatusCode == TASK_COMPLETED){
                        Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Task successfully executed!!!" +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        //Update the task processing state
                        task.taskState = AbstractTask.TASK_EXECUTION_COMPLETED;

                        //As soon as the task gets processed, make sure to execute client's code
                        if(task.taskListener != null){
                            Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Calling TaskListener.onTaskProcessingComplete()..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            task.taskListener.onTaskCompleted(result);
                        }
                        Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Cleaning up the completed task; Calling AbstractTask.onPerformCleanup()" +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        //Perform cleanup on the task
                        task.performCleanup();

                        //If this is the finishup task then wake up the thread that may be waiting for new tasks
                        if(finishUp){
                            //Notify up the waiting thread
                            if(tasksProcessor.getState() == Thread.State.WAITING){
                                synchronized (lockForNewTasks){
                                    lockForNewTasks.notify();
                                }
                            }
                        }

                    }
                    else if(taskExecutionStatusCode == TASK_FAILED){
                        Log.e(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Task execution failed!!!" +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        //Update the task processing state
                        task.taskState = AbstractTask.TASK_EXECUTION_FAILED;
                        Thread t;
                    /*The accountCreationTaskListener callback must be called in a separate thread because
                    * further up just after this we have to wait ASAP be it for the internal retry dialog
                    * or the external one(if the user provides one - in which case the user shall inform us about it
                    * and we shall resume then or if the user asks us to wrap it up here then be it)*/
                        if(task.taskListener != null){
                            t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Calling TaskProcessorListener.onTaskProcessingFailed()..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                    task.taskListener.onTaskFailed(result);
                                }
                            });
                            t.start();
                        }
                        switch(notifyUIFlag){
                            case NOTIFY_UI_AUTO:
                                Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Requesting dismissal of progress Dialog..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                //Dismiss the already started task processing progress dialog
                                dismissTaskProcessingProgressDialog();
                                //Have to wait on the main thread for the response
                                Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Trying to acquire the lockForRetryInternal lock..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                synchronized(lockForRetryInternal){
                                    Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Requesting show of Retry Dialog after failed task..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                    //Have to show a default retry dialog
                                    showRetryDialog();
                                    Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: lockForRetryInternal lock acquired." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                    while(!informedAboutRetryInternal){
                                        try{
                                            Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Suspending this thread until notified by user's response to the retry dialog..." +
                                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                                            lockForRetryInternal.wait();
                                        }
                                        catch(InterruptedException ie){
                                            //Think of something
                                        }
                                    }
                                    Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Resuming this thread after being notified by user's response to the retry dialog..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                }
                                break;
                            case NOTIFY_UI_CUSTOM:
                                //The caller is expected to implement their own dialog
                                Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Trying to acquire the lockForRetryServerExternal lock..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                synchronized (lockForRetryTaskExternal){
                                    Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: lockForRetryServerExternal lock acquired." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                    while(!informedAboutRetryTaskExternal){
                                        try{
                                            Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Suspending this thread for MAX 10 sec. until notified by the user..." +
                                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                                            lockForRetryTaskExternal.wait();
                                        }
                                        catch(InterruptedException ie){
                                            //Think of something
                                        }
                                        //Safety: In case 10 sec have passed and no reply - Get out of here forcefully
                                        informedAboutRetryTaskExternal = true;
                                    }
                                    Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Resuming this thread after being notified by user..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                }
                                break;
                        }
                        //Either case, time to take up further action
                        if(shouldTaskProcessingBeRetried){
                            //Not resetting these boolean fields can cause a TSUNAMI of infinite requests to server
                            informedAboutRetryInternal = false;
                            informedAboutRetryTaskExternal = false;

                            if(notifyUIFlag == NOTIFY_UI_AUTO){
                                Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Requesting show of  Progress Dialog..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                //Start the task processing animation again
                                showTaskProcessingProgressDialog();
                            }

                            Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Retrying the task..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Retry in the next iteration of the loop
                        }
                        else{
                        /*NOTE: THIS BLOCK REPRESENTS THE CANCELLATION OF FURTHER PROCESSING TASKS
                        AFTER FAILED EXECUTION OF SOME TASK*/

                            //Too bad - User wants to cancel the after failed task processing attempt...
                            //Update the task processing state
                            overallTaskProcessingState = OVERALL_TASKS_PROCESSING_FAILED;

                            //Perform cleanup on the task
                            task.performCleanup();

                            if(taskProcessorListener != null){
                                Log.d(TAG, "TaskProcessor.performTaskProcessing(): " + "\n" + "MESSAGE: Calling TaskProcessorListener.onTaskProcessingFailed()..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                taskProcessorListener.onTaskProcessingFailed();
                            }
                            //Ready to finish up
                            finishUp = true;

                            //Clear out the worker thread queue
                            workerThread.getHandlerInstance().removeCallbacksAndMessages(null);

                            //Notify up the waiting thread
                            if(tasksProcessor.getState() == Thread.State.WAITING){
                                synchronized (lockForNewTasks){
                                    lockForNewTasks.notify();
                                }
                            }
                        }
                    }
                    else{
                        throw new InvalidFlagException("Invalid return code. Check the list of valid flags.");
                    }

                }while(shouldTaskProcessingBeRetried);


            }
        });

    }

    public synchronized void queueTask(AbstractTask task){
        if(shouldFinishUp){
            throw new RuntimeException("You can't enqueue new tasks because you have already asked for a finish up");
        }
        if(task == null){
            //No use waking up the thread
            return;
        }
        tasksQueue.add(task);
        //Wake up the thread if its waiting
        if(tasksProcessor.getState() == Thread.State.WAITING){
            shouldWaitForNewTasks = false;
            synchronized (lockForNewTasks){
                lockForNewTasks.notify();
            }
        }
    }
    public synchronized void queueTasks(AbstractTask... tasks){
        if(shouldFinishUp){
            throw new RuntimeException("You can't enqueue new tasks because you have already asked for a finish up");
        }
        if(tasks == null){
            //No use waking up the thread
            return;
        }
        tasksQueue.addAll(Arrays.asList(tasks));
        //Wake up the thread if its waiting
        if(tasksProcessor.getState() == Thread.State.WAITING){
            shouldWaitForNewTasks = false;
            synchronized (lockForNewTasks){
                lockForNewTasks.notify();
            }
        }
    }

    /**This method asks the TaskProcessor to finish up with the tasks currently enqueued and accept no new tasks further.
     * The finishUp() methods don't reset the state variables to the original ones. This implies that even after
     * the TaskProcessor has been asked to finishUp() till the TaskProcessor instance stays alive, it is able
     * to reject fresh requests through these states*/
    public synchronized void finishUp(){
        if(shouldShutDown){
            return;
        }
        //The thread is probably waiting or in the middle of a task - Let it finish first then. Leave an indication here thus.
        shouldFinishUp = true;
        shouldWaitForNewTasks = false;

        //Lets queue a finishUp task that will executed only in case all the tasks  in the queueget successfully
        //get executed
        tasksQueue.add(new AbstractTask() {
            @Override
            public Result<Void> performTask() {
                finishUp = true;
                return Result.RESULT_SUCCESS;
            }
        });
        //Notify up the waiting thread
        if(tasksProcessor!= null && tasksProcessor.isAlive() && tasksProcessor.getState() == Thread.State.WAITING){
            synchronized (lockForNewTasks){
                lockForNewTasks.notify();
            }
        }
    }

    private void finishUpTaskProcessing(){
        Log.d(TAG, "TaskProcessor.finishUpTaskProcessing(): " + "\n" + "MESSAGE: Requesting dismissal of the progress dialog..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        switch(notifyUIFlag){
            case NOTIFY_UI_AUTO:
                dismissTaskProcessingProgressDialog();
                break;
            case NOTIFY_UI_CUSTOM:
                //The caller is expected to implement their own dialog
                break;
        }
        //At this point, we are sure that the task processing has truly completed.
        //Lets call the callback for that if there is one.
        if(overallTaskProcessingState != OVERALL_TASKS_PROCESSING_FAILED){
            overallTaskProcessingState = OVERALL_TASKS_PROCESSING_COMPLETED;
            if(taskProcessorListener != null){
                Log.d(TAG, "TaskProcessor.finishUpTaskProcessing(): " + "\n" + "MESSAGE: Calling TaskProcessorListener.onTaskProcessingComplete()..." +
                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                taskProcessorListener.onTaskProcessingComplete();
            }
        }

        //Now, lets perform global cleanup
        performCleanup();
    }

    /**This method can be invoked in case the current task in execution has failed. Users need not call ths method
     * in case of NOTIFY_UI_AUTO mode because the Retry and Progress dialogs are internally handled in this mode
     * and task automatically retries as per the user's response to those dialogs.
     *
     * Call this method ONLY if you are using the NOTIFY_UI_CUSTOM mode; Calling this method in NOTIFY_UI_AUTO mode
     * has NO EFFECT*/
    public synchronized void retryOnFailedTask(){

        Log.d(TAG, "TaskProcessor.retryOnFailedTask(): " + "\n" + "MESSAGE: retryOnFailedTask() called..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        //This method would only be valid call if AC creation has failed
        if(currentTaskInExecution.taskState != AbstractTask.TASK_EXECUTION_FAILED){
            throw new RuntimeException("There is no failed task there to be retried");
        }

        //Even though its Auto, the person has still called this method. Nevermind, we shall ignore
        if(notifyUIFlag == NOTIFY_UI_AUTO){
            //Do nothing.
        }
        else{
            Log.d(TAG, "TaskProcessor.retryOnFailedTask(): " + "\n" + "MESSAGE: RETRY button clicked..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            shouldTaskProcessingBeRetried = true;

            synchronized (lockForRetryTaskExternal){
                Log.d(TAG, "TaskProcessor.retryOnFailedTask(): " + "\n" + "MESSAGE: notifying the waiting thread with the lock <lockForRetryTaskExternal>..." +
                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                informedAboutRetryTaskExternal = true;
                lockForRetryTaskExternal.notify();
            }
        }
    }

    /**This method can be invoked in case the current task in execution has failed. Users need not call ths method
     * in case of NOTIFY_UI_AUTO mode because the Retry and Progress dialogs are internally handled in this mode
     * and task processing automatically retries as per the user's response to those dialogs.
     *
     * Call this method ONLY if you are using the NOTIFY_UI_CUSTOM mode; Calling this method in NOTIFY_UI_AUTO mode
     * has NO EFFECT*/
    public synchronized void cancelOnFailedTask(){

        Log.d(TAG, "TaskProcessor.cancelOnFailedTask(): " + "\n" + "MESSAGE: cancelOnFailedTask() called..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        //This method would only be valid call if AC creation has failed
        if(currentTaskInExecution.taskState != AbstractTask.TASK_EXECUTION_FAILED){
            throw new RuntimeException("There is no task to be cancelled");
        }

        //Even though its Auto, the person has still called this method. Nevermind, we shall ignore
        if(notifyUIFlag == NOTIFY_UI_AUTO){
            //Do nothing.
        }
        else{
            Log.d(TAG, "TaskProcessor.cancelOnFailedTask(): " + "\n" + "MESSAGE: CANCEL button clicked..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            shouldTaskProcessingBeRetried = false;

            synchronized (lockForRetryTaskExternal){
                Log.d(TAG, "TaskProcessor.cancelOnFailedTask(): " + "\n" + "MESSAGE: notifying the waiting thread with the lock <lockForRetryTaskExternal>..." +
                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                informedAboutRetryTaskExternal = true;
                lockForRetryTaskExternal.notify();
            }
        }
    }

    private void performCleanup(){

        Log.d(TAG, "TaskProcessor.performCleanup(): " + "\n" + "MESSAGE: Cleaning up globally..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        if(workerThread != null){
            workerThread.quit();
            workerThread = null;
        }

        tasksQueue = null;
        currentTaskInExecution = null;

        tasksProcessor = null;

        isValid = false;

        //NOTE: The cleanup of taskProcessingProgressDialog and taskProcessorInstance instances are being scheduled in the main thread
        // because dismissal of the progress dialog might have not yet finished
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if(taskProcessorListener != null){
                    Log.d(TAG, "TaskProcessor.performCleanup(): " + "\n" + "MESSAGE: Calling TaskProcessorListener.onCleanupComplete()..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    taskProcessorListener.onCleanupComplete();
                }
                if(shouldShutDown){
                    context = null;
                    taskProcessingProgressDialog = null;
                    taskProcessingRetryAlertDialog = null;
                    mUIHandler = null;
                    taskProcessorListener = null;
                }
                //taskProcessorInstance = null;
            }
        });
    }

    /**Call this method for complete disposal of the TaskProcessor which cleans up the complete state. This method should be
     * called at the end when the user is done for good with the Tasks processing. It is highly advised to call this
     * method ultimately as not calling it will definitely lead to Context leak*/
    public synchronized void shutDown(){
        shouldShutDown = true;

        if(!shouldFinishUp){
            //finishUp() has not been called

            //The thread is probably waiting or in the middle of a task - Let it finish first then. Leave an indication here thus.
            shouldFinishUp = true;
            shouldWaitForNewTasks = false;

            //Lets queue a finishUp task that will executed only in case all the tasks  in the queue successfully
            //get executed
            tasksQueue.add(new AbstractTask() {
                @Override
                public Result<Void> performTask() {
                    finishUp = true;
                    return Result.RESULT_SUCCESS;
                }
            });
            //Notify up the waiting thread
            if(tasksProcessor!= null && tasksProcessor.isAlive() && tasksProcessor.getState() == Thread.State.WAITING){
                synchronized (lockForNewTasks){
                    lockForNewTasks.notify();
                }
            }
        }
        else{
            //Notify up the waiting thread
            if(tasksProcessor!= null && tasksProcessor.isAlive() && tasksProcessor.getState() == Thread.State.WAITING){
                synchronized (lockForNewTasks){
                    lockForNewTasks.notify();
                }
            }
        }
    }

    /**Set this listener to listen to overall Task processing. That includes a callback after all the
     * tasks have finished successfully; a callback in case a task fails any is intentionally made to cancel by the caller
     * and a callback for performing any activity before the start of Task processing. NOTE: This activity shall be performed in
     * a background thread*/
    public void setOverallTaskProcessorListener(TaskProcessorListener taskProcessorListener) {

        //If the task processing has started, you can't set the flag/taskListener now
        if(overallTaskProcessingState == OVERALL_TASKS_PROCESSING_STARTED){
            throw new IllegalStateException("Task processing has started. You can't set the taskListener in the middle");
        }
        this.taskProcessorListener = taskProcessorListener;
    }

    public void setUIFlag(int notifyUIFlag) {
        //If the task processing has started, you can't set the flag/taskListener now
        if(overallTaskProcessingState == OVERALL_TASKS_PROCESSING_STARTED){
            throw new IllegalStateException("Task processing has started. You can't set the flag in the middle");
        }
        checkUIFlagValidity(notifyUIFlag);
        this.notifyUIFlag = notifyUIFlag;
    }

    public void setTaskProcessingRetryDialog(AlertDialog taskProcessingRetry) {
        //If the task processing has started, you can't set the flag/taskListener now
        if(overallTaskProcessingState == OVERALL_TASKS_PROCESSING_STARTED){
            throw new IllegalStateException("Task processing has started. You can't set the Alert Dialog in the middle");
        }
        taskProcessingRetryAlertDialog = taskProcessingRetry;

        //Set the default config.
        taskProcessingRetryAlertDialog.setCancelable(false);
    }

    public void setTaskProcessingProgressDialog(ProgressDialog progressDialog) {
        //If the task processing has started, you can't set the flag/taskListener now
        if(overallTaskProcessingState == OVERALL_TASKS_PROCESSING_STARTED){
            throw new IllegalStateException("Task processing has started. You can't set the Progress Dialog in the middle");
        }
        taskProcessingProgressDialog = progressDialog;

        //Set the default config.
        taskProcessingProgressDialog.setIndeterminate(true);
        taskProcessingProgressDialog.setCancelable(false);
        taskProcessingProgressDialog.setCanceledOnTouchOutside(false);
    }

    /**Check if the UI flag belongs to the set of valid ones*/
    private void checkUIFlagValidity(int taskProcessorUIFlag){
        switch(taskProcessorUIFlag){
            case NOTIFY_UI_AUTO:
                //Pass
                break;
            case NOTIFY_UI_CUSTOM:
                //Pass
                break;
            default:
                //Fail
                throw new InvalidFlagException("Invalid UI flag. Check the list of valid flags.");
        }
    }

    private void showRetryDialog(){
        //If dialog already showing, no use invoking it again
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if(taskProcessingRetryAlertDialog == null){
                    taskProcessingRetryAlertDialog = new AlertDialog.Builder(context)
                            .setTitle("Task Processing Failed")
                            .setMessage("There was a problem processing the task")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "TaskProcessor.showRetryDialog(): " + "\n" + "MESSAGE: RETRY button clicked..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());

                                    dialog.cancel();

                                    shouldTaskProcessingBeRetried = true;

                                    //Inform the guy waiting to know the user's response
                                    informedAboutRetryInternal = true;
                                    synchronized(lockForRetryInternal){
                                        TaskProcessor.this.shouldTaskProcessingBeRetried = shouldTaskProcessingBeRetried;
                                        lockForRetryInternal.notify();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "TaskProcessor.showRetryDialog(): " + "\n" + "MESSAGE: CANCEL button clicked..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                    dialog.cancel();

                                    shouldTaskProcessingBeRetried = false;
                                    //Indication that the Task processing has been unsuccessful and should be retried

                                    //Inform the guy waiting to know the user's response
                                    informedAboutRetryInternal = true;
                                    synchronized(lockForRetryInternal){
                                        TaskProcessor.this.shouldTaskProcessingBeRetried = shouldTaskProcessingBeRetried;
                                        lockForRetryInternal.notify();
                                    }
                                }
                            })
                            .setCancelable(false)
                            .create();
                }
                if(!taskProcessingRetryAlertDialog.isShowing()){
                    Log.d(TAG, "TaskProcessor.showRetryDialog(): " + "\n" + "MESSAGE: Showing the Retry dialog..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    taskProcessingRetryAlertDialog.show();
                }
                else{
                    if(DEBUG_MODE){
                        throw new RuntimeException("Retry Dialog already showing - Check for redundant invocation by the second caller");
                    }
                }

            }
        });

    }

    private void showTaskProcessingProgressDialog(){
        //If dialog already in progress, no use invoking it again
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if(taskProcessingProgressDialog == null){
                    taskProcessingProgressDialog = new ProgressDialog(context);
                    taskProcessingProgressDialog.setIndeterminate(true);
                    taskProcessingProgressDialog.setTitle("Performing Task...");
                    taskProcessingProgressDialog.setCancelable(true);
                    taskProcessingProgressDialog.setCanceledOnTouchOutside(false);
                }
                if(!taskProcessingProgressDialog.isShowing()){
                    Log.d(TAG, "TaskProcessor.showRetryDialog(): " + "\n" + "MESSAGE: Showing the progress dialog..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    taskProcessingProgressDialog.show();
                }
                else{
                    if(DEBUG_MODE){
                        throw new RuntimeException("Dialog already in progress - Check for redundant invocation by the second caller");
                    }
                }
            }
        });
    }

    private void dismissTaskProcessingProgressDialog(){
        //If dialog already dismissed, no use dismissing it again
        if(taskProcessingProgressDialog.isShowing()){
            Log.d(TAG, "AccountCreationManager.showRetryDialog(): " + "\n" + "MESSAGE: Dismissing the progress dialog..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    taskProcessingProgressDialog.dismiss();
                    taskProcessingProgressDialog.cancel();
                }
            });
        }
        else{
            if(DEBUG_MODE){
                throw new RuntimeException("Dialog already been dismissed - Check for redundant dismissal by the second caller");
            }
        }
    }

    public boolean isTaskProcessorValid() {
        return isValid;
    }

    /**
     * This interface shall be implemented by anyone wanting to get a window into the overall task processing states
     * and perform tasks deemed necessary by them at those points.
     */
    public interface TaskProcessorListener {
        void onTaskProcessingComplete();
        void onTaskProcessingFailed();
        void onPreTaskProcessingStart();
        void onCleanupComplete();
    }

    static class InvalidFlagException extends RuntimeException{
        public InvalidFlagException(String msg){
            super(msg);
        }
    }

    private static class TaskProcessingWorkerThread extends HandlerThread {

        private Handler handler;

        public TaskProcessingWorkerThread(String threadName){
            super(threadName);
        }

        public void execute(Runnable r){
            handler.post(r);
        }
        public Handler getHandlerInstance(){
            return handler;
        }

        public void prepareHandler(){
            if(handler == null){
                handler = new Handler(getLooper());
            }
        }
    }

}
