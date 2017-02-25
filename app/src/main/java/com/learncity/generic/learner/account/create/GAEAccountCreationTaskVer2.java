package com.learncity.generic.learner.account.create;

import android.util.Log;

import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learner.account.profile.model.LearnerProfile;
import com.learncity.tutor.account.profile.model.TutorProfile;
import com.learncity.util.account_management.AbstractTask;
import com.learncity.util.account_management.Result;

import static com.learncity.util.account_management.AccountCreationService.ACCOUNT_CREATION_COMPLETED;
import static com.learncity.util.account_management.AccountCreationService.ACCOUNT_CREATION_FAILED;

/**
 * Created by DJ on 2/15/2017.
 */

public class GAEAccountCreationTaskVer2 extends AbstractTask<Void> {

    private static final String TAG = "GAEACCreationTask2";

    private AccountCreationClient accountCreationClient;

    private GenericLearnerProfile profile;

    private boolean requestProcessingComplete = false;
    private final Object requestLock = new Object();

    private long taskThreadId = Thread.currentThread().getId();

    private Result<Void> result;

    public GAEAccountCreationTaskVer2(GenericLearnerProfile profile){
        this.profile = profile;
    }

    /**
     * Override this method to carry out the task.
     *
     * @return Returns the task completion status out of the following codes:
     * LoginService.{ACCOUNT_CREATION_FAILED, ACCOUNT_CREATION_COMPLETED}
     */
    @Override
    public Result<Void> performTask() {

        Log.d(TAG, "GAEAccountCreationTask.performTask(): " + "\n" + "MESSAGE: Performing AC creation..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        selectClient(profile);
        //Prepare the client
        Log.d(TAG, "GAEAccountCreationTask.performTask(): " + "\n" + "MESSAGE: Preparing client..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        accountCreationClient.prepareClient();

        //Send the Account creation request
        Log.d(TAG, "GAEAccountCreationTask.performTask(): " + "\n" + "MESSAGE: Sending AC creation request..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        accountCreationClient.sendRequest();

        //Now, it is quite possible that the client can do the work in some background thread
        //We shall wait in that case for the task to finish up
        if(requestProcessingComplete){
            //Processing complete; no need to wait up
            return result;
        }
        else{
            while(!requestProcessingComplete){
                synchronized (requestLock){
                    try{
                        requestLock.wait();
                    }
                    catch(InterruptedException ie){
                        //Think of something
                    }
                }
            }
        }

        return result;
    }

    @Override
    public void performCleanup() {
        Log.d(TAG, "GAEAccountCreationTask.performCleanup(): " + "\n" + "MESSAGE: Cleaning up GAEAccountCreationTask..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        accountCreationClient.performCleanup();

        profile = null;
        taskListener = null;
        accountCreationClient = null;
        result = null;
    }

    private void selectClient(GenericLearnerProfile profile){
        if(profile instanceof TutorProfile){
            GAETutorAccountCreationClient client = new GAETutorAccountCreationClient((TutorProfile)profile);
            client.setClientListener(new AccountCreationClient.AccountCreationClientListener() {
                @Override
                public void onClientPrepared() {
                    Log.d(TAG, "GAETutorAccountCreationClient.onClientPrepared(): " + "\n" + "MESSAGE: Client prepared!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                }

                @Override
                public void onRequestSuccessful() {
                    Log.d(TAG, "GAETutorAccountCreationClient.onRequestSuccessful(): " + "\n" + "MESSAGE: Account creation request successfully sent!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());

                    //Request has been successful. Set the result
                    result = Result.RESULT_SUCCESS;
                    requestProcessingComplete = true;
                    notifyTaskIfWaiting();
                }

                @Override
                public void onRequestFailed() {
                    Log.e(TAG, "GAETutorAccountCreationClient.onRequestFailed(): " + "\n" + "MESSAGE: Account creation request failed!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());

                    result = Result.RESULT_FAILURE;
                    requestProcessingComplete = true;
                    notifyTaskIfWaiting();
                }
            });
            accountCreationClient = client;
        }
        else{
            GAELearnerAccountCreationClient client = new GAELearnerAccountCreationClient((LearnerProfile) profile);
            client.setClientListener(new AccountCreationClient.AccountCreationClientListener() {
                @Override
                public void onClientPrepared() {
                    Log.d(TAG, "GAELearnerAccountCreationClient.onClientPrepared(): " + "\n" + "MESSAGE: Client prepared!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                }

                @Override
                public void onRequestSuccessful() {
                    Log.d(TAG, "GAELearnerAccountCreationClient.onRequestSuccessful(): " + "\n" + "MESSAGE: Account creation request successfully sent!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    //Request has been successful. Set the result
                    result = Result.RESULT_SUCCESS;
                    requestProcessingComplete = true;
                    notifyTaskIfWaiting();
                }

                @Override
                public void onRequestFailed() {
                    Log.e(TAG, "GAELearnerAccountCreationClient.onRequestFailed(): " + "\n" + "MESSAGE: Account creation request failed!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    result = Result.RESULT_FAILURE;
                    requestProcessingComplete = true;
                    notifyTaskIfWaiting();
                }
            });
            accountCreationClient = client;
        }

    }

    private void notifyTaskIfWaiting(){
        //Wake the thread up
        if(Thread.currentThread().getId() == taskThreadId){
            //Same thread. No BS.
            return;
        }
        else{
            synchronized(requestLock){
                requestLock.notify();
            }
        }
    }
}
