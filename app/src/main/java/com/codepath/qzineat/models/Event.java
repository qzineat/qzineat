package com.codepath.qzineat.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shyam Rokde on 3/2/16.
 */
@ParseClassName("Event")
public class Event extends ParseObject {

    public Event(){
        // Required for Parse
    }

    public String getEventImageUrl() {
        return getString("eventImageUrl");
    }

    public void setEventImageUrl(String eventImageUrl) {
        put("eventImageUrl" , eventImageUrl);
    }

    public String getEventTitle() {
        return getString("eventTitle");
    }

    public void setEventTitle(String eventTitle) {
        put("eventTitle", eventTitle);
    }







    // TODO: Remove later - this is not used in parse
    public Event(JSONObject jsonObject){
        super();
        try{
            setEventImageUrl(jsonObject.getString("image"));
            setEventTitle(jsonObject.getString("title"));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    // TODO: Remove later - this is not used in parse
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
