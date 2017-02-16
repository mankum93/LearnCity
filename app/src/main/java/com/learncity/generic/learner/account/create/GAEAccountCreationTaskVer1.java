package com.learncity.generic.learner.account.create;

import android.util.Log;

import com.learncity.generic.learner.account.profile.model.GenericLearnerProfileParcelableVer1;
import com.learncity.tutor.account.profile.model.TutorProfileParcelableVer1;

/**
 * Created by DJ on 2/15/2017.
 */

public class GAEAccountCreationTaskVer1 extends BaseAccountCreationTask {

    private static final String TAG = "GAEACCreationTask1";

    private AccountCreationClient accountCreationClient;

    private GenericLearnerProfileParcelableVer1 profile;

    private boolean requestProcessingComplete = false;
    private final Object requestLock = new Object();

    private long taskThreadId = Thread.currentThread().getId();

    private int returnCode;

    public GAEAccountCreationTaskVer1(GenericLearnerProfileParcelableVer1 profile){
        this.profile = profile;
    }

    @Override
    public int performAccountCreation() {

        Log.d(TAG, "GAEAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Performing AC creation..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        if(accountCreationClient == null){
            selectClient(profile);
        }
        //Prepare the client
        Log.d(TAG, "GAEAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Preparing client..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        accountCreationClient.prepareClient();

        //Send the Account creation request
        Log.d(TAG, "GAEAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Sending AC creation request..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        accountCreationClient.sendRequest();

        //Now, it is quite possible that the client can do the work in some background thread
        //We shall wait in that case for the task to finish up
        if(requestProcessingComplete){
            //Processing complete; no need to wait up
            return returnCode;
        }
        else{
            while(!requestProcessingComplete){
                Log.e(TAG, "HEHE");
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

        return returnCode;
    }

    @Override
    public void performCleanup() {
        Log.d(TAG, "GAEAccountCreationTask.performCleanup(): " + "\n" + "MESSAGE: Cleaning up GAEAccountCreationTask..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());

        accountCreationClient.performCleanup();

        profile = null;
        accountCreationTaskListener = null;
        accountCreationClient = null;
    }

    private void selectClient(GenericLearnerProfileParcelableVer1 profile){
        if(profile instanceof TutorProfileParcelableVer1){
            GAETutorAccountCreationClient client = new GAETutorAccountCreationClient((TutorProfileParcelableVer1)profile);
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

                    //Request has been successful. Ser the return code
                    returnCode = ACCOUNT_CREATION_COMPLETED;
                    requestProcessingComplete = true;
                    notifyTaskIfWaiting();
                }

                @Override
                public void onRequestFailed() {
                    Log.e(TAG, "GAETutorAccountCreationClient.onRequestFailed(): " + "\n" + "MESSAGE: Account creation request failed!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    returnCode = ACCOUNT_CREATION_FAILED;
                    requestProcessingComplete = true;
                    notifyTaskIfWaiting();
                }
            });
            accountCreationClient = client;
        }
        else{
            GAELearnerAccountCreationClient client = new GAELearnerAccountCreationClient(profile);
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
                    //Request has been successful. Set the return code
                    returnCode = ACCOUNT_CREATION_COMPLETED;
                    requestProcessingComplete = true;
                    notifyTaskIfWaiting();
                }

                @Override
                public void onRequestFailed() {
                    Log.e(TAG, "GAELearnerAccountCreationClient.onRequestFailed(): " + "\n" + "MESSAGE: Account creation request failed!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    returnCode = ACCOUNT_CREATION_FAILED;
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
