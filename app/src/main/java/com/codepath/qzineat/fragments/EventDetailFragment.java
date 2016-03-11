package com.codepath.qzineat.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.Attendee;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.models.Review;
import com.codepath.qzineat.models.User;
import com.parse.CountCallback;
import com.parse.GetCallback;
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
    // Review
    @Bind(R.id.ratingBar) RatingBar ratingBar;
    @Bind(R.id.tvStarLabel) TextView tvStarLabel;
    @Bind(R.id.etReviewComment) EditText etReviewComment;
    @Bind(R.id.btnSubmit) Button btnSubmit;


    @Bind(R.id.fabSignUp) FloatingActionButton fabSignUp;

    private Event event;
    private String eventObjectId;

    private static int FRAGMENT_CODE = 100;
    private String profileType;

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
        ParseFile pf = event.getImageFile();
        Glide.with(this).load(pf.getUrl()).centerCrop().into(ivEventImage);

    }

    /**
     * This will do sign up or login
     */
    private void setupFabButton() {

        if(User.isUserLoggedIn()){
            // Hello :) I am host - don't show me SignUp Button
            if(event.getHost() != null
                    && event.getHost().getObjectId().equals(User.getLoggedInUser().getObjectId())){
                fabSignUp.setVisibility(View.GONE);
                return;
            }

            // Check I already Registered for this event or not
            ParseQuery<Attendee> query = ParseQuery.getQuery(Attendee.class);
            query.whereEqualTo("event", event);
            query.whereEqualTo("user", User.getLoggedInUser());
            query.countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if (e == null) {
                        if (count > 0) {
                            // I am already registered for this event
                            changeSignUpButton();
                            // Rating
                            setupRatingBar();
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

    private void setupRatingBar() {
        // Hello :) I am host - don't show me Review Button
        if(event.getHost() != null
                && event.getHost().getObjectId().equals(User.getLoggedInUser().getObjectId())){
            hideReviewSubmit();
            hideRatingBar();
            return;
        }

        // Check Date
        Date today = new Date();
        if(today.compareTo(event.getDate()) <= 0){
            hideReviewSubmit();
            hideRatingBar();
            return;
        }

        // Check User Already reviewed or not
        ParseQuery<Review> query = ParseQuery.getQuery(Review.class);
        query.whereEqualTo("reviewedBy", User.getLoggedInUser());
        query.whereEqualTo("event", event);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (count == 0) {
                    // I haven't reviewed yet
                    showRatingBar();
                    showReviewSubmit();

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Save Event
                            int rating = Math.round(ratingBar.getRating());
                            String comment = etReviewComment.getText().toString();
                            saveReview(rating, comment);
                        }
                    });
                }else {
                    setRating();
                }
            }
        });
    }

    private void saveReview(int rating, String comment){
        // Update Event
        event.increment("numberOfReviews");
        event.increment("ratingSum", rating);
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    setRating();
                }
            }
        });

        // Save Review
        Review review = new Review();
        review.setComment(comment);
        review.setEvent(event);
        review.setReviewedBy(User.getLoggedInUser());
        review.setRating(rating);
        review.saveInBackground();
    }
    private void showReviewSubmit(){
        tvStarLabel.setVisibility(View.VISIBLE);
        etReviewComment.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.VISIBLE);
    }
    private void hideReviewSubmit(){
        tvStarLabel.setVisibility(View.GONE);
        etReviewComment.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);
    }
    private void showRatingBar(){
        ratingBar.setVisibility(View.VISIBLE);
    }
    private void hideRatingBar(){
        ratingBar.setVisibility(View.GONE);
    }

    private void setRating(){
        hideReviewSubmit();
        showRatingBar();

        double d = event.getRatingSum() * 1.0 / event.getNumberOfReviews();
        ratingBar.setRating((float) d);
        ratingBar.setIsIndicator(true);
    }

    private void showFabRegister(){
        fabSignUp.setVisibility(View.VISIBLE);
        fabSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (User.isUserLoggedIn()) {
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
        attendee.setUser(User.getLoggedInUser());
        attendee.setEvent(event);

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
