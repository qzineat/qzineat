package com.codepath.qzineat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.qzineat.interfaces.CommunicationChannel;
import com.codepath.android.qzineat.R;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/20/16.
 */
public class HomeFragment extends Fragment {

    @Bind(R.id.toolbar) Toolbar toolbar;

    Drawer drawer;
    AccountHeader drawerHeader;
    ProfileDrawerItem profileAccountItem;
    PrimaryDrawerItem logInItem, profileItem, eventsItem,
            hostEventItem, userEventsItem,
            filterItem, logOutItem, switchItem;


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



        EventListFragment fragment = new  EventListFragment();

        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flEventListContent, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();


        mCommunicationChannelListener.attachDrawer(toolbar);
        loadBackdrop(view);




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

    private void loadBackdrop(View view) {
        final ImageView imageView = (ImageView) view.findViewById(R.id.backdrop);
        Glide.with(getContext()).load(R.drawable.home_image_three).centerCrop().into(imageView);
    }


}
