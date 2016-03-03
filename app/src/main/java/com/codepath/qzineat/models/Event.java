package com.codepath.qzineat.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Shyam Rokde on 3/2/16.
 */
@ParseClassName("Event")
public class Event extends ParseObject {


    private String title;
    private String description;
    private String imageUrl;
    private Date date;
    private int guestLimit;
    private int signupCount;
    private int favouritesCount;
    private String address;
    private ParseUser host;


    public Event(){
        // Required for Parse
    }

    public String getImageUrl() {
        return getString("eventImageUrl");
    }

    public void setImageUrl(String eventImageUrl) {
        put("eventImageUrl" , eventImageUrl);
    }

    public String getTitle() {
        return getString("eventTitle");
    }

    public void setTitle(String eventTitle) {
        put("eventTitle", eventTitle);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public Date getDate() {
        return getDate("date");
    }

    public void setDate(Date date) {
        put("date", date);
    }

    public int getGuestLimit() {
        return getInt("guestLimit");
    }

    public void setGuestLimit(int guestLimit) {
        put("guestLimit", guestLimit);
    }

    public int getSignupCount() {
        return getInt("signupCount");
    }

    public void setSignupCount(int signupCount) {
        put("signupCount", signupCount);
    }

    public int getFavouritesCount() {
        return getInt("favouritesCount");
    }

    public void setFavouritesCount(int favouritesCount) {
        put("favouritesCount", favouritesCount);
    }

    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String address) {
        put("address", address);
    }

    public ParseUser getHost() {
        return getParseUser("host");
    }

    // TODO: find out how to make current user host... Also ask login/signup before hosting
    public void setHost(ParseUser host) {
        put("host", host);
    }
}
