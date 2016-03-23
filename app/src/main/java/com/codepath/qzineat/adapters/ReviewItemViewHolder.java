package com.codepath.qzineat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.android.qzineat.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/12/16.
 */
public class ReviewItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvComment) public TextView tvComment;
    @Bind(R.id.tvReviewedBy) public TextView tvReviewedBy;
    @Bind(R.id.ivProfileImage) public ImageView ivProfileImage;


    public ReviewItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
