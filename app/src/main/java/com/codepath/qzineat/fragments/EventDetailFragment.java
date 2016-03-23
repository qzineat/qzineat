package com.codepath.qzineat.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.activities.LoginActivity;
import com.codepath.qzineat.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.qzineat.adapters.ReviewsRecyclerViewAdapter;
import com.codepath.qzineat.adapters.WrapContentLinearLayoutManager;
import com.codepath.qzineat.dialogs.EnrollDialogFragment;
import com.codepath.qzineat.dialogs.ReviewDialogFragment;
import com.codepath.qzineat.models.Attendee;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.models.Review;
import com.codepath.qzineat.models.User;
import com.codepath.qzineat.utils.FragmentCode;
import com.codepath.qzineat.utils.QZinUtil;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/6/16.
 */
public class EventDetailFragment extends BaseFragment {

//    @Bind(R.id.ivEventImage) ImageView ivEventImage;
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
    @Bind(R.id.rvReviews) RecyclerView rvReviews;
    @Bind(R.id.llReview) LinearLayout llReview;
    @Bind(R.id.llReviewBar) LinearLayout llReviewBar;

    @Bind(R.id.fabSignUp) FloatingActionButton fabSignUp;

    private Event event;
    private String eventObjectId;

    private static int FRAGMENT_CODE = 100;
    private View cell;
    private TextView text;
    private ParseObject pObject;
    private Bitmap bitmap;

    private ArrayList<Review> mReviews;
    private ReviewsRecyclerViewAdapter recyclerViewAdapter;
    private LinearLayout mainLayout;

    private Context context;

    Transformation transformation = new RoundedTransformationBuilder()
            .oval(true).cornerRadius(1)
            .borderColor(Color.WHITE)
            .borderWidth(3)
            .build();

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
        final View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        ButterKnife.bind(this, view);

        context = getActivity().getApplicationContext();

        // Setup RecyclerView
        setupRecyclerView();

        mainLayout = (LinearLayout) view.findViewById(R.id._linearLayout);

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("objectId", event.getHost().getObjectId());
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                profileFragment.setArguments(bundle);
                transaction.replace(R.id.flContent, profileFragment);
                transaction.commit();
            }
        });

        // Get Event
        getEvent();

        toolbar.setTitle("Event Detail");

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Enroll for event
        if(resultCode == FragmentCode.ENROLL_DIALOG_FRAGMENT_RESULT_CODE){
            Log.d("DEBUG", "Message Received on Enroll..");
            saveAttendee(data.getIntExtra("guestCount", 1));
        }

        // Save Review
        if(resultCode == FragmentCode.EVENT_DETAIL_REVIEW_FRAGMENT_RESULT_CODE){
            Log.d("DEBUG", "Message Received on Review..");

            saveReview(data.getIntExtra("rating", 1), data.getStringExtra("comment"));
        }

    }

    private void setupRecyclerView() {
        rvReviews.setAdapter(recyclerViewAdapter);

        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        rvReviews.setLayoutManager(layoutManager);

        rvReviews.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (reviewLastCreatedAt != null) {
                    getReviews();
                }
            }
        });

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
        if(reviewLastCreatedAt != null){
            query.whereLessThan("createdAt", reviewLastCreatedAt);
        }
        query.findInBackground(new FindCallback<Review>() {
            @Override
            public void done(List<Review> reviewList, ParseException e) {
                if (e == null) {
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

        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo("objectId", event.getHost().getObjectId());
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> UserList, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Review size -" + UserList.size());
                    ArrayList<User> arrayList = new ArrayList<>(UserList);

                    Log.d("DEBUG", "User object" + arrayList.get(0).getEmail());
                    Log.d("DEBUG", "User object" + arrayList.get(0).getImageFile());

                    Picasso.with(getContext()).load(arrayList.get(0).getImageFile().getUrl()).transform(transformation).into(ivProfileImage);
                } else
                    Picasso.with(getContext()).load(R.mipmap.ic_profile_placeholder).transform(transformation).into(ivProfileImage);
            }
        });


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
        String imgUrl;
        if(pf != null && !pf.getUrl().isEmpty()){
            imgUrl = pf.getUrl();
        }else {
            imgUrl = QZinUtil.getQZinImageUrl();
        }
        //Glide.with(getContext()).load(imgUrl).centerCrop().into(ivEventImage);
        setImages(event);
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
                    cell = LayoutInflater.from(getContext()).inflate(R.layout.cell_list, null);
                    final ImageView imageView = (ImageView) cell.findViewById(R.id._image);
                    imageView.setImageResource(android.R.color.transparent);
                    bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);


                    Glide.with(getContext()).load(pFile.getUrl()).centerCrop().into(imageView);
                    //imageView.setImageBitmap(bitmap);
                    //text.setText("#" + (i + 1));
                    //Glide.with(mContext).load(pFile.getUrl()).centerCrop().into(imageView);
                    mainLayout.addView(cell);

                }
            } else {
                    cell = LayoutInflater.from(getContext()).inflate(R.layout.cell_list, null);
                    final ImageView imageView = (ImageView) cell.findViewById(R.id._image);
                    imageView.setImageResource(android.R.color.transparent);
                    Glide.with(getContext()).load(QZinUtil.getQZinImageUrl()).centerCrop().into(imageView);
                    mainLayout.addView(cell);
            }
        }
    }



    public void setAvalCount(int availability){
        tvAttendeesMaxCount.setText(String.format("%d seats left of %d", availability, event.getAttendeesMaxCount()));
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
            hideReviewButton();
            hideRatingBar();
            return;
        }

        // Check Date
        Date today = new Date();
        if(today.compareTo(event.getDate()) <= 0){
            hideReviewButton(); // Btn
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
                if(e!=null){
                    // TODO: network fail
                    e.printStackTrace();
                    return;
                }

                if (count == 0) {
                    // I haven't reviewed yet & show dialog
                    showReviewButton();
                    llReview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("DEBUG", "User clicked on me..");
                            // 1. Open Dialog
                            ReviewDialogFragment reviewDialogFragment = new ReviewDialogFragment();
                            reviewDialogFragment.setTargetFragment(EventDetailFragment.this, FragmentCode.EVENT_DETAIL_REVIEW_FRAGMENT_RESULT_CODE);
                            reviewDialogFragment.show(getFragmentManager(), FragmentCode.TAG_REVIEW);
                        }
                    });
                } else {
                    setRating();
                }
            }
        });
    }

    private void hideReviewButton(){
        llReview.setVisibility(View.GONE);
    }
    private void showReviewButton(){
        llReview.setVisibility(View.VISIBLE);
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

        // Add Review in List
        mReviews.add(0, review);
        recyclerViewAdapter.notifyItemInserted(0);
    }


    private void showRatingBar(){
        llReviewBar.setVisibility(View.VISIBLE);
        ratingBar.setIsIndicator(true);
        hideReviewButton();
    }
    private void hideRatingBar(){
        llReviewBar.setVisibility(View.GONE);
    }

    private void setRating(){
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
                    // TODO: Add Fragment
                    Bundle args = new Bundle();
                    args.putDouble("price", event.getPrice());

                    EnrollDialogFragment enrollDialogFragment = new EnrollDialogFragment();
                    enrollDialogFragment.setArguments(args);
                    enrollDialogFragment.setTargetFragment(EventDetailFragment.this, FragmentCode.ENROLL_DIALOG_FRAGMENT_RESULT_CODE);
                    enrollDialogFragment.show(getFragmentManager(), FragmentCode.TAG_ENROLL);
                } else {
                    /*Fragment fragment = new LoginFragment();
                    fragment.setTargetFragment(EventDetailFragment.this, FRAGMENT_CODE);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.flContent, fragment);
                    transaction.addToBackStack(null);
                    // Commit the transaction
                    transaction.commit();*/
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void saveAttendee(final int guestCount){
        // Change Button
        changeSignUpButton();

        // Parse Save
        final Attendee attendee = new Attendee();
        attendee.setGuestCount(guestCount); // TODO: Change later for adding more guests
        attendee.setSubscribedBy(User.getLoggedInUser());
        attendee.setEvent(event);


        // Save attendee
        attendee.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null) {
                    int avl;
                    if(guestCount == 0){
                        avl = event.getAttendeesMaxCount() - event.getAttendeesAvailableCount() - 1;
                    }else {
                        avl = event.getAttendeesMaxCount() - event.getAttendeesAvailableCount() - guestCount;
                    }

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
        Log.d("Debug_UserName", event.getHost().getUsername()); // host = event.getHost().getUsername();
        ParseQuery pQuery = ParseInstallation.getQuery(); // <-- Installation query
        Log.d("Debug_pQuery", pQuery.toString());
        pQuery.whereEqualTo("username", event.getHost().getUsername()); // <-- you'll probably want to target someone that's not the current user, so modify accordingly
        parsePush.sendMessageInBackground("Hey your Event has a subscriber", pQuery);
    }

    private void changeSignUpButton(){
        fabSignUp.setBackgroundColor(Color.GREEN);
        fabSignUp.setVisibility(View.VISIBLE);
        fabSignUp.setEnabled(false);
        fabSignUp.setImageResource(R.drawable.ic_check);
    }


}
