package com.learncity.util.account_management;

/**
 * Created by DJ on 2/21/2017.
 */

public interface Service {

    int SERVICE_READY = 0x20;
    int SERVICE_FINISHUP_REQUESTED = 0x21;
    int SERVICE_SHUTDOWN_REQUESTED = 0x22;

    interface ServiceStateListener{
        void onServiceRefresh();
        void onServiceFinishUp();
        void onServiceShutdown();
    }
}
