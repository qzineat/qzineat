package com.codepath.qzineat.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Shyam Rokde on 3/5/16.
 */
@ParseClassName("Attendee")
public class Attendee extends ParseObject {

    public Attendee(){}

    public String getEventId() {
        return getString("eventId");
    }

    public void setEventId(String eventId) {
        put("eventId", eventId);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public int getGuestCount() {
        return getInt("guestCount");
    }

    public void setGuestCount(int guestCount) {
        put("guestCount", guestCount);
    }

}
