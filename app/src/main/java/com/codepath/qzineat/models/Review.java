package com.codepath.qzineat.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Shyam Rokde on 3/10/16.
 */
@ParseClassName("Review")
public class Review extends ParseObject{
    // Rating = 1 to 5
    // Rating by User
    // Rating for Event

    private ParseUser reviewedBy;
    private Event event;
    private int rating;

    public ParseUser getReviewedBy() {
        return getParseUser("reviewedBy");
    }

    public void setReviewedBy(ParseUser reviewedBy) {
        put("reviewedBy", reviewedBy);
    }

    public Event getEvent() {
        return (Event) getParseObject("event");
    }

    public void setEvent(Event event) {
        put("event", event);
    }

    public int getRating() {
        return getInt("rating");
    }

    public void setRating(int rating) {
        put("rating", rating);
    }
}
