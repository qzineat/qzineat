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

import com.codepath.android.navigationdrawerexercise.R;
import com.codepath.qzineat.QzinEatClient;
import com.codepath.qzineat.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.qzineat.adapters.EventsRecyclerViewAdapter;
import com.codepath.qzineat.models.Event;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Shyam Rokde on 3/2/16.
 */
public class EventListFragment extends Fragment {

    private ArrayList<Event> mEvents;
    private EventsRecyclerViewAdapter recyclerViewAdapter;
    private QzinEatClient qzinEatClient;


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
        // create client
        qzinEatClient = new QzinEatClient();

        mEvents = new ArrayList<>();
        recyclerViewAdapter = new EventsRecyclerViewAdapter(mEvents, getContext());

        // get data
        qzinEatClient.getEvents(mEventListResponseHandler);
    }

    private void setupRecyclerView() {
        rvEvents.setAdapter(recyclerViewAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvEvents.setLayoutManager(layoutManager);

        rvEvents.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                qzinEatClient.getEvents(mEventListResponseHandler);
            }
        });
    }

    private final JsonHttpResponseHandler mEventListResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onStart() {
            Log.d("DEBUG", "Request: " + super.getRequestURI().toString());
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
            Log.d("DEBUG", "Response: " + jsonObject.toString());

            int curSize = recyclerViewAdapter.getItemCount();

            ArrayList<Event> arrayList = null;
            try {
                arrayList = Event.fromJSONArray(jsonObject.getJSONArray("event"));

                mEvents.addAll(arrayList);
                recyclerViewAdapter.notifyItemRangeInserted(curSize, arrayList.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

        }
    };
}
