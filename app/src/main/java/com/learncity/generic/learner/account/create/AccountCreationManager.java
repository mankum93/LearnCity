package com.learncity.generic.learner.account.create;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelableVer1;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by DJ on 2/5/2017.
 */

public class AccountCreationManager {

    private static final String TAG = "AccountCreationManager";

    /**Debug Mode has been enable default for the developer. It allows for the developer to test
     * rigorously for any invalid condition/behavior. Developer is encouraged to write their test code internally
     * within Debug mode to ensure robustness of code.*/
    private final boolean DEBUG_MODE = false;

    public static final int CREATE_ACCOUNT_SERVER = 0x0001;
    public static final int CREATE_ACCOUNT_LOCAL = 0x0002;
    public static final int CREATE_ACCOUNT_SERVER_LOCAL = 0x0003;
    public static final int CREATE_ACCOUNT_DEFAULT = CREATE_ACCOUNT_SERVER_LOCAL;

    private static final int GAE_ACCOUNT_CREATION_MODE = 0x0004;
    private static final int SQLITE_ACCOUNT_CREATION_MODE = 0x0005;

    private static final int DEFAULT_ACCOUNT_CREATION_MODE = GAE_ACCOUNT_CREATION_MODE | SQLITE_ACCOUNT_CREATION_MODE;

    public static final int NOTIFY_UI_AUTO = 0x0010;
    public static final int NOTIFY_UI_CUSTOM = 0x0011;
    public static final int NOTIFY_UI_DEFAULT = NOTIFY_UI_AUTO;
    //Internal use only
    private static final int NOTIFY_UI_NO_STATE = 0x0012;

    /**States indicating the Account Creation*/
    private static final int ACCOUNT_CREATION_STARTED = 0x0020;
    private static final int LOCAL_ACCOUNT_CREATION_STARTED = 0x0021;
    private static final int SERVER_ACCOUNT_CREATION_STARTED = 0x0022;
    private static final int BEFORE_ACCOUNT_CREATION_START = 0x0023;
    private static final int BEFORE_LOCAL_ACCOUNT_CREATION_START = 0x0024;
    private static final int BEFORE_SERVER_ACCOUNT_CREATION_START = 0x0025;
    private static final int ACCOUNT_CREATION_COMPLETED = 0x0026;
    private static final int LOCAL_ACCOUNT_CREATION_COMPLETED = 0x0027;
    private static final int SERVER_ACCOUNT_CREATION_COMPLETED = 0x0028;
    private static final int ACCOUNT_CREATION_FAILED = 0x0029;
    private static final int LOCAL_ACCOUNT_CREATION_FAILED = 0x0030;
    private static final int SERVER_ACCOUNT_CREATION_FAILED = 0x0031;

    private static AccountCreationManager accountCreationManager;

    private AccountCreationTask serverAccountCreationTask;
    private AccountCreationTask localAccountCreationTask;

    private GenericLearnerProfileParcelableVer1 profile;

    private Context context;

    private AccountCreationListener localAccountCreationListener;
    private AccountCreationListener serverAccountCreationListener;

    private AccountCreationListener accountCreationListener;

    private int accountCreationFlag = CREATE_ACCOUNT_DEFAULT;

    private int accountCreationMode = DEFAULT_ACCOUNT_CREATION_MODE;

    /**This flag indicates if user wants to implement their own dialogs UI for AC creation checkpoints or
     * prefer the default implementation. List of valid UI flags:
     * {NOTIFY_UI_AUTO, NOTIFY_UI_CUSTOM}
     * It defaults to the Auto mode
     * */
    private int accountCreationUIFlag = NOTIFY_UI_DEFAULT;

    /**Strictly for internal sanity check.
     * These are check to that if the caller has set Auto UI mode for server then it has to be auto for local as well
     * Or Vice Versa.
     * */
    //Initial states
    private int localAccountCreationUIFlag = NOTIFY_UI_NO_STATE;
    private int serverAccountCreationUIFlag = NOTIFY_UI_NO_STATE;

    //Initial states
    private int accountCreationState = BEFORE_ACCOUNT_CREATION_START;
    private int localAccountCreationState = BEFORE_LOCAL_ACCOUNT_CREATION_START;
    private int serverAccountCreationState = BEFORE_SERVER_ACCOUNT_CREATION_START;

    private AlertDialog alertDialogACCreationRetry;

    private ProgressDialog accountCreationProgressDialog;

    private final Object lockForRetryInternal = new Object();
    private boolean informedAboutRetryInternal = false;

    private final Object lockForRetryServerExternal = new Object();
    private boolean informedAboutRetryServerExternal = false;

    private final Object lockForRetryLocalExternal = new Object();
    private boolean informedAboutRetryLocalExternal = false;

    private final Object lockForFinalCleanup = new Object();
    private boolean informedAboutFinalCleanup = false;
    private boolean isReadyForFinalCleanup = false;

    private Handler mUIHandler;

    private boolean shouldAccountCreationBeRetried;

    private AccountCreationWorkerThread workerThread;


    private AccountCreationManager(@NonNull Context context){

        //TODO: Insert checks for the flag
        this.context = context;

        if(mUIHandler == null){
            mUIHandler = new Handler(context.getMainLooper());
        }
        workerThread = new AccountCreationWorkerThread("AccountCreationManager");

    }
    private AccountCreationManager(@NonNull Context context, int accountCreationFlag){

        //TODO: Insert checks for the flag
        this.context = context;

        if(mUIHandler == null){
            mUIHandler = new Handler(context.getMainLooper());
        }
        workerThread = new AccountCreationWorkerThread("AccountCreationManager");

        this.accountCreationFlag = accountCreationFlag;
    }

    /*Its quite possible that this method is called more than once. More so, its quite possible
     *that there may be 2 different contexts involved. Even though we are returning the same instance,
     * we must update the instance with the given context*/
    public static AccountCreationManager getAccountCreationManager(@NonNull Context context){

        if(accountCreationManager == null){
            return accountCreationManager = new AccountCreationManager(context);
        }
        else{
            //Refresh the context
            if(accountCreationManager.context != context){
                accountCreationManager.context = context;
            }
        }
        return accountCreationManager;
    }
    public static AccountCreationManager getAccountCreationManager(@NonNull Context context, int accountCreationFlag){

        if(accountCreationManager == null){
            return accountCreationManager = new AccountCreationManager(context, accountCreationFlag);
        }
        else{
            //Refresh the context
            if(accountCreationManager.context != context){
                accountCreationManager.context = context;
            }
            //Refresh the flag
            accountCreationManager.accountCreationFlag = accountCreationFlag;
        }
        return accountCreationManager;
    }

    public void startAccountCreation(@NonNull GenericLearnerProfileParcelableVer1 profile, final int accountCreationFlag){

        Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Called from the MAIN thread to initiate the AC creation..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        if(profile == null){
            throw new NullPointerException("Profile to be inserted cannot be null");
        }
        this.profile = profile;

        //Initialize the worker thread
        if(workerThread == null){
            workerThread = new AccountCreationWorkerThread("AccountCreationManager");
        }
        if(!workerThread.isAlive()){
            Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Starting worker thread to initiate the AC creation process..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            workerThread.start();
        }
        workerThread.prepareHandler();

        workerThread.execute(new Runnable() {
            @Override
            public void run() {

                if(!EventBus.getDefault().isRegistered(AccountCreationManager.this)){
                    Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Registering AccountManager instance with EventBus..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    EventBus.getDefault().register(AccountCreationManager.this);
                }

                switch(accountCreationFlag){

                    case CREATE_ACCOUNT_LOCAL:
                        Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Exclusively Server AC creation requested..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        if(localAccountCreationListener != null){
                            Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Calling localAccountCreationListener.onPreAccountCreation()..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            localAccountCreationListener.onPreAccountCreation();
                        }
                        performDefaultPreLocalAccountCreationTasks();
                        initLocalAccountCreator();
                        startAccountCreationLocally();
                        break;

                    case CREATE_ACCOUNT_SERVER:
                        Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Exclusively Server AC creation requested..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        if(serverAccountCreationListener != null){
                            Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Calling serverAccountCreationListener.onPreAccountCreation()..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            serverAccountCreationListener.onPreAccountCreation();
                        }
                        performDefaultPreServerAccountCreationTasks();
                        initServerAccountCreator();
                        startAccountCreationOnServer();
                        break;

                    case CREATE_ACCOUNT_SERVER_LOCAL:
                        Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Both server & local AC creation requested..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        if(localAccountCreationListener != null){
                            Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Calling localAccountCreationListener.onPreAccountCreation()..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            localAccountCreationListener.onPreAccountCreation();
                        }
                        if(serverAccountCreationListener != null){
                            Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Calling serverAccountCreationListener.onPreAccountCreation()..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            serverAccountCreationListener.onPreAccountCreation();
                        }
                        if(accountCreationListener != null){
                            Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Calling accountCreationListener.onPreAccountCreation()..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            accountCreationListener.onPreAccountCreation();
                        }
                        performDefaultPreLocalServerAccountCreationTasks();
                        initLocalServerAccountCreators();
                        startAccountCreationOnServerAndLocally();
                        break;
                }
            }
        });
    }

    public void startAccountCreation(@NonNull GenericLearnerProfileParcelableVer1 profile){
        Log.d(TAG, "AccountCreationManager.startAccountCreation(): " + "\n" + "MESSAGE: Called from the MAIN thread to initiate the AC creation..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        if(profile == null){
            throw new NullPointerException("Profile to be inserted cannot be null");
        }
        this.profile = profile;
        startAccountCreation(profile, accountCreationFlag);
    }

    private void performDefaultPreServerAccountCreationTasks() {
        Log.d(TAG, "AccountCreationManager.performDefaultPreServerAccountCreationTasks(): " + "\n" + "MESSAGE: Calling performDefaultPreServerAccountCreationTasks()..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        switch(accountCreationUIFlag){
            case NOTIFY_UI_AUTO:
                showACCreationProgressDialog();
                break;
            case NOTIFY_UI_CUSTOM:
                //The caller is expected to implement their own dialog
                break;
            default:
                //Invalid flag
                throw new InvalidFlagException("Invalid Account UI flag. Check the list of valid flags.");
        }
    }

    private void performDefaultPreLocalAccountCreationTasks() {
        Log.d(TAG, "AccountCreationManager.performDefaultPreLocalAccountCreationTasks(): " + "\n" + "MESSAGE: Calling performDefaultPreLocalAccountCreationTasks()..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        switch(accountCreationUIFlag){
            case NOTIFY_UI_AUTO:
                showACCreationProgressDialog();
                break;
            case NOTIFY_UI_CUSTOM:
                //The caller is expected to implement their own dialog
                break;
            default:
                //Invalid flag
                throw new InvalidFlagException("Invalid Account UI flag. Check the list of valid flags.");
        }
    }

    private void performDefaultPreLocalServerAccountCreationTasks() {
        Log.d(TAG, "AccountCreationManager.performDefaultPreLocalServerAccountCreationTasks(): " + "\n" + "MESSAGE: Calling performDefaultPreLocalServerAccountCreationTasks()..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        switch(accountCreationUIFlag){
            case NOTIFY_UI_AUTO:
                showACCreationProgressDialog();
                break;
            case NOTIFY_UI_CUSTOM:
                //The caller is expected to implement their own dialog
                break;
            default:
                //Invalid flag
                throw new InvalidFlagException("Invalid Account UI flag. Check the list of valid flags.");
        }
    }

    //NOTE: This method would be called from the main thread
    public void retryAccountCreation(){
        Log.d(TAG, "AccountCreationManager.retryAccountCreation(): " + "\n" + "MESSAGE: retryAccountCreation() called..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        shouldAccountCreationBeRetried = true;

        //Even though its Auto, the person has still called this method. Nevermind, we shall ignore
        if(accountCreationUIFlag == NOTIFY_UI_AUTO){
            //Do nothing.
            return;
        }
        else{
            switch(accountCreationFlag){

                case CREATE_ACCOUNT_LOCAL:
                    synchronized (lockForRetryLocalExternal){
                        Log.d(TAG, "AccountCreationManager.retryAccountCreation(): " + "\n" + "MESSAGE: notifying the waiting thread with the lock <lockForRetryLocalExternal>..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        informedAboutRetryLocalExternal = true;
                        lockForRetryLocalExternal.notifyAll();
                    }
                    break;

                case CREATE_ACCOUNT_SERVER:
                    synchronized (lockForRetryServerExternal){
                        Log.d(TAG, "AccountCreationManager.retryAccountCreation(): " + "\n" + "MESSAGE: notifying the waiting thread with the lock <lockForRetryServerExternal>..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        informedAboutRetryServerExternal = true;
                        lockForRetryServerExternal.notifyAll();
                    }
                    break;

                case CREATE_ACCOUNT_SERVER_LOCAL:
                    if(localAccountCreationState == LOCAL_ACCOUNT_CREATION_FAILED){
                        synchronized (lockForRetryLocalExternal){
                            Log.d(TAG, "AccountCreationManager.retryAccountCreation(): " + "\n" + "MESSAGE: notifying the waiting thread with the lock <lockForRetryLocalExternal>..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            informedAboutRetryLocalExternal = true;
                            lockForRetryLocalExternal.notifyAll();
                        }
                    }
                    else{
                        synchronized (lockForRetryServerExternal){
                            Log.d(TAG, "AccountCreationManager.retryAccountCreation(): " + "\n" + "MESSAGE: notifying the waiting thread with the lock <lockForRetryServerExternal>..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            informedAboutRetryServerExternal = true;
                            lockForRetryServerExternal.notifyAll();
                        }
                    }
                    break;
            }
        }
    }

    //NOTE: This method would be called from the main thread
    public void performCleanup(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isReadyForFinalCleanup){
                    performFinalCleanup();
                }
                else{
                    while(!informedAboutFinalCleanup){
                        synchronized(lockForFinalCleanup){
                            try{
                                lockForFinalCleanup.wait();
                            }
                            catch(InterruptedException ie){
                                ie.printStackTrace();
                            }
                        }
                    }
                    performFinalCleanup();
                }
            }
        }).start();
    }

    private void performFinalCleanup(){
        Log.d(TAG, "AccountCreationManager.performCleanup(): " + "\n" + "MESSAGE: Cleaning up globally..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        context = null;

        alertDialogACCreationRetry = null;
        accountCreationProgressDialog = null;

        accountCreationManager = null;

        mUIHandler = null;

        isReadyForFinalCleanup = false;
        informedAboutFinalCleanup = false;
    }

    public void resetAccountCreationFlag() {
        //TODO: Insert proper checks as per the anticipations
        this.accountCreationFlag = CREATE_ACCOUNT_DEFAULT;
    }

    private void startAccountCreationLocally() {
        //Set the AC creation state
        localAccountCreationState = LOCAL_ACCOUNT_CREATION_STARTED;
        Log.d(TAG, "AccountCreationManager.startAccountCreationLocally(): " + "\n" + "MESSAGE: Starting AC creation locally..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        localAccountCreationTask.performAccountCreation();
    }

    private void startAccountCreationOnServer() {
        //Set the AC creation state
        serverAccountCreationState = SERVER_ACCOUNT_CREATION_STARTED;
        Log.d(TAG, "AccountCreationManager.startAccountCreationLocally(): " + "\n" + "MESSAGE: Starting AC creation on the server..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        serverAccountCreationTask.performAccountCreation();
    }

    private void startAccountCreationOnServerAndLocally() {
        //Set the AC creation state
        accountCreationState = ACCOUNT_CREATION_STARTED;
        serverAccountCreationState = SERVER_ACCOUNT_CREATION_STARTED;
        Log.d(TAG, "AccountCreationManager.startAccountCreationLocally(): " + "\n" + "MESSAGE: Starting AC creation on server and then locally..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        serverAccountCreationTask.performAccountCreation();
    }

    private void initServerAccountCreator(){
        if((accountCreationMode & GAE_ACCOUNT_CREATION_MODE) == GAE_ACCOUNT_CREATION_MODE){
            GAEAccountCreationTask task = new GAEAccountCreationTask(profile);
            task.setAccountCreationListener(new AccountCreationTask.AccountCreationTaskListener() {
                @Override
                public void onAccountCreated() {
                    Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: AC created on GAE!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Update the AC creation state
                    serverAccountCreationState = SERVER_ACCOUNT_CREATION_COMPLETED;

                    if(serverAccountCreationListener != null){
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreated()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        serverAccountCreationListener.onAccountCreated();
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onPostAccountCreation()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        serverAccountCreationListener.onPostAccountCreation();
                    }
                    Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling performDefaultPostServerAccountCreationTasks()..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    performDefaultPostServerAccountCreationTasks();

                    //The user will try to initiate final cleanup at some point. If they have already, lets complete it.
                    informedAboutFinalCleanup = true;
                    isReadyForFinalCleanup = true;
                    synchronized(lockForFinalCleanup){
                        lockForFinalCleanup.notify();
                    }
                }

                @Override
                public void onAccountCreationFailed() {
                    Log.e(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: AC creation failed on GAE!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Update the AC creation state
                    serverAccountCreationState = SERVER_ACCOUNT_CREATION_FAILED;
                    Thread t;
                    /*The listener callback must be called in a separate thread because
                    * further up just after this we have to wait ASAP be it for the internal retry dialog
                    * or the external one(if the user provides one - in which case the user shall inform us about it
                    * and we shall resume then or if the user asks us to wrap it up here then be it)*/
                    if(serverAccountCreationListener != null){
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreationFailed()..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                serverAccountCreationListener.onAccountCreationFailed();
                            }
                        });
                        t.start();
                    }
                    switch(accountCreationUIFlag){
                        case NOTIFY_UI_AUTO:
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Requesting dismissal of AC creation progress Dialog..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Dismiss the already started AC creation progress dialog
                            dismissACCreationProgressDialog();
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Requesting show of Retry Dialog after failed AC creation..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Have to show a default retry dialog
                            showRetryDialog();
                            //Have to wait on the main thread for the response
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Trying to acquire the lockForRetryInternal lock..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            synchronized(lockForRetryInternal){
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: lockForRetryInternal lock acquired." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                while(!informedAboutRetryInternal){
                                    try{
                                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Suspending this thread until notified by user's response to the retry dialog..." +
                                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                                        lockForRetryInternal.wait();
                                    }
                                    catch(InterruptedException ie){
                                        //TODO: Cancelling the object creation process that is going on currently and showing a retry
                                    }
                                }
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Resuming this thread after being notified by user's response to the retry dialog..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                            }
                            break;
                        case NOTIFY_UI_CUSTOM:
                            //The caller is expected to implement their own dialog
                            /*Since the AC creation has been failed, I am gonna wait for 10 seconds
                             * before I terminate here*/
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Trying to acquire the lockForRetryServerExternal lock..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            synchronized (lockForRetryServerExternal){
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: lockForRetryServerExternal lock acquired." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                while(!informedAboutRetryServerExternal){
                                    try{
                                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Suspending this thread for MAX 10 sec. until notified by the user..." +
                                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                                        lockForRetryServerExternal.wait(10*1000);
                                    }
                                    catch(InterruptedException ie){
                                        //TODO: Cancelling the object creation process that is going on currently and showing a retry
                                    }
                                    //Safety: In case 10 sec have passed and no reply - Get out of here forcefully
                                    informedAboutRetryServerExternal = true;
                                }
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Resuming this thread after being notified by user..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                            }
                            break;
                    }
                    //Either case, time to take up further action
                    if(shouldAccountCreationBeRetried){
                        //Not resetting these boolean fields can cause a TSUNAMI of infinite requests to server
                        informedAboutRetryInternal = false;
                        informedAboutRetryServerExternal = false;

                        if(accountCreationUIFlag == NOTIFY_UI_AUTO){
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Requesting show of AC Creation Progress Dialog..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Start the AC Creation animation again
                            showACCreationProgressDialog();
                        }

                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Retrying the server AC creation..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        //Retry it
                        serverAccountCreationTask.performAccountCreation();
                    }
                    else{
                        if(accountCreationListener != null){
                            t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "AccountCreationListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreationFailed()..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                    accountCreationListener.onAccountCreationFailed();
                                }
                            });
                            t.start();
                        }
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: AC creation retry aborted intentionally. Calling performDefaultPostServerAccountCreationTasks()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        //Too bad! We return
                        performDefaultPostServerAccountCreationTasks();

                        //The user will try to initiate final cleanup at some point. If they have already, lets complete it.
                        informedAboutFinalCleanup = true;
                        isReadyForFinalCleanup = true;
                        synchronized(lockForFinalCleanup){
                            lockForFinalCleanup.notify();
                        }
                    }
                }
            });
            serverAccountCreationTask = task;
        }
    }

    private void performDefaultPostServerAccountCreationTasks() {
        Log.d(TAG, "AccountCreationManager.performDefaultPostServerAccountCreationTasks(): " + "\n" + "MESSAGE: Performing Server AC creation/failed abortion cleanup..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        //Setting the states to the initial values
        serverAccountCreationState = BEFORE_SERVER_ACCOUNT_CREATION_START;
        Log.d(TAG, "AccountCreationManager.performDefaultPostServerAccountCreationTasks(): " + "\n" + "MESSAGE: Calling AccountCreationTask.performCleanup() for server..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        serverAccountCreationTask.performCleanup();
        serverAccountCreationTask = null;

        shouldAccountCreationBeRetried = false;
        informedAboutRetryInternal = false;
        informedAboutRetryServerExternal = false;

        if(workerThread != null){
            workerThread.quit();
            workerThread = null;
        }

        profile = null;

        if(!EventBus.getDefault().isRegistered(this)){
            Log.d(TAG, "AccountCreationManager.performDefaultPostServerAccountCreationTasks(): " + "\n" + "MESSAGE: Unregistering the AccountCreationManager from EventBus..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            EventBus.getDefault().unregister(this);
        }

        Log.d(TAG, "AccountCreationManager.performDefaultPostServerAccountCreationTasks(): " + "\n" + "MESSAGE: Requesting dismissal of the AC creation progress dialog..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        switch(accountCreationUIFlag){
            case NOTIFY_UI_AUTO:
                dismissACCreationProgressDialog();
                break;
            case NOTIFY_UI_CUSTOM:
                //The caller is expected to implement their own dialog
                break;
        }
    }

    private void initLocalAccountCreator(){
        if((accountCreationMode & SQLITE_ACCOUNT_CREATION_MODE) == SQLITE_ACCOUNT_CREATION_MODE){
            SQLiteAccountCreationTask task = new SQLiteAccountCreationTask(context, profile);
            task.setAccountCreationListener(new AccountCreationTask.AccountCreationTaskListener() {
                @Override
                public void onAccountCreated() {
                    Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: AC created on SQLite!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Update the AC creation state
                    localAccountCreationState = LOCAL_ACCOUNT_CREATION_COMPLETED;

                    if(localAccountCreationListener != null){
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreated()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        localAccountCreationListener.onAccountCreated();
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onPostAccountCreation()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        localAccountCreationListener.onPostAccountCreation();
                    }
                    Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling performDefaultPostLocalAccountCreationTasks()..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    performDefaultPostLocalAccountCreationTasks();
                    //The user will try to initiate final cleanup at some point. If they have already, lets complete it.
                    informedAboutFinalCleanup = true;
                    isReadyForFinalCleanup = true;
                    synchronized(lockForFinalCleanup){
                        lockForFinalCleanup.notify();
                    }
                }

                @Override
                public void onAccountCreationFailed() {
                    Log.e(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: AC creation failed on SQLite!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Update the AC creation state
                    localAccountCreationState = LOCAL_ACCOUNT_CREATION_FAILED;

                    Thread t;
                    /*The listener callback must be called in a separate thread because
                    * further up just after this we have to wait ASAP be it for the internal retry dialog
                    * or the external one(if the user provides one - in which case the user shall inform us about it
                    * and we shall resume then or if the user asks us to wrap it up here then be it)*/
                    if(localAccountCreationListener != null){
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreationFailed()..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                localAccountCreationListener.onAccountCreationFailed();
                            }
                        });
                        t.start();
                    }
                    switch(accountCreationUIFlag){
                        case NOTIFY_UI_AUTO:
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Requesting show of Retry Dialog after failed AC creation..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Have to show a default retry dialog
                            showRetryDialog();
                            //Have to wait on the main thread for the response
                            synchronized(lockForRetryInternal){
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: lockForRetryInternal lock acquired." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                while(!informedAboutRetryInternal){
                                    try{
                                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Suspending this thread until notified by user's response to the retry dialog..." +
                                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                                        lockForRetryInternal.wait();
                                    }
                                    catch(InterruptedException ie){
                                        //TODO: Cancelling the object creation process that is going on currently and showing a retry
                                    }
                                }
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Resuming this thread after being notified by user's response to the retry dialog..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                            }
                            break;
                        case NOTIFY_UI_CUSTOM:
                            //The caller is expected to implement their own dialog
                            /*Since the AC creation has been failed, I am gonna wait for 10 seconds
                             * before I terminate here*/
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Trying to acquire the lockForRetryLocalExternal lock..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            synchronized (lockForRetryLocalExternal){
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: lockForRetryLocalExternal lock acquired." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                while(!informedAboutRetryLocalExternal){
                                    try{
                                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Suspending this thread for MAX 10 sec. until notified by the user..." +
                                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                                        lockForRetryLocalExternal.wait(10*1000);
                                    }
                                    catch(InterruptedException ie){
                                        //TODO: Cancelling the object creation process that is going on currently and showing a retry
                                    }
                                    //Safety: In case 10 sec have passed and no reply - Get out of here forcefully
                                    informedAboutRetryLocalExternal = true;
                                }
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Resuming this thread after being notified by user..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                            }
                            break;
                    }
                    //Either case, time to take up further action
                    if(shouldAccountCreationBeRetried){
                        //Not resetting these boolean fields can cause a TSUNAMI of infinite requests to server
                        informedAboutRetryInternal = false;
                        informedAboutRetryLocalExternal = false;

                        if(accountCreationUIFlag == NOTIFY_UI_AUTO){
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Requesting dismissal of AC creation progress Dialog..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Dismiss the already started AC creation progress dialog
                            dismissACCreationProgressDialog();
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Requesting show of AC Creation Progress Dialog..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Start the AC Creation animation again
                            showACCreationProgressDialog();
                        }
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Retrying the local AC creation..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        //Retry it
                        localAccountCreationTask.performAccountCreation();
                    }
                    else{
                        if(accountCreationListener != null){
                            t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "AccountCreationListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreationFailed()..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                    accountCreationListener.onAccountCreationFailed();
                                }
                            });
                            t.start();
                        }
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: AC creation retry aborted intentionally. Calling performDefaultPostLocalAccountCreationTasks()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        //Too bad! We return
                        performDefaultPostLocalAccountCreationTasks();
                        //The user will try to initiate final cleanup at some point. If they have already, lets complete it.
                        informedAboutFinalCleanup = true;
                        isReadyForFinalCleanup = true;
                        synchronized(lockForFinalCleanup){
                            lockForFinalCleanup.notify();
                        }
                    }
                }
            });
            localAccountCreationTask = task;
        }
    }

    private void performDefaultPostLocalAccountCreationTasks() {
        Log.d(TAG, "AccountCreationManager.performDefaultPostLocalAccountCreationTasks(): " + "\n" + "MESSAGE: Performing Local AC creation/failed abortion cleanup..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        //Setting the states to the initial values
        localAccountCreationState = BEFORE_LOCAL_ACCOUNT_CREATION_START;

        Log.d(TAG, "AccountCreationManager.performDefaultPostLocalAccountCreationTasks(): " + "\n" + "MESSAGE: Calling AccountCreationTask.performCleanup() for local..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        localAccountCreationTask.performCleanup();
        localAccountCreationTask = null;

        shouldAccountCreationBeRetried = false;
        informedAboutRetryInternal = false;
        informedAboutRetryLocalExternal = false;

        if(workerThread != null){
            workerThread.quit();
            workerThread = null;
        }

        profile = null;

        if(!EventBus.getDefault().isRegistered(this)){
            Log.d(TAG, "AccountCreationManager.performDefaultPostLocalAccountCreationTasks(): " + "\n" + "MESSAGE: Unregistering the AccountCreationManager from EventBus..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            EventBus.getDefault().unregister(this);
        }

        Log.d(TAG, "AccountCreationManager.performDefaultPostLocalAccountCreationTasks(): " + "\n" + "MESSAGE: Requesting dismissal of the AC creation progress dialog..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        switch(accountCreationUIFlag){
            case NOTIFY_UI_AUTO:
                dismissACCreationProgressDialog();
                break;
            case NOTIFY_UI_CUSTOM:
                //The caller is expected to implement their own dialog
                break;
        }
    }

    private void initLocalServerAccountCreators(){
        if((accountCreationMode & GAE_ACCOUNT_CREATION_MODE) == GAE_ACCOUNT_CREATION_MODE){
            GAEAccountCreationTask task = new GAEAccountCreationTask(profile);
            task.setAccountCreationListener(new AccountCreationTask.AccountCreationTaskListener() {
                @Override
                public void onAccountCreated() {
                    Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: AC created on GAE!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Update the AC creation state
                    serverAccountCreationState = SERVER_ACCOUNT_CREATION_COMPLETED;

                    /*NOTE: It is best to clean up after final creation locally. So,
                    * call all the 3 post default tasks at the end.*/

                    if(serverAccountCreationListener != null){
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreated()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        serverAccountCreationListener.onAccountCreated();
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onPostAccountCreation()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        serverAccountCreationListener.onPostAccountCreation();
                    }

                    Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Now starting local AC creation after server AC creation..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Now, create the account on the local Db
                    localAccountCreationState = LOCAL_ACCOUNT_CREATION_STARTED;
                    localAccountCreationTask.performAccountCreation();
                }

                @Override
                public void onAccountCreationFailed() {
                    Log.e(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: AC creation failed on GAE!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Update the AC creation state
                    serverAccountCreationState = SERVER_ACCOUNT_CREATION_FAILED;
                    Thread t;
                    /*The listener callback must be called in a separate thread because
                    * further up just after this we have to wait ASAP be it for the internal retry dialog
                    * or the external one(if the user provides one - in which case the user shall inform us about it
                    * and we shall resume then or if the user asks us to wrap it up here then be it)*/
                    if(serverAccountCreationListener != null){
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreationFailed()..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                serverAccountCreationListener.onAccountCreationFailed();
                            }
                        });
                        t.start();
                    }
                    switch(accountCreationUIFlag){
                        case NOTIFY_UI_AUTO:
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Requesting dismissal of AC creation progress Dialog..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Dismiss the already started AC creation progress dialog
                            dismissACCreationProgressDialog();
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Requesting show of Retry Dialog after failed AC creation..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Have to show a default retry dialog
                            showRetryDialog();
                            //Have to wait on the main thread for the response
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Trying to acquire the lockForRetryInternal lock..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            synchronized(lockForRetryInternal){
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: lockForRetryInternal lock acquired." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                while(!informedAboutRetryInternal){
                                    try{
                                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Suspending this thread until notified by user's response to the retry dialog..." +
                                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                                        lockForRetryInternal.wait();
                                    }
                                    catch(InterruptedException ie){
                                        //TODO: Cancelling the object creation process that is going on currently and showing a retry
                                    }
                                }
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Resuming this thread after being notified by user's response to the retry dialog..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                            }
                            break;
                        case NOTIFY_UI_CUSTOM:
                            //The caller is expected to implement their own dialog
                            /*Since the AC creation has been failed, I am gonna wait for 10 seconds
                             * before I terminate here*/
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Trying to acquire the lockForRetryServerExternal lock..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            synchronized (lockForRetryServerExternal){
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: lockForRetryServerExternal lock acquired." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                while(!informedAboutRetryServerExternal){
                                    try{
                                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Suspending this thread for MAX 10 sec. until notified by the user..." +
                                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                                        lockForRetryServerExternal.wait(10*1000);
                                    }
                                    catch(InterruptedException ie){
                                        //TODO: Cancelling the object creation process that is going on currently and showing a retry
                                    }
                                    //Safety: In case 10 sec have passed and no reply - Get out of here forcefully
                                    informedAboutRetryServerExternal = true;
                                }
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Resuming this thread after being notified by user..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                            }
                            break;
                    }
                    //Either case, time to take up further action
                    if(shouldAccountCreationBeRetried){
                        //Not resetting these boolean fields can cause a TSUNAMI of infinite requests to server
                        informedAboutRetryInternal = false;
                        informedAboutRetryServerExternal = false;

                        if(accountCreationUIFlag == NOTIFY_UI_AUTO){
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Requesting show of AC Creation Progress Dialog..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Start the AC Creation animation again
                            showACCreationProgressDialog();
                        }
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Retrying the server AC creation..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        //Retry it
                        serverAccountCreationTask.performAccountCreation();
                    }
                    else{
                        if(accountCreationListener != null){
                            t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "AccountCreationListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreationFailed()..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                    accountCreationListener.onAccountCreationFailed();
                                }
                            });
                            t.start();
                        }
                        //Too bad! We return
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: AC creation retry aborted intentionally. Calling performDefaultPostLocalAccountCreationTasks()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        performDefaultPostLocalAccountCreationTasks();
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: AC creation retry aborted intentionally. Calling performDefaultPostServerAccountCreationTasks()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        performDefaultPostServerAccountCreationTasks();
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: AC creation retry aborted intentionally. Calling performDefaultPostLocalServerAccountCreationTasks()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        performDefaultPostLocalServerAccountCreationTasks();

                        //The user will try to initiate final cleanup at some point. If they have already, lets complete it.
                        informedAboutFinalCleanup = true;
                        isReadyForFinalCleanup = true;
                        synchronized(lockForFinalCleanup){
                            lockForFinalCleanup.notify();
                        }
                    }
                }
            });
            serverAccountCreationTask = task;
        }
        if((accountCreationMode & SQLITE_ACCOUNT_CREATION_MODE) == SQLITE_ACCOUNT_CREATION_MODE){
            SQLiteAccountCreationTask task = new SQLiteAccountCreationTask(context, profile);
            task.setAccountCreationListener(new AccountCreationTask.AccountCreationTaskListener() {
                @Override
                public void onAccountCreated() {
                    Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: AC created on SQLite!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Update the AC creation state
                    localAccountCreationState = LOCAL_ACCOUNT_CREATION_COMPLETED;
                    accountCreationState = ACCOUNT_CREATION_COMPLETED;

                    if(localAccountCreationListener != null){
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling LOCAL AccountCreationListener.onAccountCreated()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        localAccountCreationListener.onAccountCreated();
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling LOCAL AccountCreationListener.onPostAccountCreation()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        localAccountCreationListener.onPostAccountCreation();
                    }
                    if(accountCreationListener != null){
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling SERVER AccountCreationListener.onAccountCreated()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        accountCreationListener.onAccountCreated();
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling SERVER AccountCreationListener.onPostAccountCreation()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        accountCreationListener.onPostAccountCreation();
                    }
                    Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling performDefaultPostLocalAccountCreationTasks()..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    performDefaultPostLocalAccountCreationTasks();
                    Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling performDefaultPostServerAccountCreationTasks()..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    performDefaultPostServerAccountCreationTasks();
                    Log.d(TAG, "AccountCreationTaskListener.onAccountCreated(): " + "\n" + "MESSAGE: Calling performDefaultPostLocalServerAccountCreationTasks()..." +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    performDefaultPostLocalServerAccountCreationTasks();

                    //The user will try to initiate final cleanup at some point. If they have already, lets complete it.
                    informedAboutFinalCleanup = true;
                    isReadyForFinalCleanup = true;
                    synchronized(lockForFinalCleanup){
                        lockForFinalCleanup.notify();
                    }
                }

                @Override
                public void onAccountCreationFailed() {
                    Log.e(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: AC creation failed on SQLite!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Update the AC creation state
                    localAccountCreationState = LOCAL_ACCOUNT_CREATION_FAILED;
                    accountCreationState = ACCOUNT_CREATION_FAILED;
                    Thread t;
                    /*The listener callback must be called in a separate thread because
                    * further up just after this we have to wait ASAP be it for the internal retry dialog
                    * or the external one(if the user provides one - in which case the user shall inform us about it
                    * and we shall resume then or if the user asks us to wrap it up here then be it)*/
                    if(localAccountCreationListener != null){
                        t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Calling LOCAL AccountCreationListener.onAccountCreationFailed()..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                localAccountCreationListener.onAccountCreationFailed();
                                if(accountCreationListener != null){
                                    Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Calling OVERALL AccountCreationListener.onAccountCreationFailed()..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                    accountCreationListener.onAccountCreationFailed();
                                }
                            }
                        });
                        t.start();
                    }
                    switch(accountCreationUIFlag){
                        case NOTIFY_UI_AUTO:
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Requesting dismissal of AC creation progress Dialog..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Dismiss the already started AC creation progress dialog
                            dismissACCreationProgressDialog();
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Requesting show of Retry Dialog after failed AC creation..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Have to show a default retry dialog
                            showRetryDialog();
                            //Have to wait on the main thread for the response
                            synchronized(lockForRetryInternal){
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: lockForRetryInternal lock acquired." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                while(!informedAboutRetryInternal){
                                    try{
                                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Suspending this thread until notified by user's response to the retry dialog..." +
                                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                                        lockForRetryInternal.wait();
                                    }
                                    catch(InterruptedException ie){
                                        //TODO: Cancelling the object creation process that is going on currently and showing a retry
                                    }
                                }
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Resuming this thread after being notified by user's response to the retry dialog..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                            }
                            break;
                        case NOTIFY_UI_CUSTOM:
                            //The caller is expected to implement their own dialog
                            /*Since the AC creation has been failed, I am gonna wait for 10 seconds
                             * before I terminate here*/
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Trying to acquire the lockForRetryLocalExternal lock..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            synchronized (lockForRetryLocalExternal){
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: lockForRetryLocalExternal lock acquired." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                                while(!informedAboutRetryLocalExternal){
                                    try{
                                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Suspending this thread for MAX 10 sec. until notified by the user..." +
                                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                                        lockForRetryLocalExternal.wait(10*1000);
                                    }
                                    catch(InterruptedException ie){
                                        //TODO: Cancelling the object creation process that is going on currently and showing a retry
                                    }
                                    //Safety: In case 10 sec have passed and no reply - Get out of here forcefully
                                    informedAboutRetryLocalExternal = true;
                                }
                                Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Resuming this thread after being notified by user..." +
                                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                            }
                            break;
                    }
                    //Either case, time to take up further action
                    if(shouldAccountCreationBeRetried){
                        //Not resetting these boolean fields can cause a TSUNAMI of infinite requests to server
                        informedAboutRetryInternal = false;
                        informedAboutRetryLocalExternal = false;

                        if(accountCreationUIFlag == NOTIFY_UI_AUTO){
                            Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Requesting show of AC Creation Progress Dialog..." +
                                    "\n" +"Thread ID: " + Thread.currentThread().getId());
                            //Start the AC Creation animation again
                            showACCreationProgressDialog();
                        }
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Retrying the local AC creation..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        //Retry it
                        localAccountCreationTask.performAccountCreation();
                    }
                    else{
                        if(accountCreationListener != null){
                            t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "AccountCreationListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: Calling AccountCreationListener.onAccountCreationFailed()..." +
                                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                                    accountCreationListener.onAccountCreationFailed();
                                }
                            });
                            t.start();
                        }
                        //Too bad! We return
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: AC creation retry aborted intentionally. Calling performDefaultPostLocalAccountCreationTasks()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        performDefaultPostLocalAccountCreationTasks();
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: AC creation retry aborted intentionally. Calling performDefaultPostServerAccountCreationTasks()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        performDefaultPostServerAccountCreationTasks();
                        Log.d(TAG, "AccountCreationTaskListener.onAccountCreationFailed(): " + "\n" + "MESSAGE: AC creation retry aborted intentionally. Calling performDefaultPostLocalServerAccountCreationTasks()..." +
                                "\n" +"Thread ID: " + Thread.currentThread().getId());
                        performDefaultPostLocalServerAccountCreationTasks();

                        //The user will try to initiate final cleanup at some point. If they have already, lets complete it.
                        informedAboutFinalCleanup = true;
                        isReadyForFinalCleanup = true;
                        synchronized(lockForFinalCleanup){
                            lockForFinalCleanup.notify();
                        }
                    }
                }
            });
            localAccountCreationTask = task;
        }
    }

    private void performDefaultPostLocalServerAccountCreationTasks() {
        Log.d(TAG, "AccountCreationManager.performDefaultPostLocalServerAccountCreationTasks(): " + "\n" + "MESSAGE: Performing Local AC creation/failed abortion cleanup..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        //Setting the states to the initial values
        accountCreationState = BEFORE_ACCOUNT_CREATION_START;

        shouldAccountCreationBeRetried = false;
        informedAboutRetryInternal = false;
        informedAboutRetryLocalExternal = false;
        informedAboutRetryServerExternal = false;

        if(workerThread != null){
            workerThread.quit();
            workerThread = null;
        }

        profile = null;

        if(!EventBus.getDefault().isRegistered(this)){
            Log.d(TAG, "AccountCreationManager.performDefaultPostLocalServerAccountCreationTasks(): " + "\n" + "MESSAGE: Unregistering the AccountCreationManager from EventBus..." +
                    "\n" +"Thread ID: " + Thread.currentThread().getId());
            EventBus.getDefault().unregister(this);
        }

        Log.d(TAG, "AccountCreationManager.performDefaultPostLocalServerAccountCreationTasks(): " + "\n" + "MESSAGE: Requesting dismissal of the AC creation progress dialog..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        switch(accountCreationUIFlag){
            case NOTIFY_UI_AUTO:
                dismissACCreationProgressDialog();
                break;
            case NOTIFY_UI_CUSTOM:
                //The caller is expected to implement their own dialog
                break;
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
    public void shouldAccountCreationBeRetried(Boolean shouldAccountCreationBeRetried){
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

    public int getAccountCreationFlag() {
        return accountCreationFlag;
    }

    public void setAccountCreationFlag(int accountCreationFlag) {
        //TODO: Insert proper checks as per the anticipations
        this.accountCreationFlag = accountCreationFlag;
    }

    public void setLocalAccountCreationListener(AccountCreationListener localAccountCreationTaskListener, int flagUI) {

        //If the local account creation has started, you can't set the flag/listener now
        if(localAccountCreationState == LOCAL_ACCOUNT_CREATION_STARTED || accountCreationState == ACCOUNT_CREATION_STARTED){
            throw new IllegalStateException("Account creation has started. You can't set the listener/flag in the middle");
        }
        checkUIFlagValidity(flagUI);
        if(localAccountCreationUIFlag == NOTIFY_UI_NO_STATE && serverAccountCreationUIFlag == NOTIFY_UI_NO_STATE){
            //Setting it the first time.
            this.accountCreationUIFlag = flagUI;
            localAccountCreationUIFlag = flagUI;
        }
        else if(localAccountCreationUIFlag == NOTIFY_UI_NO_STATE && serverAccountCreationUIFlag != NOTIFY_UI_NO_STATE){
            //Server's flag has already been set.
            if(flagUI != serverAccountCreationUIFlag){
                //Server and local flags are not matching. BOOM!
                throw new UnmatchingFlagException("The flags for both Local and Server should be matching. You have set" +
                        "the server flag as " + serverAccountCreationUIFlag + " while the local flag you are trying to set is " +
                        flagUI);
            }
            else{
                //Both the flags equal. You may pass!!!
                this.accountCreationUIFlag = flagUI;
                localAccountCreationUIFlag = flagUI;
            }
        }
        else if(localAccountCreationUIFlag != NOTIFY_UI_NO_STATE && serverAccountCreationUIFlag == NOTIFY_UI_NO_STATE){
            //Resetting the local flag. Hmph...Pass
            this.accountCreationUIFlag = flagUI;
            localAccountCreationUIFlag = flagUI;
        }
        else{
            //Both have been set. They are bound to be equal.
            if(flagUI != accountCreationUIFlag){
                throw new UnmatchingFlagException("The flags for both Local and Server should be matching. You have set" +
                        "both the flags as " + accountCreationUIFlag + " while the local flag you are trying to set is " +
                        flagUI);
            }
            else{
                //Setting the local with the same flag again. Be my guest.
            }
        }
        this.localAccountCreationListener = localAccountCreationTaskListener;
    }

    public void setServerAccountCreationListener(AccountCreationListener serverAccountCreationTaskListener, int flagUI) {

        //If the server account creation has started, you can't set the flag/listener now
        if(serverAccountCreationState == SERVER_ACCOUNT_CREATION_STARTED || accountCreationState == ACCOUNT_CREATION_STARTED){
            throw new IllegalStateException("Account creation has started. You can't set the listener/flag in the middle");
        }

        checkUIFlagValidity(flagUI);
        if(serverAccountCreationUIFlag == NOTIFY_UI_NO_STATE && localAccountCreationUIFlag == NOTIFY_UI_NO_STATE){
            //Setting it the first time.
            this.accountCreationUIFlag = flagUI;
            serverAccountCreationUIFlag = flagUI;
        }
        else if(serverAccountCreationUIFlag == NOTIFY_UI_NO_STATE && localAccountCreationUIFlag != NOTIFY_UI_NO_STATE){
            //Local's flag has already been set.
            if(flagUI != localAccountCreationUIFlag){
                //Server and local flags are not matching. BOOM!
                throw new UnmatchingFlagException("The flags for both Local and Server should be matching. You have set" +
                        "the local flag as " + localAccountCreationUIFlag + " while the server flag you are trying to set is " +
                        flagUI);
            }
            else{
                //Both the flags equal. You may pass!!!
                this.accountCreationUIFlag = flagUI;
                serverAccountCreationUIFlag = flagUI;
            }
        }
        else if(serverAccountCreationUIFlag != NOTIFY_UI_NO_STATE && localAccountCreationUIFlag == NOTIFY_UI_NO_STATE){
            //Resetting the local flag. Hmph...Pass
            this.accountCreationUIFlag = flagUI;
            serverAccountCreationUIFlag = flagUI;
        }
        else{
            //Both have been set. They are bound to be equal.
            if(flagUI != accountCreationUIFlag){
                throw new UnmatchingFlagException("The flags for both Local and Server should be matching. You have set" +
                        "both the flags as " + accountCreationUIFlag + " while the local flag you are trying to set is " +
                        flagUI);
            }
            else{
                //Setting the local with the same flag again. Be my guest.
            }
        }
        this.serverAccountCreationListener = serverAccountCreationTaskListener;

    }

    public void setAccountCreationListener(AccountCreationListener accountCreationListener, int flagUI) {

        //If the account creation has started, you can't set the flag/listener now
        if(accountCreationState == ACCOUNT_CREATION_STARTED){
            throw new IllegalStateException("Account creation has started. You can't set the listener/flag in the middle");
        }
        checkUIFlagValidity(flagUI);
        //Since AC creation hasn't begun yet, user can set the flag and listener as many times as they want
        this.accountCreationUIFlag = flagUI;
        this.accountCreationListener = accountCreationListener;
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

    /**
     * This interface shall be implemented by anyone wanting to get a window into the AC creation states
     * and perform tasks deemed necessary by them at those points.
     */
    public interface AccountCreationListener {
        void onAccountCreated();
        void onAccountCreationFailed();
        void onPreAccountCreation();
        void onPostAccountCreation();
    }

    class InvalidFlagException extends RuntimeException{
        public InvalidFlagException(String msg){
            super(msg);
        }
    }

    class UnmatchingFlagException extends RuntimeException{
        public UnmatchingFlagException(String msg){
            super(msg);
        }
    }

}
