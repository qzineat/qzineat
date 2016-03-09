package com.codepath.qzineat.models;
import com.parse.ParseUser;

/**
 * Usage
 *
 * if(User.isUserLoggedIn()){
 *      User.getLoggedInUser().setPhone("555-555-5555");
 *      User.getLoggedInUser().saveInBackground();
 *  }
 */
public class User extends ParseUser {

    public static final int USER_LOG_IN_SUCCESS = 1;   // The request code
    public static final int USER_LOG_IN_FAILED = 2;    // The request code
    public static final int USER_LOG_IN_CANCEL = 3;    // The request code


    public User(){}

    public String getPhone() {
        return getString("phone");
    }

    public void setPhone(String phone) {
        put("phone", phone);
    }

    public static User getLoggedInUser(){
        return (User) User.getCurrentUser();
    }

    public static boolean isUserLoggedIn() {
        return User.getCurrentUser() != null;
    }

    public void logout() {
        User.logOut();
    }
    
    public String getUserCity() {
        return getString("UserCity");
    }

    public void setUserCity(String UserCity) {
        put("UserCity", UserCity);

    }
}
