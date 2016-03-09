package com.codepath.qzineat.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by glondhe on 3/6/16.
 */
@ParseClassName("User")
public class User extends ParseObject {

    public User(){
        // Required for Parse
    }
    public String getUserId() {
        return getString("userId");
    }

    public void setUserId(String userId) {
        put("userId", userId);
    }

    public String getUserCity() {
        return getString("UserCity");
    }

    public void setUserCity(String UserCity) {
        put("UserCity", UserCity);
    }
}
