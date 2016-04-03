package com.codepath.qzineat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.qzineat.R;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by Shyam Rokde on 3/2/16.
 */

public class EventItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.ivEventImage1) public ImageView ivEventImage1;
//    @Bind(R.id.ivEventImage2) public ImageView ivEventImage2;
//    @Bind(R.id.ivEventImage3) public ImageView ivEventImage3;
//    @Bind(R.id.ivEventImage4) public ImageView ivEventImage4;
    @Bind(R.id.tvTitle) public TextView tvTitle;
    @Bind(R.id.tvLocality) public TextView tvLocality;
    @Bind(R.id.tvEventDate) public TextView tvEventDate;
    @Bind(R.id.tvPrice) public TextView tvPrice;
    @Bind(R.id.ivShare) public ImageView ivShare;
    @Bind(R.id.ivSubscribe) public ImageView ivSubscribe;

    public int position;

    public EventItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
