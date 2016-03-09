package com.codepath.qzineat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.User;
import com.parse.ParseFile;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/6/16.
 */
public class ProfileFragment extends Fragment{

    @Bind(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @Bind(R.id.tvProfileName)
    TextView tvProfileName;
    @Bind(R.id.tvCreatedDate)
    TextView tvCreatedDate;
    @Bind(R.id.tvUserCity)
    TextView tvUserCity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        ParseFile parseFile = User.getLoggedInUser().getImageFile();
        Glide.with(this).load(parseFile.getUrl()).centerCrop().into(ivProfileImage);
        return view;
    }
}
