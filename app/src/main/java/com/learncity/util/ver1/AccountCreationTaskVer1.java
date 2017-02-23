package com.learncity.util.ver1;

/**
 * Created by DJ on 2/6/2017.
 */

public interface AccountCreationTaskVer1 {

    /**Account creation status indicators*/
    int ACCOUNT_CREATION_FAILED = 0x0022;
    int ACCOUNT_CREATION_COMPLETED = 0x0023;

    /**Override this method to perform any setup tasks deemed necessary*/
    void initializeTask();

    /**Override this method to carry out the Account creation process.
     * @return Returns the Account creation status out of the following codes:
     * {ACCOUNT_CREATION_FAILED, ACCOUNT_CREATION_COMPLETED}*/
    int performAccountCreation();

    /**Override this method to perform any cleanup activities with the AC creation process*/
    void performCleanup();

    /**
     * Created by DJ on 2/5/2017.
     */
    interface AccountCreationTaskListener {
        void onAccountCreated();
        void onAccountCreationFailed();
    }
}
