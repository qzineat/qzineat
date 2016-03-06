package com.codepath.qzineat.utils;

import com.facebook.AccessToken;

/**
 * Created by Shyam Rokde on 3/6/16.
 */

public class UserUtil {

    public static String getLoggedInUserId() {
        return AccessToken.getCurrentAccessToken().getUserId();
    }

    public static boolean isUserLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }
}
