package com.codepath.qzineat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.adapters.EventsPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/6/16.
 */
public class EnrollEventFragment extends Fragment{


    @Bind(R.id.pager) ViewPager vpPager;
    @Bind(R.id.tabs) PagerSlidingTabStrip tabStrip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroll_event, container, false);
        ButterKnife.bind(this, view);

        vpPager.setAdapter(new EventsPagerAdapter(getChildFragmentManager()));
        tabStrip.setViewPager(vpPager);

        return view;
    }
}
