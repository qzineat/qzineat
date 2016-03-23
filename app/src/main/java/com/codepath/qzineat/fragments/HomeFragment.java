package com.codepath.qzineat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.activities.SearchActivity;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.flaviofaria.kenburnsview.Transition;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/20/16.
 */
public class HomeFragment extends BaseFragment implements KenBurnsView.TransitionListener {


    @Bind(R.id.fabSearch) FloatingActionButton fabSearch;
    @Bind(R.id.backdrop) KenBurnsView backdrop;
    @Bind(R.id.viewSwitcher) ViewSwitcher mViewSwitcher;
    @Bind(R.id.backdrop2) KenBurnsView backdrop2;

    Handler handler;
    private static final int TRANSITIONS_TO_SWITCH = 3;
    private int mTransitionsCount = 0;
    private static final int TRANSITION_DURATION = 20000; // Millisecond
    private RandomTransitionGenerator generator;

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

        // Transition Generator
        generator = new RandomTransitionGenerator();
        generator.setTransitionDuration(TRANSITION_DURATION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        ButterKnife.bind(this, view);



        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);



        /*CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Shyam");

        loadBackdrop(view);*/

        /*

        loadBackdrop(view);*/

        /*handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        hideProgressBar();
                    }
                };

                handler.postDelayed(r, 5000);*/


        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SearchActivity.class);
                startActivity(i);
            }
        });

        loadBackdrop();

        if(toolbar!=null){
            toolbar.setLogo(R.drawable.ic_qzineat_logo_final);
        }

        return view;
    }


    private void loadBackdrop() {

        backdrop.setTransitionListener(this);
        backdrop2.setTransitionListener(this);

        backdrop.setTransitionGenerator(generator);
        backdrop.setTransitionGenerator(generator);


        //final ImageView imageView = (ImageView) view.findViewById(R.id.backdrop);
        //Glide.with(getContext()).load(R.drawable.home_image_three).centerCrop().into(imageView);
        //Glide.with(getContext()).load(R.drawable.home_image_one).centerCrop().into(backdrop);
    }


    @Override
    public void onTransitionStart(Transition transition) {

    }

    @Override
    public void onTransitionEnd(Transition transition) {
        mTransitionsCount++;
        if (mTransitionsCount == TRANSITIONS_TO_SWITCH) {
            mViewSwitcher.showNext();
            mTransitionsCount = 0;
        }
    }
}
