package com.codepath.qzineat.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.Attendee;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.utils.UserUtil;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/6/16.
 */
public class EventDetailFragment extends Fragment {

    @Bind(R.id.ivEventImage) ImageView ivEventImage;
    @Bind(R.id.ivProfileImage) ImageView ivProfileImage;
    @Bind(R.id.tvTitle) TextView tvTitle;
    @Bind(R.id.tvDate) TextView tvDate;
    @Bind(R.id.tvLocation) TextView tvLocation;
    @Bind(R.id.tvAvailability) TextView tvAvailability;
    @Bind(R.id.tvGuestCount) TextView tvGuestCount;
    @Bind(R.id.tvDescription) TextView tvDescription;

    @Bind(R.id.fabSignUp) FloatingActionButton fabSignUp;

    private Event event;
    private String eventObjectId;

    private static int FRAGMENT_CODE = 100;

    public static EventDetailFragment newInstance(String eventObjectId){
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putString("eventObjectId", eventObjectId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventObjectId = getArguments().getString("eventObjectId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        ButterKnife.bind(this, view);

        // Get Event
        getEvent();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Display selected Event
    private void getEvent() {
        // Construct query to execute
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        // TODO: check later that we can get Event with all attendees or not
        query.getInBackground(eventObjectId, new GetCallback<Event>() {
            @Override
            public void done(Event object, ParseException e) {
                if (e == null) {
                    event = object;
                    // Start Loading Data here
                    populateEvent();
                    // Fab Button
                    setupFabButton();
                }
            }
        });
    }



    private void populateEvent() {
        Glide.with(this).load(event.getImageUrl()).centerCrop().into(ivEventImage);
        Glide.with(this).load(R.mipmap.ic_profile_placeholder).centerCrop().into(ivProfileImage);

        tvTitle.setText(event.getTitle());

        DateFormat dateFormat = new SimpleDateFormat("MMMM F @KK:mm a", Locale.US);
        tvDate.setText(dateFormat.format(new Date()));

        if(event.getAddress() != null) {
            tvLocation.setText(event.getAddress());
        }else{
            tvLocation.setVisibility(View.GONE);
        }

        tvGuestCount.setText(String.valueOf(event.getGuestLimit()));

        tvDescription.setText(event.getDescription());

        //Retrieve Image
        ParseFile imageFile = event.getImageFile();
        if( imageFile != null) {
            imageFile.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        // data has the bytes for the resume
                        Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        ivEventImage.setImageBitmap(bMap);
                        // Tried using Glide. But it runs into error below:
                        // ERROR: You must provide a Model of a type for which there is a registered ModelLoader, if you are using a custom model, you must first call Glide#register with a ModelLoaderFactory for your custom model class
                        // Glide.with(mContext).load(bMap).asBitmap().centerCrop().into(viewHolder.ivEventImage);
                    } else {
                        Log.d("DEBUG", "Parse exception: " + e.toString());
                    }
                }
            });
        }
    }

    /**
     * This will do sign up or login
     */
    private void setupFabButton() {

        if(UserUtil.isUserLoggedIn()){
            // Hello :) I am host - don't show me SignUp Button
            if(event.getHost() != null
                    && event.getHost().getObjectId().equals(UserUtil.getLoggedInUser().getObjectId())){
                fabSignUp.setVisibility(View.GONE);
                return;
            }

            // Check I already Registered for this event or not
            ParseQuery<Attendee> query = ParseQuery.getQuery(Attendee.class);
            query.whereEqualTo("eventId", eventObjectId);
            query.whereEqualTo("user", UserUtil.getLoggedInUser());
            query.countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if (e == null) {
                        if (count > 0) {
                            // I am already registered for this event
                            changeSignUpButton();
                        }else {
                            showFabRegister();
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            showFabRegister();
        }

    }

    private void showFabRegister(){
        fabSignUp.setVisibility(View.VISIBLE);
        fabSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserUtil.isUserLoggedIn()) {
                    saveAttendee();
                } else {
                    Fragment fragment = new LoginFragment();
                    fragment.setTargetFragment(EventDetailFragment.this, FRAGMENT_CODE);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.flContent, fragment);
                    transaction.addToBackStack(null);
                    // Commit the transaction
                    transaction.commit();
                }
            }
        });
    }

    private void saveAttendee(){
        // Change Button
        changeSignUpButton();

        // Parse Save
        final Attendee attendee = new Attendee();
        attendee.setGuestCount(1); // TODO: Change later for adding more guests
        attendee.setUser(UserUtil.getLoggedInUser());
        attendee.setEventId(event.getObjectId());

        // Save attendee
        attendee.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null) {
                    // associate event
                    event.addAttendee(attendee);
                    event.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), getString(R.string.somthing_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    ex.printStackTrace();
                    Toast.makeText(getContext(), getString(R.string.somthing_went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeSignUpButton(){
        fabSignUp.setBackgroundColor(Color.GREEN);
        fabSignUp.setVisibility(View.VISIBLE);
        fabSignUp.setEnabled(false);
        fabSignUp.setImageResource(R.drawable.ic_check);
    }
}
