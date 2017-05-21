package com.learncity;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.learncity.generic.learner.account.profile.database.ProfileDbHelperVer1;
import com.learncity.learncity.BuildConfig;
import com.learncity.learner.search.SearchResultsActivity;
import com.learncity.util.account_management.impl.AccountManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by DJ on 2/8/2017.
 */

public class LearnCityApplication extends MultiDexApplication {

    /**Root URL of App Engine backend for the Application.*/
    // TODO: Stash this URL at some more appropriate location like a place for connection/network info
    public static final String BACKEND_ROOT_URL_LIVE = "https://1-dot-unified-surfer-147104.appspot.com/_ah/api/";
    public static final String BACKEND_ROOT_URL_LOCALHOST = "http://10.0.2.2:8080/_ah/api/";
    //public static final String BACKEND_ROOT_URL = BACKEND_ROOT_URL_LIVE;
    public static final String BACKEND_ROOT_URL = BACKEND_ROOT_URL_LOCALHOST;

    private static final String TAG = "LearnCityApplication";

    private final boolean DEBUG = true;

    @Override
    public void onCreate(){
        super.onCreate();

        // Instantiating a singleton instance of EverNote JobManager
        JobManager.create(this).addJobCreator(new SearchResultsActivity.TutoringRequestForwardingJobCreator());

        // Configuring the EventBus to be able to rethrow exceptions thrown by the subscribers
        EventBus.builder().throwSubscriberException(BuildConfig.DEBUG).installDefaultEventBus();

        if(DEBUG){
            // Delete the existing Db if there is one
            if(AccountManager.isExistingDbOnThisDevice(this)){
                deleteDatabase(ProfileDbHelperVer1.DATABASE_NAME);
            }
        }
        // Load the AccountManager
        AccountManager manager = AccountManager.getAccountManager(this);
        // Check if there is an existing Account on this device. If there is an existing one,
        // prepare it either here or in the launch Activity. If there isn't an existing AC on
        // the device, load the AC creation as well as login service
        if(manager.isExistingAccountLocally(this) == null){
            AccountManager.loadService(this, AccountManager.ACCOUNT_CREATION_SERVICE);
            AccountManager.loadService(this, AccountManager.LOGIN_SERVICE);
        }
        else{
            // Account prepared by AccountManager
            Log.d(TAG, "Account already existing on this device");
        }
        // Note: The context has to be revised to the Activity for the service to work
    }
}
