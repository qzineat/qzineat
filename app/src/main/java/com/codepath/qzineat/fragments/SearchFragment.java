package com.codepath.qzineat.fragments;

import android.os.Bundle;

/**
 * Created by glondhe on 3/8/16.
 */
public class SearchFragment extends EventListFragment {
    public SearchFragment client;

    public static SearchFragment newInstance(String queryString) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("queryString", queryString);
        args.putString("cityName", queryString);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getEvents(getArguments().getString("queryString"),getArguments().getString("cityName") );
    }

}
