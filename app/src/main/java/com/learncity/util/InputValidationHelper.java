package com.learncity.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DJ on 5/22/2017.
 */

/**
 * A helper class for validating text inputs in forms.
 * Adapted more or less, as it is, from <a href="http://stackoverflow.com/questions/33072569/best-practice-input-validation-android">here.</a>
 * The author is @PinoyCoder
 */
public final class InputValidationHelper {

    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final String PWD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
    public static final String PWD_ERROR_TEXT = "Password should at least include,\n one uppercase letter,\n "
            + "one lowercase letter,\n one digit, \n one special characters from, {'@' ,'#', '$', '+', '='}";

    public static boolean isValidEmail(String emailString) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(emailString);
        return matcher.matches();
    }

    public static boolean isValidPassword(String pwdString) {

        Pattern pattern = Pattern.compile(PWD_PATTERN);
        Matcher matcher = pattern.matcher(pwdString);
        return matcher.matches();
    }

    public static boolean isNullOrEmpty(String string) {
        return TextUtils.isEmpty(string);
    }

    public static boolean isNumeric(String string) {
        return TextUtils.isDigitsOnly(string);
    }

    public static boolean isValidIndianMobileNo(String phoneNo){
        // Phone No should be 10 digits exactly(Indian mobile numbers)
        if(phoneNo.length()!=10){
            return false;
        }
        // Phone number can only start with 6, 7, 8 or 9
        char firstChar = phoneNo.charAt(0);
        if(firstChar != '6' && firstChar != '7' && firstChar != '8' && firstChar != '9'){
            return false;
        }
        return true;
    }

    //Add more validators here if necessary
}
