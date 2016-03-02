package com.codepath.qzineat.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shyam Rokde on 3/2/16.
 */
public class Event {
    private String eventImageUrl;
    private String eventTitle;

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Event(JSONObject jsonObject){
        super();
        try{
            setEventImageUrl(jsonObject.getString("image"));
            setEventTitle(jsonObject.getString("title"));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<Event> fromJSONArray(JSONArray jsonArray) {

        ArrayList<Event> events = new ArrayList<>();

        for(int i=0; i < jsonArray.length(); i++){
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Event event = new Event(jsonObject);
                events.add(event);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return events;
    }
}
