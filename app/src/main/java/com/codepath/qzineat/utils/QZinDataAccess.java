package com.codepath.qzineat.utils;

import android.util.Log;

import com.codepath.qzineat.QZinEatApplication;
import com.codepath.qzineat.interfaces.UserEventCountListener;
import com.codepath.qzineat.models.Attendee;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.models.User;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * Created by Shyam Rokde on 3/13/16.
 */
public class QZinDataAccess {

    public static void saveAttendee(final Event event, final int guestCount){
        final Attendee attendee = new Attendee();
        attendee.setGuestCount(guestCount);
        attendee.setSubscribedBy(User.getLoggedInUser());
        attendee.setEvent(event);

        // Save attendee
        attendee.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if(ex != null){
                    ex.printStackTrace();
                    return;
                }

                int avl;
                if (guestCount == 0) {
                    avl = event.getAttendeesMaxCount() - event.getAttendeesAvailableCount() - 1;
                } else {
                    avl = event.getAttendeesMaxCount() - event.getAttendeesAvailableCount() - guestCount;
                }

                // Save event
                event.addAttendee(attendee);
                event.setAttendeesAvailableCount(avl);
                event.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public static void findUserEventsCount(final UserEventCountListener listener){
        Log.d("DEBUG", "in getHostedEventsCount");
        if(!User.isUserLoggedIn()){
            return;
        }

        // When host
        if(QZinEatApplication.isHostView) {
            ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
            query.whereEqualTo("host", User.getCurrentUser());
            query.countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if (count > 0) {
                        listener.onUserEventCount(count);
                    }
                }
            });

        }else {
            ParseQuery<Attendee> query = ParseQuery.getQuery(Attendee.class);
            query.whereEqualTo("subscribedBy", User.getLoggedInUser());
            query.countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if (count > 0) {
                        listener.onUserEventCount(count);
                    }
                }
            });
        }

    }

}
