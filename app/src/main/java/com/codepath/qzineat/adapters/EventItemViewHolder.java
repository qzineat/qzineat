package com.codepath.qzineat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.android.qzineat.R;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by Shyam Rokde on 3/2/16.
 */

public class EventItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.ivEventImage) public ImageView ivEventImage;
    @Bind(R.id.tvTitle) public TextView tvTitle;
    @Bind(R.id.tvLocality) public TextView tvLocality;
    @Bind(R.id.tvEventDate) public TextView tvEventDate;

    public EventItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
