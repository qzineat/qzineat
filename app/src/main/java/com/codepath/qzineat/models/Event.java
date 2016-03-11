package com.codepath.qzineat.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
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

    public String getImageUrl() {
        return getString("imageUrl");
    }

    public void setImageUrl(String imageUrl) {
        put("imageUrl" , imageUrl);
    }

    public ParseFile getImageFile() {
        return getParseFile("imageFile");
    }

    public void setImageFile(ParseFile imageFile) {
        put("imageFile" , imageFile);
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

    public String getCategory() {
        return getString("category");
    }

    public void setCategory(String category) {
        put("category", category);
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

    public Date getTime() {
        return getDate("date");
    }

    public void setTime(Date time) {
        put("time", time);
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

    //
    // Review
    //
    public int getNumberOfReviews() {
        return getInt("numberOfReviews");
    }

    public void setNumberOfReviews(int numberOfReviews) {
        put("numberOfReviews", numberOfReviews);
    }

    public int getRatingSum() {
        return getInt("ratingSum");
    }

    public void setRatingSum(int ratingSum) {
        put("ratingSum", ratingSum);
    }

    //
    // Address
    //
    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint location) {
        put("location", location);
    }

    public String getLocality() {
        return getString("locality");
    }

    public void setLocality(String locality) {
        put("locality", locality);
    }

    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String address) {
        put("address", address);
    }
}
