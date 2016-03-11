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
<<<<<<< HEAD
        ParseFile pf = event.getImageFile();
        Glide.with(mContext).load(pf.getUrl()).centerCrop().into(viewHolder.ivEventImage);
        viewHolder.tvCity.setText(event.getAddress());
=======
        Glide.with(mContext).load(event.getImageUrl()).centerCrop().into(viewHolder.ivEventImage);
        viewHolder.tvLocality.setText(event.getLocality());
>>>>>>> ab4ae9e7856889030b27b8c246e8bd13a550dcf9

        ///// COMMENTED SECTION CAN BE SAFELY DELETED AFTER TEST
//        //Retrieve Image
//        ParseFile imageFile = event.getImageFile();
//        if( imageFile != null) {
//            imageFile.getDataInBackground(new GetDataCallback() {
//                public void done(byte[] data, ParseException e) {
//                    if (e == null) {
//                        // data has the bytes for the resume
//                        Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                        viewHolder.ivEventImage.setImageBitmap(bMap);
//
//                       // Tried using Glide. But it runs into error below:
//                       // ERROR: You must provide a Model of a type for which there is a registered ModelLoader, if you are using a custom model, you must first call Glide#register with a ModelLoaderFactory for your custom model class
//                       // Glide.with(mContext).load(bMap).asBitmap().centerCrop().into(viewHolder.ivEventImage);
//
//                    } else {
//                        Log.d("DEBUG", "Parse exception: " + e.toString());                    }
//                }
//            });
//        }
    }

    private void retriveImage() {
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
