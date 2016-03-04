package com.codepath.qzineat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.qzineat.adapters.EventsRecyclerViewAdapter;
import com.codepath.qzineat.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
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

        // Setup RecyclerView
        setupRecyclerView();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mEvents = new ArrayList<>();
        recyclerViewAdapter = new EventsRecyclerViewAdapter(mEvents, getContext());

        // Populate Data
        getEvents();
    }

    private void getEvents() {
        // Construct query to execute
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        // Configure limit and sort order
        query.setLimit(20);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e == null) {
                    if (events.size() > 0) {
                        int curSize = recyclerViewAdapter.getItemCount();
                        ArrayList<Event> arrayList = new ArrayList<>(events);
                        mEvents.addAll(arrayList);
                        recyclerViewAdapter.notifyItemRangeInserted(curSize, arrayList.size());
                    }
                } else {
                    Log.e("ERROR", "Error Loading events" + e); // Don't notify this to user..
                }
            }
        });
    }

    private void setupRecyclerView() {
        rvEvents.setAdapter(recyclerViewAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvEvents.setLayoutManager(layoutManager);

        rvEvents.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // TODO: Get more - pagination logic here
            }
        });
    }



}
