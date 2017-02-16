package com.learncity.generic.learner.account.create.ver0;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by DJ on 2/6/2017.
 */

public class GAEAccountCreationHandlerThread extends HandlerThread {

    private Handler handler;

    public GAEAccountCreationHandlerThread(String threadName){
        super(threadName);
    }

    public void execute(Runnable r){
        handler.post(r);
    }
    public Handler getHandlerInstance(){
        return handler;
    }

    public void prepareHandler(){
        if(handler == null){
            handler = new Handler(getLooper());
        }
    }
}