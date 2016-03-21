package com.codepath.qzineat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.qzineat.interfaces.CommunicationChannel;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.adapters.EventsPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/6/16.
 */
public class EnrollEventFragment extends Fragment{

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.pager) ViewPager vpPager;
    @Bind(R.id.tabs) PagerSlidingTabStrip tabStrip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroll_event, container, false);
        ButterKnife.bind(this, view);

        vpPager.setAdapter(new EventsPagerAdapter(getChildFragmentManager()));
        tabStrip.setViewPager(vpPager);

        // This is for drawer
        mCommunicationChannelListener.attachDrawer(toolbar, true);

        return view;
    }

    CommunicationChannel mCommunicationChannelListener = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((AppCompatActivity) context).setSupportActionBar(toolbar);
        if(context instanceof CommunicationChannel){
            mCommunicationChannelListener = (CommunicationChannel) context;
        }
    }
}
