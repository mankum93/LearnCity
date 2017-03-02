package com.learncity.util.account_management.impl;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.learncity.loginApi.model.GenericLearnerProfileVer1;
import com.learncity.util.account_management.AbstractTask;
import com.learncity.util.account_management.Service;
import com.learncity.util.account_management.Task;
import com.learncity.util.account_management.TaskProcessor;

import java.io.IOException;


/**
 * Created by DJ on 2/16/2017.
 */

public class LoginService {

    private final String TAG = "LoginService";

    /**Account creation status indicators*/
    public static final int LOGIN_FAILED = Task.TASK_FAILED;
    public static final int LOGIN_SUCCESSFUL = Task.TASK_COMPLETED;

    public static final int NOTIFY_UI_AUTO = TaskProcessor.NOTIFY_UI_AUTO;
    public static final int NOTIFY_UI_CUSTOM = TaskProcessor.NOTIFY_UI_CUSTOM;
    public static final int NOTIFY_UI_DEFAULT = TaskProcessor.NOTIFY_UI_DEFAULT;

    private TaskProcessor taskProcessor;
    private Context context;
    private LoginListener loginListener;

    private LoginEventResponse loginEventResponse;

    private boolean finishUpInitiated = false;
    private boolean hasFinishedUp = false;
    private final Object finishUpLock = new Object();
    private Thread shutDownThread;

    private final Object refreshLock = new Object();
    private Thread refreshThread;

    int serviceState;

    private Service.ServiceStateListener serviceStateListener;

    /**The default task processor listener in case the service caller decides not to provide one*/
    private TaskProcessorListenerImpl defaultTaskProcessorListener;

    //private static LoginService loginService;

    /**Call this method to re-awaken the service(after it has been asked to finish up
     * and it has completed the finish up) to start taking requests again. If the finish up has not
     * completed yet, this method facilitates specifying if you
     * want to block the main thread or wait in the background thread to let it finish up.
     * Define the code to be executed on finish up in the LoginListener.onLoginServiceRefresh()
     * (only in the case you choose to invoke this method in non-blocking mode).
     * NOTE: Most probably, you won't feel the need to call this method - you can refrain from calling
     * finishUp() on the service and queue tasks when the need arises again. However, if there is a considerable
     * time interval between the first usage of the service and the consecutive one, you might want to
     * consider calling this method after you have called finishUp()
     * @param context You can optionally pass in the context if it has changed(you are on some other Activity for example)
     * By default, it uses the previously set context
     * @param shouldBlock Specify if you want to block the main thread until it finishes up refreshing with "true"
     * Or, false will not block but you will have to write the code to be executed on refreshUp in the LoginListener.onLoginServiceRefresh()*/
    public void refreshService(@Nullable Context context, boolean shouldBlock){
        //If a task is queued after finishup; task processor will throw and exception but we have
        // to ensure that after shutdown, no more calls to service are serviced
        if(serviceState == Service.SERVICE_SHUTDOWN_REQUESTED){
            throw new IllegalStateException("You have requested for a service shutdown You can't refresh now." +
                    "To queue more request, load the service again");
        }
        if(serviceState != Service.SERVICE_FINISHUP_REQUESTED){
            throw new IllegalStateException("You have not requested for a service finish up yet. You can't refresh now.");
        }
        //If finishUp() has completed then only I can refresh the service
        if(hasFinishedUp){
            taskProcessor = new TaskProcessor(context==null?this.context:context);
        }
        else{
            if(shouldBlock){
                while(!hasFinishedUp){

                }
                taskProcessor = new TaskProcessor(context==null?this.context:context);

                //Call the service state callback
                if(serviceStateListener != null){
                    serviceStateListener.onServiceRefresh();
                }
            }
            else{
                //Wait in a background thread
                refreshThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(!hasFinishedUp){
                            synchronized (refreshLock){
                                try{
                                    refreshLock.wait();
                                }
                                catch(InterruptedException ie){
                                    //Think of something
                                }
                            }
                        }
                        if(loginListener != null){
                            //Now that finishUp() has completed, please do the honors for the caller of this method
                            loginListener.onLoginServiceRefresh();
                        }
                        //Call the service state callback
                        if(serviceStateListener != null){
                            serviceStateListener.onServiceRefresh();
                        }
                    }
                });
                refreshThread.start();
            }
        }

    }

    /**Call this method only when you just want to refresh the context and not if you have initiated a SHUTDOWN or
     * FINISHUP of the service
     * @param context The context to be refreshed*/
    public void refreshContext(@Nullable Context context){

        if(context == null){
            return;
        }
        //If a task is queued after finishup; task processor will throw and exception but we have
        // to ensure that after shutdown, no more calls to service are serviced
        if(serviceState == Service.SERVICE_SHUTDOWN_REQUESTED){
            throw new IllegalStateException("You have requested for a service shutdown You can't refresh now." +
                    "To queue more request, load the service again");
        }
        else if(serviceState == Service.SERVICE_FINISHUP_REQUESTED){
            throw new IllegalStateException("You have requested for a service finish up. You can't refresh now." +
                    "To queue more request, load the service again");
        }
        //Refresh the context
        this.context = context;
        taskProcessor.refreshContext(context);
    }


    public LoginService(Context context) {
        this.context = context;
        //Initialize the TaskProcessor
        taskProcessor = new TaskProcessor(context);
        //Register with EventBus
        //EventBus.getDefault().register(this);

        //Update the Service state
        serviceState = Service.SERVICE_READY;
    }

    /*
    public static LoginService getLoginService(@NonNull Context context){

        if(loginService == null){
            return loginService = new LoginService(context);
        }
        else{
            //Refresh the context
            if(loginService.context != context){
                loginService.context = context;
                loginService.taskProcessor.refreshContext(context);
            }
        }
        return loginService;
    }
    */


    /**Call this method to shutdown the service for good, i.e, the service shall not accept more request as well shall
     * have to be loaded again if is required. Call this method when you are sure that you are done using up the service.
     * You can also initiate a shutDown() of the service through the shutDown() of AccountManager
     * NOTE: You are advised to call this method in the onStop() method of your Activity if not before.*/
    //Reasons for synchronization:
    //1. We don't want the finishup and shutdown to be called at the same time because of the interaction between the two
    //2. We don't want that the finishUp complete callback is called when this thread is in the state of init
    //because the callback wakes up this thread only
    public synchronized void shutDown(){
        //Update the service state
        serviceState = Service.SERVICE_SHUTDOWN_REQUESTED;

        //If finish up has not already been initiated, initiate it
        if(!finishUpInitiated){
            finishUp();
        }
        shutDownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (finishUpLock){
                    while(!hasFinishedUp){
                        try{
                            finishUpLock.wait();
                        }
                        catch(InterruptedException ie){
                            //Think of something
                        }
                    }
                }
                //Unregister the instace with the EventBus
                //EventBus.getDefault().unregister(this);
                taskProcessor.shutDown();
                loginListener = null;
                taskProcessor = null;
                context = null;
                loginEventResponse = null;
                defaultTaskProcessorListener = null;

                //loginService = null;

                //Call the service state callback
                if(serviceStateListener != null){
                    serviceStateListener.onServiceShutdown();
                }
                serviceStateListener = null;
                shutDownThread = null;
            }
        });
        shutDownThread.start();

    }

    /**Call this method when you want to queue a task for processing. Do not call this method if you have initiated
     * a shutDown() or a finishUp()*/
    public void queueTask(AbstractTask task) {
        //If a task is queued after finishup; task processor will throw and exception but we have
        // to ensure that after shutdown, no more calls to service are serviced
        if(serviceState == Service.SERVICE_SHUTDOWN_REQUESTED){
            throw new IllegalStateException("You have requested for a service shutdown You can't queue any more requests." +
                    "To queue more request, load the service again");
        }
        taskProcessor.queueTask(task);
    }

    /**This method asks the TaskProcessor to finish up with the tasks currently enqueued and accept no new tasks further.
     * If you are done with using the service for good, call the shutDown() method instead of this method. Shutting down the
     * service will finishUp() as well as perform a complete cleanup.
     * */
    public synchronized void finishUp() {

        //Update the service state
        serviceState = Service.SERVICE_FINISHUP_REQUESTED;

        finishUpInitiated = true;
        if(taskProcessor != null){
            taskProcessor.finishUp();
        }
    }

    /**Call this method when you want to queue tasks for processing. Do not call this method if you have initiated
     * a shutDown() or a finishUp()*/
    public void queueTasks(AbstractTask... tasks) {
        //If a task is queued after finishup; task processor will throw and exception but we have
        // to ensure that after shutdown, no more calls to service are serviced
        if(serviceState == Service.SERVICE_SHUTDOWN_REQUESTED){
            throw new IllegalStateException("You have requested for a service shutdown You can't queue any more requests." +
                    "To queue more request, load the service again");
        }
        taskProcessor.queueTasks(tasks);
    }

    /**This method can be invoked in case the current task in execution has failed. Users need not call ths method
     * in case of NOTIFY_UI_AUTO mode because the Retry and Progress dialogs are internally handled in this mode
     * and task processing automatically retries as per the user's response to those dialogs.
     *
     * Call this method ONLY if you are using the NOTIFY_UI_CUSTOM mode; Calling this method in NOTIFY_UI_AUTO mode
     * has NO EFFECT*/
    public void cancelOnFailedLogin() {
        taskProcessor.cancelOnFailedTask();
    }

    /**
     * @param listener Set this loginListener to listen to overall Account creation process. That includes a callback after all the
     * tasks have finished successfully; a callback in case a task fails any is intentionally made to cancel by the caller
     * and a callback for performing any activity before the start of Login process. NOTE: This activity shall be performed in
     * a background thread
     * */
    public void setLoginListener(LoginListener listener) {
        //If a task is queued after finishup; task processor will throw and exception but we have
        // to ensure that after shutdown, no more calls to service are serviced
        if(serviceState == Service.SERVICE_SHUTDOWN_REQUESTED){
            throw new IllegalStateException("You have requested for a service shutdown You can't queue any more requests." +
                    "To queue more request, load the service again");
        }
        loginListener = listener;

        TaskProcessor.TaskProcessorListener taskListener;

        if(loginListener != null){
            //Translate LoginListener to TaskListener
            taskListener = new TaskProcessor.TaskProcessorListener() {
                @Override
                public void onTaskProcessingComplete() {
                    loginListener.onLogin(loginEventResponse);
                }

                @Override
                public void onTaskProcessingFailed() {
                    loginListener.onLoginFailed(loginEventResponse);
                }

                @Override
                public void onPreTaskProcessingStart() {
                    loginListener.onPreLogin();
                }

                @Override
                public void onCleanupComplete() {
                    synchronized (LoginService.this){
                        hasFinishedUp = true;
                        //Wake up the waiting thread only if IT IS WAITING ;)
                        //If its not waiting then it won't because of the finishup flag set here
                        if(shutDownThread != null && shutDownThread.isAlive() && shutDownThread.getState() == Thread.State.WAITING){
                            synchronized (finishUpLock){
                                finishUpLock.notify();
                            }
                        }
                        if(refreshThread != null && refreshThread.isAlive() && refreshThread.getState() == Thread.State.WAITING){
                            synchronized (refreshLock){
                                refreshLock.notify();
                            }
                        }
                    }
                    //Call the service state callback
                    if(serviceStateListener != null){
                        serviceStateListener.onServiceFinishUp();
                    }
                }
            };
        }
        else{
            //Set the default listener if not already set
            if(defaultTaskProcessorListener != null){
                defaultTaskProcessorListener = new TaskProcessorListenerImpl();
            }
            taskListener = defaultTaskProcessorListener;
        }

        taskProcessor.setOverallTaskProcessorListener(taskListener);
    }

    public void setUIFlag(int notifyUIFlag) {
        taskProcessor.setUIFlag(notifyUIFlag);
    }

    /**Provide your own implementation of an Retry Alert Dialog.*/
    public void setLoginRetryAlertDialog(AlertDialog taskProcessingRetry) {
        taskProcessor.setTaskProcessingRetryDialog(taskProcessingRetry);
    }

    /**This method can be invoked in case the current task in execution has failed. Users need not call ths method
     * in case of NOTIFY_UI_AUTO mode because the Retry and Progress dialogs are internally handled in this mode
     * and task automatically retries as per the user's response to those dialogs.
     *
     * Call this method ONLY if you are using the NOTIFY_UI_CUSTOM mode; Calling this method in NOTIFY_UI_AUTO mode
     * has NO EFFECT*/
    public void retryOnFailedLogin() {
        taskProcessor.retryOnFailedTask();
    }

    /**Provide your own implementation of a Progress Dialog.*/
    public void setLoginProgressDialog(ProgressDialog taskProcessingProgressDialog) {
        taskProcessor.setTaskProcessingProgressDialog(taskProcessingProgressDialog);
    }

    /**This method initiates the processing of tasks
     * @param tasks LoginTask that will perform the Login*/
    public void startLoginProcess(AbstractTask... tasks) {
        //It is possible that this method was called without setting a listener; Set the default one
        // in that case with the default flag(loginUIFlag) if not provided
        if(loginListener == null){
            if(defaultTaskProcessorListener != null){
                defaultTaskProcessorListener = new TaskProcessorListenerImpl();
                taskProcessor.setOverallTaskProcessorListener(defaultTaskProcessorListener);
            }
        }
        taskProcessor.startTasksProcessing(tasks);
    }
    //---------------------------------------------------------------------------------------------------------------------
    /**This listener enables listening to the States of service namely - onServiceCreate(),
     * onServiceFinishUp() and onServiceShutdown().
     * It is intended to be set by the one working closely with the states of server, for example, AccountManager.
     * For this reason, it has not been publicly exposed.*/
    void setServiceStateListener(Service.ServiceStateListener listener){
        serviceStateListener = listener;
    }

    //---------------------------------------------------------------------------------------------------------------------
    /*
    @Subscribe
    public void onLoginResponseFromServer(LoginEventResponse event){
        Log.d(TAG, "Login event response received by LoginService");
        this.loginEventResponse = event;
        //TODO: Perform task on receiving response from server
    }
    */

    //---------------------------------------------------------------------------------------------------------------------
    /**
     * This interface shall be implemented by anyone wanting to get a window into the overall task processing states
     * and perform tasks deemed necessary by them at those points.
     */
    public interface LoginListener {

        void onLogin(LoginEventResponse loginEventResponse);
        void onLoginFailed(LoginEventResponse loginEventResponse);
        void onPreLogin();
        void onLoginServiceRefresh();
    }

    //---------------------------------------------------------------------------------------------------------------------
    private class TaskProcessorListenerImpl implements TaskProcessor.TaskProcessorListener{
        @Override
        public void onTaskProcessingComplete() {

        }

        @Override
        public void onTaskProcessingFailed() {

        }

        @Override
        public void onPreTaskProcessingStart() {

        }

        @Override
        public void onCleanupComplete() {
            synchronized (LoginService.this){
                hasFinishedUp = true;
                //Wake up the waiting thread only if IT IS WAITING ;)
                //If its not waiting then it won't because of the finishup flag set here
                if(shutDownThread != null && shutDownThread.isAlive() && shutDownThread.getState() == Thread.State.WAITING){
                    synchronized (finishUpLock){
                        finishUpLock.notify();
                    }
                }
                if(refreshThread != null && refreshThread.isAlive() && refreshThread.getState() == Thread.State.WAITING){
                    synchronized (refreshLock){
                        refreshLock.notify();
                    }
                }
            }
            //Call the service state callback
            if(serviceStateListener != null){
                serviceStateListener.onServiceFinishUp();
            }
        }
    }

    //---------------------------------------------------------------------------------------------------------------------

    /**This class simply encapsulates the login details*/
    public static class LoginDetails{

        private String emailID;
        private String password;

        public LoginDetails(@NonNull String emailID, @NonNull String password) {

            if(emailID == null){
                throw new NullPointerException("Email ID can't be null");
            }
            if(password == null){
                throw new NullPointerException("Password can't be null");
            }
            this.emailID = emailID;
            this.password = password;
        }

        public String getEmailID() {
            return emailID;
        }

        public void setEmailID(String emailID) {
            if(emailID == null){
                throw new NullPointerException("Email ID can't be null");
            }
            this.emailID = emailID;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            if(password == null){
                throw new NullPointerException("Password can't be null");
            }
            this.password = password;
        }
    }

    //---------------------------------------------------------------------------------------------------------------------

    public static class LoginEventResponse {
        //In case there is a successful login
        private GenericLearnerProfileVer1 profileFromServer;
        //In case there is an unsuccessful attempt
        private IOException exception;

        public LoginEventResponse(GenericLearnerProfileVer1 profileFromServer) {
            this.profileFromServer = profileFromServer;
        }

        public LoginEventResponse(IOException exception) {
            this.exception = exception;
        }

        public GenericLearnerProfileVer1 getProfileResponse() {
            return profileFromServer;
        }

        public void setProfileResponse(GenericLearnerProfileVer1 profileFromServer) {
            this.profileFromServer = profileFromServer;
        }

        public IOException getException() {
            return exception;
        }

        public void setException(IOException exception) {
            this.exception = exception;
        }
    }
}
