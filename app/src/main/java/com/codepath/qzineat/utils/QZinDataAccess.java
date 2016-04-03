package com.codepath.qzineat.utils;

import android.util.Log;

import com.codepath.qzineat.QZinEatApplication;
import com.codepath.qzineat.interfaces.DataUpdateListener;
import com.codepath.qzineat.interfaces.UserEventCountListener;
import com.codepath.qzineat.interfaces.UserEventsListener;
import com.codepath.qzineat.models.Attendee;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.models.User;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shyam Rokde on 3/13/16.
 */
public class QZinDataAccess {


    public static void saveEvent(Event event, final DataUpdateListener dataUpdateListener) {
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    dataUpdateListener.onEventSave();
                }
            }
        });
    }

    public static void saveAttendee(final Event event, final int guestCount) {
        final Attendee attendee = new Attendee();
        attendee.setGuestCount(guestCount);
        attendee.setSubscribedBy(User.getLoggedInUser());
        attendee.setEvent(event);

        // Save attendee
        attendee.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex != null) {
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

    public static void findUserEventsCount(final UserEventCountListener listener) {
        Log.d("DEBUG", "findUserEventsCount");
        if (!User.isUserLoggedIn()) {
            return;
        }

        // When host
        if (QZinEatApplication.isHostView) {
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

        } else {
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


    public static void findUpcomingHostEvents(Date eventDate, UserEventsListener listener) {
        Log.d("DEBUG", "findUpcomingHostedEvents");
        if (!User.isUserLoggedIn()) {
            return;
        }

        if (QZinEatApplication.isHostView) {
            getHostedEvents(eventDate, listener, true);
        }
    }

    public static void findPastHostEvents(Date eventDate, UserEventsListener listener) {
        Log.d("DEBUG", "findPastHostedEvents");
        if (!User.isUserLoggedIn()) {
            return;
        }

        if (QZinEatApplication.isHostView) {
            getHostedEvents(eventDate, listener, false);
        }
    }

    public static void findHostedEvents(Date eventDate, UserEventsListener listener) {
        Log.d("DEBUG", "findHostedEvents");
        if (!User.isUserLoggedIn()) {
            return;
        }

        if (QZinEatApplication.isHostView) {
            getAllHostedEvents(eventDate, listener);
        }
    }


    public static void getAllHostedEvents(Date eventDate, final UserEventsListener listener) {

        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.whereEqualTo("host", User.getCurrentUser());
        query.orderByDescending("date");

        if (eventDate != null) { // paging
            query.whereLessThan("date", eventDate);
        }
        query.setLimit(10);

        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e == null) {
                    ArrayList<Event> arrayList = new ArrayList<>(events);

                    // Order By Created Date
                    if (arrayList.size() > 0) {
                        // set value for pagination
                        Date newEventDate = arrayList.get(arrayList.size() - 1).getDate();
                        listener.onEventsSearch(newEventDate, arrayList);
                    }
                }
            }
        });
    }

    public static void getHostedEvents(Date eventDate, final UserEventsListener listener, boolean upComing) {
        // Today
        Date today = new Date();
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.whereEqualTo("host", User.getCurrentUser());
        query.orderByAscending("date");
        if (upComing) {
            query.whereGreaterThan("date", today);
        } else {
            query.whereLessThan("date", today);
        }

        if (eventDate != null) { // paging
            query.whereGreaterThan("date", eventDate);
        }
        query.setLimit(10);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e == null) {
                    ArrayList<Event> arrayList = new ArrayList<>(events);

                    // Order By Created Date
                    if (arrayList.size() > 0) {
                        // set value for pagination
                        Date newEventDate = arrayList.get(arrayList.size() - 1).getDate();
                        listener.onEventsSearch(newEventDate, arrayList);
                    }
                }
            }
        });
    }

    public static void findPastSubscribedEvents(Date lastCreatedAt, UserEventsListener listener) {
        Log.d("DEBUG", "findPastSubscribedEvents");
        if (!User.isUserLoggedIn()) {
            return;
        }
        if (QZinEatApplication.isHostView) {
            return;
        }
        getSubscriberEventsByDay(lastCreatedAt, listener, false);
    }


    public static void findUpcomingSubscribedEvents(Date lastCreatedAt, UserEventsListener listener) {
        Log.d("DEBUG", "findUpcomingSubscribedEvents");
        if (!User.isUserLoggedIn()) {
            return;
        }
        if (QZinEatApplication.isHostView) {
            return;
        }

        getSubscriberEventsByDay(lastCreatedAt, listener, true);
    }

    public static void getSubscriberEventsByDay(Date lastCreatedAt, final UserEventsListener listener, boolean upComing) {

        //1. Event Date greater than today
        //2. Order by Event Date Asc order - this will used as paging - not possible

        // Today
        Date today = new Date();

        Log.d("DEBUG", "today - " + today.toString());

        ParseQuery<Event> innerQuery = ParseQuery.getQuery(Event.class);
        if (upComing) {
            innerQuery.whereGreaterThan("date", today);
        } else {
            innerQuery.whereLessThan("date", today);
        }

        // Search on Attendee
        ParseQuery<Attendee> attendeeParseQuery = ParseQuery.getQuery(Attendee.class);
        attendeeParseQuery.whereEqualTo("subscribedBy", User.getLoggedInUser());
        attendeeParseQuery.include("event");
        attendeeParseQuery.whereMatchesQuery("event", innerQuery);
        attendeeParseQuery.orderByAscending("createdAt");
        attendeeParseQuery.setLimit(10);
        if (lastCreatedAt != null) { // paging
            attendeeParseQuery.whereGreaterThan("createdAt", lastCreatedAt);
            Log.d("DEBUG", "lastCreatedAt - " + lastCreatedAt.toString());
        }

        attendeeParseQuery.findInBackground(new FindCallback<Attendee>() {
            @Override
            public void done(List<Attendee> attendees, ParseException e) {
                if (e == null) {
                    ArrayList<Event> arrayList = new ArrayList<>();
                    for (Attendee a : attendees) {
                        if (a.getEvent() != null) {
                            arrayList.add(a.getEvent());
                        }
                    }
                    // Order By Created Date
                    if (attendees.size() > 0) {
                        // set value for pagination

                        Date lastCreatedAt = attendees.get(attendees.size() - 1).getCreatedAt();
                        if (arrayList.size() > 0) {
                            listener.onEventsSearch(lastCreatedAt, arrayList);
                        }
                    }
                }

            }
        });

    }


}
