package com.codepath.qzineat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.android.qzineat.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/6/16.
 */
public class ProfileFragment extends Fragment{

    @Bind(R.id.ivProfileImage) ImageView ivProfileImage;
    @Bind(R.id.tvProfileName) TextView tvProfileName;
    @Bind(R.id.tvCity) TextView tvCity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Event List
        if(savedInstanceState == null){
            EventListFragment eventListFragment = new EventListFragment();
            Bundle args = new Bundle();
            args.putBoolean("isProfileView", true);
            eventListFragment.setArguments(args);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, eventListFragment);
            ft.commit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_layout, container, false);
        ButterKnife.bind(this, view);

        //ParseFile parseFile = User.getLoggedInUser().getImageFile();
        //Glide.with(this).load(parseFile.getUrl()).centerCrop().into(ivProfileImage);

        return view;
    }


}
