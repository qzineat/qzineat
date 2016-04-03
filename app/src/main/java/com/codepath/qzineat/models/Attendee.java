package com.codepath.qzineat.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Shyam Rokde on 3/5/16.
 */
@ParseClassName("Attendee")
public class Attendee extends ParseObject {

    public Attendee() {
    }


    Event event;

    public Event getEvent() {
        return (Event) getParseObject("event");
    }

    public void setEvent(Event event) {
        put("event", event);
    }

    public ParseUser getSubscribedBy() {
        return getParseUser("subscribedBy");
    }

    public void setSubscribedBy(ParseUser user) {
        put("subscribedBy", user);
    }

    public int getGuestCount() {
        return getInt("guestCount");
    }

    public void setGuestCount(int guestCount) {
        put("guestCount", guestCount);
    }

}
