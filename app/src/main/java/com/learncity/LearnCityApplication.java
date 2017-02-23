package com.learncity;

import android.app.Application;

import com.learncity.learncity.BuildConfig;
import com.learncity.util.account_management.AccountManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by DJ on 2/8/2017.
 */

public class LearnCityApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        //Configuring the EventBus to be able to rethrow exceptions thrown by the subscribers
        EventBus.builder().throwSubscriberException(BuildConfig.DEBUG).installDefaultEventBus();

        //Load the AccountManager
        AccountManager.getAccountManager(this);
        //Check if there is an existing Account on this device. If there is an existing one,
        //prepare it either here or in the launch Activity. If there isn't an existing AC on
        //the device, load the AC creation manager as it is more likely that it is a new user
        if(!AccountManager.isAccountAlreadyExistingOnThisDevice(this)){
            AccountManager.loadService(this, AccountManager.ACCOUNT_CREATION_SERVICE);
        }
        //The context has to be revised to the Activity for the service to work
    }
}
