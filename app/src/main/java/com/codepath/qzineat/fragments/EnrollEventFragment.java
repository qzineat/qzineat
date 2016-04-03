package com.codepath.qzineat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.qzineat.R;
import com.codepath.qzineat.adapters.EventsPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/6/16.
 */
public class EnrollEventFragment extends BaseFragment {



    @Bind(R.id.pager) ViewPager vpPager;
    @Bind(R.id.tabs) PagerSlidingTabStrip tabStrip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Selected item in drawer..
        drawer.setSelection(userEventsItem, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroll_event, container, false);
        ButterKnife.bind(this, view);

        if (toolbar != null) {
            toolbar.setLogo(R.drawable.ic_qzineat_logo_final);
        }

        vpPager.setAdapter(new EventsPagerAdapter(getChildFragmentManager()));
        tabStrip.setViewPager(vpPager);


        return view;
    }
}
