package com.learncity.util.account_management;

import android.content.Context;
import android.support.annotation.NonNull;

import com.learncity.generic.learner.account.profile.database.ProfileDbHelperVer1;

/**
 * Created by DJ on 2/16/2017.
 */

public class AccountManager {

    /**Constants for invoking different Account services*/
    public static final int ACCOUNT_CREATION_SERVICE = 0x01;
    public static final int LOGIN_SERVICE = 0x02;

    private static AccountManager accountManager;

    private Context context;

    private static LoginService loginService;
    private static AccountCreationService accountCreationService;

    private final Object shutDownLock = new Object();
    private boolean isLoginServiceShutdownComplete = false;
    private boolean isACCreationServiceShutdownComplete = false;

    private Thread shutDownThread;

    private AccountManager(Context context) {
        this.context = context;
    }

    public static AccountManager getAccountManager(@NonNull Context context){

        if(context == null){
            throw new NullPointerException("Context can't be null");
        }

        if(accountManager == null){
            return accountManager = new AccountManager(context);
        }
        else{
            //Refresh the context
            accountManager.context = context;
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

                    shutDownThread = null;

                    accountManager = null;
                }
            }
        });
        shutDownThread.start();

        context = null;
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
    public static boolean isAccountAlreadyExistingOnThisDevice(Context context) {
        return ProfileDbHelperVer1.isExistingUserAccount(context);
    }


    public static class NoSuchServiceException extends RuntimeException{
        public NoSuchServiceException(String msg){
            super(msg);
        }
    }
}
