package com.codepath.qzineat.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.codepath.qzineat.interfaces.HostCountUpdateEventListener;

/**
 * Created by Shyam Rokde on 3/18/16.
 */
public class BaseFragment extends Fragment implements HostCountUpdateEventListener{
    @Override
    public void onHostCountUpdate(int count) {
       // ((MainActivity) getActivity()).onHostCountListener(count);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }
}
