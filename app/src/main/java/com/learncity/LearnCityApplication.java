package com.learncity;

import android.app.Application;

import com.learncity.learncity.BuildConfig;

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
    }
}
