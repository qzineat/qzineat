package com.codepath.qzineat.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Shyam Rokde on 3/2/16.
 */
@ParseClassName("Event")
public class Event extends ParseObject {

    private String imageUrl;
    private String title;
    private String description;
    private String guestLimit;

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
}
