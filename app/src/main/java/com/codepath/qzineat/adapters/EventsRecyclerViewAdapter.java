package com.codepath.qzineat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.interfaces.EventListCallback;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.utils.QZinUtil;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shyam Rokde on 3/2/16.
 */
public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventItemViewHolder> {

    private EventListCallback mEventListCallback;
    public ArrayList<Event> mEvents;
    public Context mContext;
    private LinearLayout mainLayout;
    private int[] images = {R.drawable.drawer, R.drawable.drawer, R.drawable.drawer,
            R.drawable.drawer, R.drawable.drawer};
    private View cell;
    private TextView text;
    private ParseObject pObject;
    private Bitmap bitmap;

    public EventsRecyclerViewAdapter(ArrayList<Event> events, Context context, EventListCallback eventListCallback) {
        this.mEvents = events;
        this.mContext = context;
        this.mEventListCallback = eventListCallback;
    }

    @Override
    public EventItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate custom layout
        View resultView = inflater.inflate(R.layout.item_events, parent, false);
        // Return new holder instance

        mainLayout = (LinearLayout) resultView.findViewById(R.id._linearLayout);

        return new EventItemViewHolder(resultView);
    }

    @Override
    public void onBindViewHolder(final EventItemViewHolder viewHolder, int position) {
        viewHolder.position = position;

        // 1. Get Event
        final Event event = mEvents.get(position);
        //setImages(event);

        // 2. Populate user interface
        viewHolder.tvTitle.setText(event.getTitle());
        viewHolder.ivEventImage1.setImageResource(android.R.color.transparent);// clear out old image for recycled view
        viewHolder.ivSubscribe.setImageResource(android.R.color.transparent);// clear out old image for recycled view
        final ParseFile pf = event.getImageFile();
        final String imgUrl;
        if(pf != null && !pf.getUrl().isEmpty()){
            imgUrl = pf.getUrl();
        }else {
            imgUrl = QZinUtil.getQZinImageUrl();
        }

       Glide.with(mContext).load(imgUrl).asBitmap().centerCrop().into(viewHolder.ivEventImage1);

        viewHolder.tvLocality.setText(event.getLocality());
        viewHolder.tvEventDate.setText(QZinUtil.getShortDate(event.getDate()));
        if(event.getPrice() > 0){
            viewHolder.tvPrice.setText(String.format("$%d", event.getPrice()));
        }else {
            viewHolder.tvPrice.setText("FREE");
            viewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("*/*");

                    sharingIntent.putExtra(Intent.EXTRA_TITLE, viewHolder.tvTitle.getText());
                    sharingIntent.setAction(Intent.ACTION_SEND);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, viewHolder.tvTitle.getText());
                    try

                    {
                        Uri pictureUri = Uri.parse(pf.getUrl());

                        Bitmap bitmap = BitmapFactory.decodeFile(pf.getUrl());
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pf.getFile()));
                    } catch (
                            ParseException e
                            )

                    {
                        e.printStackTrace();
                    }

                    sharingIntent.setType("image/*");
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    mContext.startActivity(Intent.createChooser(sharingIntent, "Share Image!"));

                }
            });
        }
    }

    private void setImages(Event evnt) {

        pObject = evnt.getMediaObject();
        if(pObject != null) {
            List<ParseFile> pFileList = null;
            try {
                pFileList = (ArrayList<ParseFile>) pObject.fetchIfNeeded().get("mediaFiles");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (null != pFileList && !pFileList.isEmpty()) {
                for (int i = 0; i < pFileList.size(); i++) {

                    ParseFile pFile = pFileList.get(i);

                    byte[] bitmapdata = new byte[0];  // here it throws error
                    try {
                        bitmapdata = pFile.getData();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                    cell = LayoutInflater.from(mContext).inflate(R.layout.cell_list, null);

                    final ImageView imageView = (ImageView) cell.findViewById(R.id._image);
                    imageView.setImageResource(android.R.color.transparent);
                    TextView text = (TextView) cell.findViewById(R.id._imageName);
                    //Glide.with(getContext()).load(pFile.getUrl()).centerCrop().into(imageView);
                    imageView.setImageBitmap(bitmap);
                    //text.setText("#" + (i + 1));

                    //Glide.with(mContext).load(pFile.getUrl()).centerCrop().into(imageView);
                    mainLayout.addView(cell);

                }
            }
        }
    }

//    private void callShare() {
//
//        if(event.isEnrolled()){
//            viewHolder.ivSubscribe.setImageResource(R.mipmap.ic_check_circle);
//        }else {
//            viewHolder.ivSubscribe.setImageResource(R.mipmap.ic_check_circle_outline);
//            // Add click listener
//            viewHolder.ivSubscribe.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(User.isUserLoggedIn()){
//                        String hostUser = event.getHost().getObjectId();
//                        String loggedInUser = User.getLoggedInUser().getObjectId();
//                        if (!hostUser.equals(loggedInUser)) {
//                            mEventListCallback.onSubscribeCallback(viewHolder.position);
//                        }
//                    }
//                }
//            });
//        }
//
//
//    }


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
