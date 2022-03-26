package com.f5.onepageresumebe.util;

public class UserUtil {

    public static String convertUserEmail(String userEmail, boolean isKakao){

        if(isKakao){
            return userEmail.substring(5);
        }else return userEmail;
    }
}
