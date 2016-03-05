package com.codepath.qzineat.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.Event;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EventDetailActivity extends AppCompatActivity {

    @Bind(R.id.ivEventImage) ImageView ivEventImage;
    @Bind(R.id.ivProfileImage) ImageView ivProfileImage;
    @Bind(R.id.tvTitle) TextView tvTitle;
    @Bind(R.id.tvDate) TextView tvDate;
    @Bind(R.id.tvLocation) TextView tvLocation;
    @Bind(R.id.tvAvailability) TextView tvAvailability;
    @Bind(R.id.tvGuestCount) TextView tvGuestCount;
    @Bind(R.id.tvDescription) TextView tvDescription;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fabSignUp) FloatingActionButton fabSignUp;

    private Event event;
    private String eventObjectId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Get Event
        getEvent();

        // Sign Up
        setupSignUpButton();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    // Display selected Event
    private void getEvent() {
        eventObjectId = getIntent().getStringExtra("eventObjectId");

        // Construct query to execute
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.getInBackground(eventObjectId, new GetCallback<Event>() {
            @Override
            public void done(Event object, ParseException e) {
                if (e == null) {
                    event = object;
                    // Start Loading Data here
                    populateEvent();
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

        try {

        }catch (Exception ex){
            ex.printStackTrace();
        }


        //tvAvailability.setText(event.getGuestLimit() - event.getSignupCount());
        //tvGuestCount.setText(event.getGuestLimit());
        tvDescription.setText(event.getDescription());
    }

    private void setupSignUpButton() {
        fabSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: We have to ask user to Login - But Lets add user in Event table and then worry abt that later
                //ParseUser.is
                Toast.makeText(EventDetailActivity.this, "Cliecked on SignUp... Let me do something", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
