package com.codepath.qzineat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.activities.EventDetailActivity;
import com.codepath.qzineat.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.qzineat.adapters.EventsRecyclerViewAdapter;
import com.codepath.qzineat.adapters.WrapContentLinearLayoutManager;
import com.codepath.qzineat.models.Attendee;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.models.User;
import com.codepath.qzineat.utils.ItemClickSupport;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/2/16.
 */
public class EventListFragment extends Fragment {

    private ArrayList<Event> mEvents;
    private EventsRecyclerViewAdapter recyclerViewAdapter;



    @Bind(R.id.rvEvents) RecyclerView rvEvents;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this, view);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(mRefreshListener);

        // Setup RecyclerView
        setupRecyclerView();

        // Item click support
        setupItemClick();

        return view;
    }

    private void setupItemClick() {
        // Item click Listener
        ItemClickSupport.addTo(rvEvents).setOnItemClickListener(mEventClickListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEvents = new ArrayList<>();
        recyclerViewAdapter = new EventsRecyclerViewAdapter(mEvents, getContext());

        // On Search
        if(getArguments() != null){
            searchFood = getArguments().getString("searchFood");
            searchLocality = getArguments().getString("searchLocality");
            isProfileView = getArguments().getBoolean("isProfileView");
            isSubscriberView = getArguments().getBoolean("isSubscriberView");
           // Log.d("DEBUG", searchQuery);
        }

        // Populate Data
        getEvents();
    }

    protected Date lastCreatedAt; // used for pagination
    protected String searchFood;
    protected String searchLocality;
    private boolean isProfileView;
    private boolean isSubscriberView;

    public void getEvents() {

        List<ParseQuery<Event>> queries = new ArrayList<ParseQuery<Event>>();
        ParseQuery<Event> mainQuery;
        if (searchFood != null){
            //query.whereStartsWith("title", searchQuery);
            //query.whereMatches("title", "Michael", "i");
            //ParseQuery<Event> q2 =  ParseQuery.getQuery(Event.class).whereContains("category", searchFood);
            ParseQuery<Event> q2 =  ParseQuery.getQuery(Event.class).whereMatches("category", searchFood, "i");
            queries.add(q2);

            //ParseQuery<Event> q3 = ParseQuery.getQuery(Event.class).whereEqualTo("locality", searchLocality); // TODO: This need geo search
            //queries.add(q3);

            mainQuery = ParseQuery.or(queries);
        }else{
            mainQuery = ParseQuery.getQuery(Event.class);
        }

        //ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        // Configure limit and sort order
        mainQuery.setLimit(3);
        mainQuery.orderByDescending("createdAt");
        if(lastCreatedAt != null){
            mainQuery.whereLessThan("createdAt", lastCreatedAt);
        }
        if(isProfileView){
            mainQuery.whereEqualTo("host", User.getCurrentUser());
        }
        if(searchLocality != null && !searchLocality.isEmpty()){
            mainQuery.whereEqualTo("locality", searchLocality);
        }

        if(isSubscriberView){
            Log.d("DEBIG","I am in Subscriber");
            // Search on Attendee
            ParseQuery<Attendee> attendeeParseQuery = ParseQuery.getQuery(Attendee.class);
            attendeeParseQuery.whereEqualTo("user", User.getLoggedInUser());
            attendeeParseQuery.include("event");
            attendeeParseQuery.orderByDescending("createdAt");
            if(lastCreatedAt != null){
                attendeeParseQuery.whereLessThan("createdAt", lastCreatedAt);
            }
            attendeeParseQuery.findInBackground(new FindCallback<Attendee>() {
                @Override
                public void done(List<Attendee> attendees, ParseException e) {
                    if (e == null) {
                        ArrayList<Event> arrayList = new ArrayList<>();
                        for(Attendee a: attendees){
                            if(a.getEvent()!= null){
                                arrayList.add(a.getEvent());
                            }
                        }

                        int curSize = recyclerViewAdapter.getItemCount();
                        mEvents.addAll(arrayList);
                        recyclerViewAdapter.notifyItemRangeInserted(curSize, arrayList.size());
                        if (attendees.size() > 0) {
                            // set value for pagination
                            lastCreatedAt = attendees.get(attendees.size() - 1).getCreatedAt();
                        }
                    }
                }
            });
        }else {
            mainQuery.findInBackground(new FindCallback<Event>() {
                @Override
                public void done(List<Event> events, ParseException e) {
                    if (e == null) {
                        Log.d("DEBUG", "Response Size - " + events.size());
                        int curSize = recyclerViewAdapter.getItemCount();
                        ArrayList<Event> arrayList = new ArrayList<>(events);
                        mEvents.addAll(arrayList);
                        recyclerViewAdapter.notifyItemRangeInserted(curSize, arrayList.size());
                        if (events.size() > 0) {
                            // set value for pagination
                            lastCreatedAt = mEvents.get(mEvents.size() - 1).getCreatedAt();
                        }
                    } else {
                        Log.e("ERROR", "Error Loading events" + e); // Don't notify this to user..
                    }
                    swipeContainer.setRefreshing(false);
                }
            });
        }
    }



    private void setupRecyclerView() {
        rvEvents.setAdapter(recyclerViewAdapter);

        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        rvEvents.setLayoutManager(layoutManager);

        rvEvents.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(lastCreatedAt != null){
                    getEvents();
                }
            }
        });

    }

    private final SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            recyclerViewAdapter.clear();
            // Reload Data
            lastCreatedAt = null;
            getEvents();
        }
    };

    private final ItemClickSupport.OnItemClickListener mEventClickListener = new ItemClickSupport.OnItemClickListener() {
        @Override
        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
            Event event = mEvents.get(position);

            Intent i = new Intent(getContext(), EventDetailActivity.class);
            i.putExtra("eventObjectId", event.getObjectId());
            startActivity(i);
        }
    };



}
