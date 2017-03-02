package com.learncity;

import android.app.Application;
import android.util.Log;

import com.learncity.generic.learner.account.profile.database.ProfileDbHelperVer1;
import com.learncity.learncity.BuildConfig;
import com.learncity.util.account_management.impl.AccountManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by DJ on 2/8/2017.
 */

public class LearnCityApplication extends Application {

    private static final String TAG = "LearnCityApplication";

    private final boolean DEBUG = true;

    @Override
    public void onCreate(){
        super.onCreate();
        //Configuring the EventBus to be able to rethrow exceptions thrown by the subscribers
        EventBus.builder().throwSubscriberException(BuildConfig.DEBUG).installDefaultEventBus();

        if(DEBUG){
            //Delete the existing Db if there is one
            if(AccountManager.isExistingDbOnThisDevice(this)){
                deleteDatabase(ProfileDbHelperVer1.DATABASE_NAME);
            }
        }
        //Load the AccountManager
        AccountManager manager = AccountManager.getAccountManager(this);
        //Check if there is an existing Account on this device. If there is an existing one,
        //prepare it either here or in the launch Activity. If there isn't an existing AC on
        //the device, load the AC creation as well as login service
        if(manager.isExistingAccountLocally(this) == null){
            AccountManager.loadService(this, AccountManager.ACCOUNT_CREATION_SERVICE);
            AccountManager.loadService(this, AccountManager.LOGIN_SERVICE);
        }
        else{
            //Account prepared by AccountManager
            Log.d(TAG, "Account already existing on this device");
        }
        //Note: The context has to be revised to the Activity for the service to work
    }
}
