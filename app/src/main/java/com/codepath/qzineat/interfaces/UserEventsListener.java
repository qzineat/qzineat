package com.codepath.qzineat.interfaces;

import com.codepath.qzineat.models.Event;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Shyam Rokde on 3/17/16.
 */
public interface UserEventsListener {
    void onEventsSearch(Date lastCreatedAt, ArrayList<Event> eventArrayList);
}
