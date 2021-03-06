package com.codepath.qzineat.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.qzineat.R;
import com.codepath.qzineat.fragments.UserEventsFragment;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.models.User;
import com.codepath.qzineat.utils.QZinUtil;
import com.parse.ParseFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by glondhe on 3/10/16.
 */
public class UserEventRecyclerViewAdapter extends RecyclerView.Adapter<UserItemViewHolder> {

    public ArrayList<Event> mEvents;
    public Context mContext;
    private String dateTime;
    private Event event;
    private UserEventsFragment mFragment;
    private Bundle mBundle;
    private Context context;
    public int position;

    public UserEventRecyclerViewAdapter(ArrayList<Event> events, Context context, UserEventsFragment fragment) {
        mEvents = events;
        mContext = context;
        mFragment = fragment;
    }

    @Override
    public UserItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate custom layout
        View resultView = inflater.inflate(R.layout.host_events, parent, false);
        // Return new holder instance
        return new UserItemViewHolder(resultView);
    }


    @Override
    public void onBindViewHolder(final UserItemViewHolder viewHolder, final int position) {

        // 1. Get Event
        this.position = position;
        event = mEvents.get(position);

        Log.d("DEBUG_Current_User", User.getCurrentUser().getObjectId());
        Log.d("DEBUG_event_User", event.getHost().getObjectId());
        if (event.getHost().getObjectId().equals(User.getLoggedInUser().getObjectId())){
            viewHolder.evEdit.setVisibility(View.VISIBLE);
        }

        // 2. Populate user interface
        dateTime = getRelativeTimeAgo(event.getDate().toString());
        viewHolder.ivEventImage.setImageResource(android.R.color.transparent); // clear out old image for recycled view
        ParseFile pf = event.getImageFile();

        String imgUrl;
        if(pf != null && !pf.getUrl().isEmpty()){
            imgUrl = pf.getUrl();
        }else {
            imgUrl = QZinUtil.getQZinImageUrl();
        }
        Glide.with(mContext).load(imgUrl).centerCrop().into(viewHolder.ivEventImage);

        viewHolder.tvTitle.setText(event.getTitle());
        viewHolder.tvDate.setText(dateTime);
        viewHolder.tvCity.setText(event.getLocality());

        viewHolder.evEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event = mEvents.get(position);
                mFragment.openHostFragmentForEdit(event.getObjectId());
            }
        });
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

    public static String getRelativeTimeAgo(String rawJsonDate) {

        String plainFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        String qzinFormat = "EEE MMM dd '@' HH:mm aaa";
        SimpleDateFormat sf = new SimpleDateFormat(plainFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            Date date = sf.parse(rawJsonDate);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(qzinFormat);
            relativeDate = simpleDateFormat.format(date).toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
