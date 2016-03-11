package com.codepath.qzineat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.android.qzineat.R;

import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/6/16.
 */
public class EnrollEventFragment extends Fragment{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Event List
        if(savedInstanceState == null){
            EventListFragment eventListFragment = new EventListFragment();
            Bundle args = new Bundle();
            args.putBoolean("isSubscriberView", true);
            eventListFragment.setArguments(args);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, eventListFragment);
            ft.commit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroll_event, container, false);
        ButterKnife.bind(this, view);

        //ParseFile parseFile = User.getLoggedInUser().getImageFile();
        //Glide.with(this).load(parseFile.getUrl()).centerCrop().into(ivProfileImage);

        return view;
    }


}
