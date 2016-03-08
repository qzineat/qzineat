package com.codepath.qzineat.utils;

import com.parse.ParseUser;

/**
 * Created by Shyam Rokde on 3/6/16.
 */

public class UserUtil {

    public static final int USER_LOG_IN_SUCCESS = 1;   // The request code
    public static final int USER_LOG_IN_FAILED = 2;    // The request code
    public static final int USER_LOG_IN_CANCEL = 3;    // The request code


    public static ParseUser getLoggedInUser(){
        return ParseUser.getCurrentUser();
    }

    public static boolean isUserLoggedIn() {
        return ParseUser.getCurrentUser() != null;
    }
}
