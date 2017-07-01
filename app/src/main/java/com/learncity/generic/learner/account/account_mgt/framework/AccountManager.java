package com.learncity.generic.learner.account.account_mgt.framework;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.learncity.generic.learner.account.profile.database.ProfileDbHelperVer1;
import com.learncity.generic.learner.account.profile.model.GenericLearnerProfile;
import com.learncity.util.account_management.Service;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import static com.learncity.generic.learner.account.profile.database.ProfileDbHelperVer1.DATABASE_NAME;

/**
 * Created by DJ on 2/16/2017.
 */

public class AccountManager {

    private final static String TAG = "AccountManager";

    /**Constants for invoking different Account services*/
    public static final int ACCOUNT_CREATION_SERVICE = 0x01;
    public static final int LOGIN_SERVICE = 0x02;

    private static AccountManager accountManager;

    //private Context context;

    private static LoginService loginService;
    private static AccountCreationService accountCreationService;

    private final Object shutDownLock = new Object();
    private boolean isLoginServiceShutdownComplete = false;
    private boolean isACCreationServiceShutdownComplete = false;

    //This profile shall be linked to either the profile from new AC creation/Login response/Local Db
    private static GenericLearnerProfile profile;

    /**Every user has a device token corresponding to the current App instance(on the device).
     * This token is for use by Firebase Connection server to send the message to the
     * device.*/
    public static String userDeviceFirebaseToken;

    private static ProfileDbHelperVer1 profileDbHelperVer1;

    private Thread shutDownThread;

    /**Shall hold reference to the an Anon instance of Object that shall receive result of the
     * AC creation rather than the AccountManager directly. This workaround is to have the result
     * available without an instance of AccountManager*/
    private static ACCreationResultReceiver sACCreationResultReceiver;

    private AccountManager(Context context) {
        //this.context = context;
        //Register with EventBus
        //EventBus.getDefault().register(this);
    }

    public static AccountManager getAccountManager(@NonNull Context context){

        if(context == null){
            throw new NullPointerException("Context can't be null");
        }

        profile = isExistingAccountLocally(context);

        if(accountManager == null){
            return accountManager = new AccountManager(context);
        }
        else{
            //Refresh the context
            //accountManager.context = context;
        }
        return accountManager;
    }

    /**Invoke this method when done with the AccountManager for good. This will shutdown any service. AccountManager
     * instance stays alive with this call;*/
    public void shutDown(){
        //Shutdown the services
        if(loginService != null){
            synchronized (loginService){
                if(loginService.serviceState != Service.SERVICE_SHUTDOWN_REQUESTED){
                    loginService.shutDown();
                }
            }
        }
        else{
            isLoginServiceShutdownComplete = true;
        }
        if(accountCreationService != null){
            synchronized (accountCreationService){
                if(accountCreationService.serviceState != Service.SERVICE_SHUTDOWN_REQUESTED){
                    accountCreationService.shutDown();
                }
            }
        }
        else{
            isACCreationServiceShutdownComplete = true;
        }
        //Lets wait without suspending the main thread
        shutDownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isACCreationServiceShutdownComplete && !isLoginServiceShutdownComplete){
                    synchronized (shutDownLock){
                        try{
                            shutDownLock.wait();
                        }
                        catch(InterruptedException ie){
                            //Think of something
                        }
                    }
                    //Both the services have shutdown; clear the rest
                    accountCreationService = null;
                    loginService = null;

                    //Unregister with EventBus
                    //EventBus.getDefault().unregister(this);
                    if(sACCreationResultReceiver != null){
                        sACCreationResultReceiver.unregisterReceiver();
                        sACCreationResultReceiver = null;
                    }

                    shutDownThread = null;

                    accountManager = null;
                }
            }
        });
        shutDownThread.start();

        //context = null;
    }

    /**Call this method to load the given service instance. The service can be loaded if you anticipating
     * its need in near future but not quite yet - Loading can reduce the overall "load & fetch" time if
     * done at a time when the user doesn't engage in any activity(CPU is relatively free)*/
    public static void loadService(Context context, int serviceId){
        switch(serviceId){
            case ACCOUNT_CREATION_SERVICE:
                if(accountCreationService == null){
                    accountCreationService = new AccountCreationService(context);
                }
                break;
            case LOGIN_SERVICE:
                if(loginService == null){
                    loginService = new LoginService(context);
                }
                break;
        }
    }

    /**Fetch the service handle through this method. If the service hasn't been loaded already then it
     * shall be loaded first.*/
    public static <T> T fetchService(Context context, int serviceId){

        T service;

        switch(serviceId){
            case ACCOUNT_CREATION_SERVICE:
                if(accountCreationService == null){
                    accountCreationService = new AccountCreationService(context);
                }
                //Refresh the context while we are at it
                accountCreationService.refreshContext(context);

                accountCreationService.setServiceStateListener(new ServiceStateListener());
                service = (T)accountCreationService;

                //Set the AC creation result receiver
                sACCreationResultReceiver = new ACCreationResultReceiver();

                break;
            case LOGIN_SERVICE:
                if(loginService == null){
                    loginService = new LoginService(context);
                }
                loginService.refreshContext(context);
                loginService.setServiceStateListener(new ServiceStateListener());
                service = (T)loginService;
                break;
            default:
                throw new NoSuchServiceException("There is no such service. Check the list of valid services");
        }

        return service;
    }

    //---------------------------------------------------------------------------------------------------------------------

    private static class ServiceStateListener implements Service.ServiceStateListener{
        @Override
        public void onServiceRefresh() {

        }

        @Override
        public void onServiceFinishUp() {

        }

        @Override
        public void onServiceShutdown() {
            accountManager.isLoginServiceShutdownComplete = true;
            if(accountManager.isACCreationServiceShutdownComplete){
                if(accountManager.shutDownThread != null &&
                        accountManager.shutDownThread.isAlive() &&
                        accountManager.shutDownThread.getState() == Thread.State.WAITING){
                    synchronized (accountManager.shutDownLock){
                        accountManager.shutDownLock.notify();
                    }
                }
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------------------
    public static boolean isExistingDbOnThisDevice(Context context) {
        File dbPath = ProfileDbHelperVer1.isExistingDatabase(context, DATABASE_NAME);
        if(dbPath == null){
            return false;
        }
        else{
            return true;
        }
    }

    public static GenericLearnerProfile isExistingAccountLocally(Context context){
        if(profileDbHelperVer1 == null){
            profileDbHelperVer1 = new ProfileDbHelperVer1(context);
        }
        //Proactively preparing the profile if there is an existing AC on ths device
        if(isExistingDbOnThisDevice(context)){
            //Stash the profile
            if(profile == null){
                profile = profileDbHelperVer1.isExistingUserAccount();
            }
        }
        return profile;
    }

    public static GenericLearnerProfile getAccountDetails(Context context){
        //Check if an Account exists locally in case it has not been checked for already
        if(profile == null){
            profile = isExistingAccountLocally(context);
        }
        return profile;
    }


    public static class NoSuchServiceException extends RuntimeException{
        public NoSuchServiceException(String msg){
            super(msg);
        }
    }

    //----------------------------------------------------------------------------------------------------------------------
    private static class ACCreationResultReceiver {

        public ACCreationResultReceiver(){
            //Register with EventBus
            EventBus.getDefault().register(this);
        }

        @Subscribe
        public void onReceiveProfileOnACCreation(AccountCreationService.ACCreationResult accountCreationFinalResult){
            Log.i(TAG, "Profile received from the successful AC creation process");
            profile = (GenericLearnerProfile) accountCreationFinalResult.getResponseData()[0];
        }

        public void unregisterReceiver(){
            //Unregister with EventBus
            EventBus.getDefault().unregister(this);
        }
    }
}
