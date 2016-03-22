package com.codepath.qzineat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;

import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/20/16.
 */
public class HomeFragment extends BaseFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Selected item in drawer..
        drawer.setSelection(eventsItem, false);

        EventListFragment fragment = new  EventListFragment();

        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flEventListContent, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        ButterKnife.bind(this, view);


        /*CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Shyam");

        loadBackdrop(view);*/

        /*CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.drawer_event));

        loadBackdrop(view);*/


        //drawer.setToolbar(getActivity(), toolbar);



        //mCommunicationChannelListener.attachDrawer(toolbar);
        loadBackdrop(view);


        return view;
    }



    private void loadBackdrop(View view) {
        final ImageView imageView = (ImageView) view.findViewById(R.id.backdrop);
        Glide.with(getContext()).load(R.drawable.cheese_2).centerCrop().into(imageView);
    }


}
