package com.codepath.qzineat.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.codepath.qzineat.adapters.ReviewsRecyclerViewAdapter;
import com.codepath.qzineat.adapters.WrapContentLinearLayoutManager;
import com.codepath.qzineat.models.Attendee;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.models.Review;
import com.codepath.qzineat.models.User;
import com.codepath.qzineat.utils.QZinUtil;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @Bind(R.id.tvPrice) TextView tvPrice;
    @Bind(R.id.tvAttendeesMaxCount) TextView tvAttendeesMaxCount;
    @Bind(R.id.tvDescription) TextView tvDescription;
    @Bind(R.id.tvAlcohol) TextView tvAlcohol;

    // Review
    @Bind(R.id.ratingBar) RatingBar ratingBar;
    @Bind(R.id.tvStarLabel) TextView tvStarLabel;
    @Bind(R.id.tvHr) TextView tvHr;
    @Bind(R.id.etReviewComment) EditText etReviewComment;
    @Bind(R.id.btnSubmit) Button btnSubmit;
    @Bind(R.id.rvReviews) RecyclerView rvReviews;

    @Bind(R.id.fabSignUp) FloatingActionButton fabSignUp;

    private Event event;
    private String eventObjectId;

    private static int FRAGMENT_CODE = 100;
    private String host;


    private ArrayList<Review> mReviews;
    private ReviewsRecyclerViewAdapter recyclerViewAdapter;


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

        mReviews = new ArrayList<>();
        recyclerViewAdapter = new ReviewsRecyclerViewAdapter(mReviews, getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        ButterKnife.bind(this, view);

        // Setup RecyclerView
        setupRecyclerView();

        // Get Event
        getEvent();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupRecyclerView() {
        rvReviews.setAdapter(recyclerViewAdapter);

        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        rvReviews.setLayoutManager(layoutManager);

        /*rvEvents.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (lastCreatedAt != null) {
                    getEvents();
                }
            }
        });*/

    }

    // Display selected Event
    private void getEvent() {
        // Construct query to execute
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.include("host");
        query.getInBackground(eventObjectId, new GetCallback<Event>() {

            @Override
            public void done(Event object, ParseException e) {
                if (e == null) {
                    event = object;
                    // Start Loading Data here
                    populateEvent();
                    // Fab Button
                    setupFabButton();
                    // Get Reviews Async if possible
                    getReviews();
                }
            }
        });
        // TODO: Add Reviews Here..
    }

    private Date reviewLastCreatedAt;

    private void getReviews(){
        ParseQuery<Review> query = ParseQuery.getQuery(Review.class);
        query.include("reviewedBy");
        query.whereEqualTo("event", event);
        query.setLimit(3);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Review>() {
            @Override
            public void done(List<Review> reviewList, ParseException e) {
                if(e==null){
                    Log.d("DEBUG", "Review size -" + reviewList.size());
                    int curSize = recyclerViewAdapter.getItemCount();
                    ArrayList<Review> arrayList = new ArrayList<>(reviewList);
                    mReviews.addAll(arrayList);
                    recyclerViewAdapter.notifyItemRangeInserted(curSize, arrayList.size());
                    if (reviewList.size() > 0) {
                        // set value for pagination
                        reviewLastCreatedAt = mReviews.get(mReviews.size() - 1).getCreatedAt();
                    }
                }

            }
        });
    }

    private void populateEvent() {

        Glide.with(this).load(R.mipmap.ic_profile_placeholder).centerCrop().into(ivProfileImage);

        tvTitle.setText(event.getTitle());
        tvDate.setText(QZinUtil.getShortDate(event.getDate()));

        if(event.getAddress() != null) {
            tvLocation.setText(event.getAddress());
        }else{
            tvLocation.setVisibility(View.GONE);
        }

        if(event.getPrice() > 0){
            tvPrice.setText(String.format("$%d", event.getPrice()));
        }else {
            tvPrice.setText("FREE");
        }

        int availability = event.getAttendeesMaxCount() - event.getAttendeesAvailableCount();
        setAvalCount(availability);
        tvAlcohol.setText(event.getAlcohol());
        tvDescription.setText(event.getDescription());
        ParseFile pf = event.getImageFile();
        Glide.with(this).load(pf.getUrl()).centerCrop().into(ivEventImage);

    }

    public void setAvalCount(int availability){
        tvAttendeesMaxCount.setText(String.format("%d avl./%d", availability, event.getAttendeesMaxCount()));
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
                } else {
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
        tvHr.setVisibility(View.VISIBLE);
    }
    private void hideReviewSubmit(){
        tvStarLabel.setVisibility(View.GONE);
        etReviewComment.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);
        tvHr.setVisibility(View.GONE);
    }
    private void showRatingBar(){
        ratingBar.setVisibility(View.VISIBLE);
        tvHr.setVisibility(View.VISIBLE);
    }
    private void hideRatingBar(){
        ratingBar.setVisibility(View.GONE);
        tvHr.setVisibility(View.GONE);
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
        host = event.getHost().getUsername();

        // Save attendee
        attendee.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null) {
                    int avl = event.getAttendeesMaxCount() - event.getAttendeesAvailableCount() - 1;
                    setAvalCount(avl); // update text box
                    // save event
                    event.addAttendee(attendee);
                    event.setAttendeesAvailableCount(avl);
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

        // Lets Send Notification
        sendNotification();
    }

    private void sendNotification() {
        // Associate the device with a user

        ParsePush parsePush = new ParsePush();
        String id = ParseInstallation.getCurrentInstallation().getInstallationId();
        Log.d("Debug_id", id);
        Log.d("Debug_UserName", host);
        ParseQuery pQuery = ParseInstallation.getQuery(); // <-- Installation query
        Log.d("Debug_pQuery", pQuery.toString());
        pQuery.whereEqualTo("username", host); // <-- you'll probably want to target someone that's not the current user, so modify accordingly
        parsePush.sendMessageInBackground("Hey your Event has a subscriber", pQuery);
    }

    private void changeSignUpButton(){
        fabSignUp.setBackgroundColor(Color.GREEN);
        fabSignUp.setVisibility(View.VISIBLE);
        fabSignUp.setEnabled(false);
        fabSignUp.setImageResource(R.drawable.ic_check);
    }
}
