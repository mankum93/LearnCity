package com.learncity.generic.learner.account.create.ver0;

import android.util.Log;

import com.learncity.util.account_management.AccountCreationClient;
import com.learncity.generic.learner.account.account_mgt.framework.GAELearnerAccountCreationClient;
import com.learncity.generic.learner.account.account_mgt.framework.GAETutorAccountCreationClient;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.learner.account.profile.model.LearnerProfile;
import com.learncity.tutor.account.profile.model.TutorProfile;
import com.learncity.util.ver0.AccountCreationTask;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by DJ on 2/6/2017.
 */

public class GAEAccountCreationTask implements AccountCreationTask {

    private static final String TAG = "GAEACCreationTask";

    private AccountCreationClient accountCreationClient;

    private GAEAccountCreationHandlerThread handlerThread;

    private GenericLearnerProfile profile;

    private AccountCreationTaskListener listener;

    public GAEAccountCreationTask(GenericLearnerProfile profile){
        this.profile = profile;
    }
    private void initializeTask() {
        //HandlerThread instance can remain in memory for a while in case we want to reuse it
        if(handlerThread == null){
            handlerThread = new GAEAccountCreationHandlerThread("GAEAccountCreationTask");
        }
        Log.d(TAG, "GAEAccountCreationTask.initializeTask(): " + "\n" + "MESSAGE: GAEAccountCreationTask initialized" +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
    }

    @Override
    public void performAccountCreation() {
        Log.d(TAG, "GAEAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Performing AC creation..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        initializeTask();
        if(!handlerThread.isAlive()){
            handlerThread.start();
        }
        handlerThread.prepareHandler();

        //Initialize the client
        handlerThread.execute(new Runnable() {
            @Override
            public void run() {
                if(accountCreationClient == null){
                    selectClient(profile);
                }
            }
        });
        //Prepare the client
        handlerThread.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "GAEAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Preparing client..." +
                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                accountCreationClient.prepareClient();
            }
        });
        //Send the Account creation request
        handlerThread.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "GAEAccountCreationTask.performAccountCreation(): " + "\n" + "MESSAGE: Sending AC creation request..." +
                        "\n" +"Thread ID: " + Thread.currentThread().getId());
                accountCreationClient.sendRequest();
            }
        });
    }

    @Override
    public void cancelAccountCreation() {

    }

    @Override
    public void performCleanup() {
        Log.d(TAG, "GAEAccountCreationTask.performCleanup(): " + "\n" + "MESSAGE: Cleaning up GAEAccountCreationTask..." +
                "\n" +"Thread ID: " + Thread.currentThread().getId());
        //Remove the Sticky Handler posted before
        if(handlerThread != null){
            EventBus.getDefault().removeStickyEvent(handlerThread.getHandlerInstance());
            handlerThread.quit();
        }
        accountCreationClient.performCleanup();

        profile = null;
        listener = null;
        accountCreationClient = null;
    }

    public void setAccountCreationListener(AccountCreationTaskListener listener) {
        this.listener = listener;
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
                    //Request has been successful. Chances are that now we will go for some other task in series
                    //Time to post the Handler for Thread continual use
                    EventBus.getDefault().postSticky(handlerThread.getHandlerInstance());
                    listener.onAccountCreated();
                }

                @Override
                public void onRequestFailed() {
                    Log.e(TAG, "GAETutorAccountCreationClient.onRequestFailed(): " + "\n" + "MESSAGE: Account creation request failed!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    listener.onAccountCreationFailed();
                }
            });
            accountCreationClient = client;
        }
        else{
            GAELearnerAccountCreationClient client = new GAELearnerAccountCreationClient((LearnerProfile)profile);
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
                    //Request has been successful. Chances are that now we will go for some other task in series
                    //Time to post the Handler for Thread continual use
                    EventBus.getDefault().postSticky(handlerThread.getHandlerInstance());
                    listener.onAccountCreated();
                }

                @Override
                public void onRequestFailed() {
                    Log.e(TAG, "GAELearnerAccountCreationClient.onRequestFailed(): " + "\n" + "MESSAGE: Account creation request failed!!!" +
                            "\n" +"Thread ID: " + Thread.currentThread().getId());
                    listener.onAccountCreationFailed();
                }
            });
            accountCreationClient = client;
        }

    }
}
