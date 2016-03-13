package com.codepath.qzineat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.utils.QZinUtil;
import com.parse.ParseFile;

import java.util.ArrayList;

/**
 * Created by Shyam Rokde on 3/2/16.
 */
public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventItemViewHolder> {

    public ArrayList<Event> mEvents;
    public Context mContext;


    public EventsRecyclerViewAdapter(ArrayList<Event> events, Context context) {
        mEvents = events;
        mContext = context;
    }

    @Override
    public EventItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate custom layout
        View resultView = inflater.inflate(R.layout.item_events, parent, false);
        // Return new holder instance
        return new EventItemViewHolder(resultView);
    }

    @Override
    public void onBindViewHolder(final EventItemViewHolder viewHolder, int position) {

        // 1. Get Event
        Event event = mEvents.get(position);

        // 2. Populate user interface
        viewHolder.tvTitle.setText(event.getTitle());
        viewHolder.ivEventImage.setImageResource(android.R.color.transparent); // clear out old image for recycled view
        ParseFile pf = event.getImageFile();
        Glide.with(mContext).load(pf.getUrl()).centerCrop().into(viewHolder.ivEventImage);
        viewHolder.tvLocality.setText(event.getLocality());
        viewHolder.tvEventDate.setText(QZinUtil.getShortDate(event.getDate()));
        if(event.getPrice() > 0){
            viewHolder.tvPrice.setText(String.format("$%d", event.getPrice()));
        }else {
            viewHolder.tvPrice.setText("FREE");
        }
    }


    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public void clear() {
        mEvents.clear();
        notifyItemRangeRemoved(0, getItemCount());

        Log.d("DEBUG", "Adapter and Event List is cleared...");
    }

}
