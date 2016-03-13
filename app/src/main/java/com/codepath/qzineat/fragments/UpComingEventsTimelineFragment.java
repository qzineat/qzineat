package com.codepath.qzineat.fragments;

import android.os.Bundle;

/**
 * Created by glondhe on 3/13/16.
 */
public class UpComingEventsTimelineFragment extends UserEventsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSubscriberView = true;
    }

}
