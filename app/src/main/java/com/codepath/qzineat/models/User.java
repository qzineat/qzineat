package com.codepath.qzineat.models;

import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * Usage
 * <p/>
 * if(User.isUserLoggedIn()){
 * User.getLoggedInUser().setPhone("555-555-5555");
 * User.getLoggedInUser().saveInBackground();
 * }
 */
public class User extends ParseUser {

    public static final int USER_LOG_IN_SUCCESS = 1;   // The request code
    public static final int USER_LOG_IN_FAILED = 2;    // The request code
    public static final int USER_LOG_IN_CANCEL = 3;    // The request code


    public User() {
    }

    public static User getLoggedInUser() {
        return (User) User.getCurrentUser();
    }

    public static boolean isUserLoggedIn() {
        return User.getCurrentUser() != null;
    }

    public String getProfileName() {
        return getString("ProfileName");
    }

    public void setProfileName(String ProfileName) {
        put("ProfileName", ProfileName);
    }

    public String getPhone() {
        return getString("phone");
    }

    public void setPhone(String phone) {
        put("phone", phone);
    }

    public void logout() {
        User.logOut();
    }

    public String getCity() {
        return getString("city");
    }

    public void setCity(String city) {
        put("city", city);
    }

    public ParseFile getImageFile() {
        return getParseFile("imageFile");
    }

    public void setImageFile(ParseFile imageFile) {

        put("imageFile", imageFile);
    }

    public String getSpeciality() {
        return getString("Speciality");
    }

    public void setSpeciality(String Speciality) {
        put("Speciality", Speciality);
    }

    public String getEmail() {
        return getString("Email");
    }

    public void setEmail(String Email) {
        put("Email", Email);
    }

    public String getWebsite() {
        return getString("Website");
    }

    public void setWebsite(String Website) {
        put("Website", Website);
    }

    public Boolean getIsHost() {
        return getBoolean("isHost");
    }

    public void setIsHost(boolean isHost) {
        put("isHost", isHost);
    }

}
