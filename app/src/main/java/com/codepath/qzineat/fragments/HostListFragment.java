package com.codepath.qzineat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.codepath.qzineat.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/10/16.
 */
public class HostListFragment extends BaseFragment {


    @Bind(R.id.ibPlus)
    ImageButton ibPlus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Event List
        if (savedInstanceState == null) {
            UserEventsFragment userEventsFragment = new UserEventsFragment();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.flContainerHEL, userEventsFragment);
            ft.commit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host_event_list, container, false);
        ButterKnife.bind(this, view);

        ibPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HostFragment hostFragment = new HostFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.flContent, hostFragment);
                transaction.commit();
            }
        });
        //ParseFile parseFile = User.getLoggedInUser().getImageFile();
        //Glide.with(this).load(parseFile.getUrl()).centerCrop().into(ivProfileImage);

        return view;
    }
}
