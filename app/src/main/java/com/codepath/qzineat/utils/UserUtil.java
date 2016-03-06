package com.codepath.qzineat.utils;

import com.facebook.AccessToken;

/**
 * Created by Shyam Rokde on 3/6/16.
 */

public class UserUtil {

    public static final int USER_LOG_IN_SUCCESS = 1;   // The request code
    public static final int USER_LOG_IN_FAILED = 2;    // The request code
    public static final int USER_LOG_IN_CANCEL = 3;    // The request code


    public static String getLoggedInUserId() {
        return AccessToken.getCurrentAccessToken().getUserId();
    }

    public static boolean isUserLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }
}
