package com.backstagesupporters.fasttrack.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

public class PatternValidate {

    /**
     * https://stackoverflow.com/questions/22505336/email-and-phone-number-validation-in-android
     * https://www.mkyong.com/regular-expressions/how-to-validate-password-with-regular-expression/
     */
   public static String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static String expression = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(.{8,15})$";


    private boolean isValidMailPattern(String email) {
        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        return Pattern.compile(EMAIL_STRING).matcher(email).matches();
    }

    private boolean isValidMailPatternAndroid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidMobilePattern(String phone) {
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 6 && phone.length() <= 13;
        }
        return false;
    }

    private boolean isValidMobilePatternAndroid(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }


    //  API level 8 and above.
    private boolean isValidEmail(CharSequence email) {
        if (!TextUtils.isEmpty(email)) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
        return false;
    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

}
