package com.codepath.qzineat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.android.qzineat.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Gaurav Londhe on 3/10/16.
 */
public class UserItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvTitle) public TextView tvTitle;
    @Bind(R.id.tvCity) public TextView tvCity;
    @Bind(R.id.tvDate) public TextView tvDate;
    @Bind(R.id.evEdit) public ImageView evEdit;
    @Bind(R.id.ivEventImage) public ImageView ivEventImage;

    public UserItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        evEdit.setVisibility(View.INVISIBLE);
    }
}
