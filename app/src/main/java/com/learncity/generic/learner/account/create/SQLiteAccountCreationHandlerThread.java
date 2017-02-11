package com.learncity.generic.learner.account.create;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by DJ on 2/6/2017.
 */

public class SQLiteAccountCreationHandlerThread extends HandlerThread {

    private Handler handler;

    public SQLiteAccountCreationHandlerThread(String threadName){
        super(threadName);
    }
    public void execute(Runnable r){
        handler.post(r);
    }
    public void prepareHandler(){
        if(handler == null){
            handler = new Handler(getLooper());
        }
    }
}
