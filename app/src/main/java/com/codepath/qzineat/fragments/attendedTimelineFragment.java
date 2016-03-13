package com.codepath.qzineat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by glondhe on 3/12/16.
 */
public class attendedTimelineFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//
//        UserEventsFragment userEventsFragment = new UserEventsFragment();
//        Bundle args = new Bundle();
//        args.putBoolean("isSubscriberView", true);
//        userEventsFragment.setArguments(args);
//
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.flContainer, userEventsFragment);
//        ft.commit();
        return container;
    }
}
