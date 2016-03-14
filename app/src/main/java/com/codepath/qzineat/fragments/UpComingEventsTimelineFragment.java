package com.codepath.qzineat.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.codepath.android.qzineat.R;

/**
 * Created by glondhe on 3/13/16.
 */
public class UpComingEventsTimelineFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserEventsFragment userEventsFragment = new UserEventsFragment();
        Bundle args = new Bundle();
        args.putBoolean("isProfileView", true);
        userEventsFragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.flContainerHEL, userEventsFragment);
        ft.commit();
    }

}
