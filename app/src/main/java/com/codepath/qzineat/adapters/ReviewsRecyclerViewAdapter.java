package com.codepath.qzineat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.Review;

import java.util.ArrayList;

/**
 * Created by Shyam Rokde on 3/12/16.
 */
public class ReviewsRecyclerViewAdapter extends RecyclerView.Adapter<ReviewItemViewHolder> {

    public ArrayList<Review> mReview;
    public Context mContext;

    public ReviewsRecyclerViewAdapter(ArrayList<Review> reviews, Context context) {
        mReview = reviews;
        mContext = context;
    }


    @Override
    public ReviewItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate custom layout
        View resultView = inflater.inflate(R.layout.item_review, parent, false);
        // Return new holder instance
        return new ReviewItemViewHolder(resultView);
    }

    @Override
    public void onBindViewHolder(final ReviewItemViewHolder viewHolder, int position) {
        // 1. Get Event
        Review review = mReview.get(position);
        // 2. Populate user interface
        viewHolder.tvComment.setText(review.getComment());
        if(review.getReviewedBy().getProfileName() != null && !review.getReviewedBy().getProfileName().isEmpty()){
            viewHolder.tvReviewedBy.setText(review.getReviewedBy().getProfileName());
            if (null != review.getReviewedBy().getImageFile() && !review.getReviewedBy().getImageFile().getUrl().isEmpty())
                Glide.with(mContext).load(review.getReviewedBy().getImageFile().getUrl()).into(viewHolder.ivProfileImage);
            else
                Glide.with(mContext).load(R.mipmap.ic_profile_placeholder).into(viewHolder.ivProfileImage);
        }
    }


    @Override
    public int getItemCount() {
        return mReview.size();
    }
}
