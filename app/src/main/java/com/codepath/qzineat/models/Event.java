package com.codepath.qzineat.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Shyam Rokde on 3/2/16.
 */
@ParseClassName("Event")
public class Event extends ParseObject {


    public Event(){
        // Required for Parse
    }


    public String getHostUserId() {
        return getString("hostUserId");
    }

    public void setHostUserId(String hostUserId) {
        put("hostUserId", hostUserId);
    }

    public String getImageUrl() {
        return getString("imageUrl");
    }

    public void setImageUrl(String imageUrl) {
        put("imageUrl" , imageUrl);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public int getPrice() {
        return getInt("price");
    }

    public void setPrice(int price) {
        put("price", price);
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

    public String getAlcohol() {
        return getString("alcohol");
    }

    public void setAlcohol(String alcohol) {
        put("alcohol", alcohol);
    }

    public ParseUser getHost() {
        return getParseUser("host");
    }

    public void setHost(ParseUser host) {
        put("host", host);
    }

    public ParseRelation<Attendee> getAttendeeRelation(){
        return getRelation("attendees");
    }

    public void addAttendee(Attendee attendee) {
        getAttendeeRelation().add(attendee);
    }

    public void removeAttendee(Attendee attendee){
        getAttendeeRelation().remove(attendee);
    }
}
