package com.codepath.qzineat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.activities.EventListActivity;
import com.flaviofaria.kenburnsview.KenBurnsView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/20/16.
 */
public class HomeFragment extends BaseFragment {


    @Bind(R.id.fabSearch) FloatingActionButton fabSearch;
    @Bind(R.id.backdrop) KenBurnsView backdrop;

    Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Selected item in drawer..
        drawer.setSelection(eventsItem, false);

        HomeEventListFragment fragment = new  HomeEventListFragment();
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


        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EventListActivity.class);
                startActivity(i);
            }
        });



        loadBackdrop();

        return view;
    }



    private void loadBackdrop() {
        //final ImageView imageView = (ImageView) view.findViewById(R.id.backdrop);
        //Glide.with(getContext()).load(R.drawable.home_image_three).centerCrop().into(imageView);
        Glide.with(getContext()).load(R.drawable.home_image_one).centerCrop().into(backdrop);
    }
}
