package com.codepath.qzineat.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.QZinEatApplication;
import com.codepath.qzineat.activities.HomeActivity;
import com.codepath.qzineat.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.qzineat.adapters.UserEventRecyclerViewAdapter;
import com.codepath.qzineat.adapters.WrapContentLinearLayoutManager;
import com.codepath.qzineat.interfaces.CommunicationChannel;
import com.codepath.qzineat.interfaces.UserEventsListener;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.utils.ItemClickSupport;
import com.codepath.qzineat.utils.QZinDataAccess;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/10/16.
 */
public class UserEventsFragment extends Fragment implements UserEventsListener {

    @Bind(R.id.rvEvents)
    RecyclerView rvEvents;
    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private ArrayList<Event> mEvents;
    private UserEventRecyclerViewAdapter recyclerViewAdapter;
    protected String searchFood;
    protected String searchLocality;
    public boolean isSubscriberView;

    CommunicationChannel mCommunicationChannel = null;


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
        recyclerViewAdapter = new UserEventRecyclerViewAdapter(mEvents, getContext(), this);

        // On Search
        if(getArguments() != null){
            searchFood = getArguments().getString("searchFood");
            searchLocality = getArguments().getString("searchLocality");
            isSubscriberView = getArguments().getBoolean("isSubscriberView");
            // Log.d("DEBUG", searchQuery);
        }
        // Populate Data
        getEvents();
    }

    protected Date lastCreatedAt; // used for pagination

    protected void getEvents() {
        if(QZinEatApplication.isHostView){
            QZinDataAccess.findHostedEvents(lastCreatedAt, this);
        }
    }

    public void onNewEvent(Event event) {
        // Add Tweet in the beginning of list
        mEvents.add(0, event);
        recyclerViewAdapter.notifyItemInserted(0);
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

            Intent i = new Intent(getContext(), HomeActivity.class);
            i.putExtra("eventObjectId", event.getObjectId());
            startActivity(i);
        }
    };


    @Override
    public void onEventsSearch(Date lastCreatedAt, ArrayList<Event> eventArrayList) {
        int curSize = recyclerViewAdapter.getItemCount();
        mEvents.addAll(eventArrayList);
        recyclerViewAdapter.notifyItemRangeInserted(curSize, eventArrayList.size());
        this.lastCreatedAt = lastCreatedAt;

        // Swipe container
        swipeContainer.setRefreshing(false);
    }

    public void openHostFragmentForEdit(String eventObjectId){
        HostFragment fragment = HostFragment.newInstance(eventObjectId);
        mCommunicationChannel.openFragment(fragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof CommunicationChannel){
            mCommunicationChannel = (CommunicationChannel) context;
        }
    }
}
