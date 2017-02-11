package com.learncity.generic.learner.account.create;

/**
 * Created by DJ on 2/5/2017.
 */

public interface AccountCreationClient {
    void prepareClient();
    void sendRequest();
    void performCleanup();

    /**
     * Created by DJ on 2/6/2017.
     */

    interface AccountCreationClientListener {

        void onClientPrepared();
        void onRequestSuccessful();
        void onRequestFailed();
    }
}
