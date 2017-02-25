package com.learncity.util.account_management;

import static com.learncity.util.account_management.Task.TASK_COMPLETED;
import static com.learncity.util.account_management.Task.TASK_FAILED;

/**
 * Created by DJ on 2/24/2017.
 */

public class Result<T> {

    public static final Result<Void> RESULT_SUCCESS = new Result<Void>(TASK_COMPLETED);
    public static final Result<Void> RESULT_FAILURE = new Result<Void>(TASK_FAILED);

    /**Represents any response data or any general data that you want to stash*/
    private T[] responseData;
    /**This is indicative of the Success or Failure of the task returning result*/
    private int resultCode;

    public Result(int resultCode) {
        this.resultCode = resultCode;
    }

    public Result(int resultCode, T... responseData) {
        this.responseData = responseData;
        this.resultCode = resultCode;
    }

    public T[] getResponseData() {
        return responseData;
    }

    public void setResponseData(T... responseData) {
        this.responseData = responseData;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultCodeSuccess(){
        resultCode = TASK_COMPLETED;
    }
    public void setResultCodeFailure(){
        resultCode = TASK_FAILED;
    }
}
