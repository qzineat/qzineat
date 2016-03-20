package com.codepath.qzineat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.qzineat.fragments.PastEventsFragment;
import com.codepath.qzineat.fragments.UpComingEventsFragment;

/**
 * Created by Shyam Rokde on 3/19/16.
 */
public class EventsPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = {"Upcoming Events", "Past Events"};

    public EventsPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new UpComingEventsFragment();
        } else if (position == 1) {
            return new PastEventsFragment();
        }

        return null;
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
