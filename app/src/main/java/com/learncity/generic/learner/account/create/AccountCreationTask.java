package com.learncity.generic.learner.account.create;

/**
 * Created by DJ on 2/6/2017.
 */

public interface AccountCreationTask {

    void performAccountCreation();
    void cancelAccountCreation();
    void performCleanup();

    /**
     * Created by DJ on 2/5/2017.
     */

    interface AccountCreationTaskListener {
        void onAccountCreated();
        void onAccountCreationFailed();
    }
}
