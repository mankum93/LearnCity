package com.learncity.backend.common.account.login;

public class LoginDetails{

    private String emailID;
    private String password;

    public LoginDetails(String emailID, String password) {
        this.emailID = emailID;
        this.password = password;
    }

    //Default constructor for serialization
    public LoginDetails(){

    }

    public String getEmailID() {
        return emailID;
    }

    public String getPassword() {
        return password;
    }
}