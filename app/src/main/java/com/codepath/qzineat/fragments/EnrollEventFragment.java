package com.codepath.qzineat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.android.qzineat.R;

import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/6/16.
 */
public class EnrollEventFragment extends Fragment{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroll_event, container, false);
        ButterKnife.bind(this, view);

        //ParseFile parseFile = User.getLoggedInUser().getImageFile();
        //Glide.with(this).load(parseFile.getUrl()).centerCrop().into(ivProfileImage);

        ViewPager vpPager = (ViewPager) view.findViewById(R.id.pager);
        vpPager.setAdapter(new TweetsPagerAdapter(getActivity().getSupportFragmentManager()));
        vpPager.getAdapter().notifyDataSetChanged();
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);

        return view;
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = {"Upcoming Events", "Past Events"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new UpComingEventsTimelineFragment();
            } else if (position == 1) {
                return new HostedEventsTimelineFragment();
            } else return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

    }
}
