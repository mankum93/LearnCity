package com.learncity.util.ver1;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by DJ on 2/6/2017.
 */

public class AccountCreationWorkerThreadVer1 extends HandlerThread {

    private Handler handler;

    public AccountCreationWorkerThreadVer1(String threadName){
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