package com.codepath.qzineat.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.User;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/13/16.
 */
public class ProfileFragment extends BaseFragment {


    @Bind(R.id.ivProfileImage) ImageView ivProfileImage;
    @Bind(R.id.tvProfileName) TextView tvProfileName;
    @Bind(R.id.ivbkgImage) ImageView ivbkgImage;
    @Bind(R.id.tvLocation) TextView tvLocation;
    @Bind(R.id.tvSpeciality) TextView tvSpeciality;
    @Bind(R.id.tvContact) TextView tvContact;
    @Bind(R.id.tvEmail) TextView tvEmail;
    @Bind(R.id.tvWebsite) TextView tvWebsite;
    @Bind(R.id.evEdit) ImageView evEdit;


    Transformation transformation = new RoundedTransformationBuilder()
            .oval(true).cornerRadius(1)
            .borderColor(Color.WHITE)
            .borderWidth(3)
            .build();
    private ParseFile pf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Selected item in drawer..
        drawer.setSelection(profileItem, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        String objectId = String.valueOf(getArguments().get("objectId"));
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> UserList, ParseException e) {
                if (e == null) {
                    ArrayList<User> arrayList = new ArrayList<>(UserList);

                    Log.d("DEBUG", "User object" + arrayList.get(0).getEmail());
                    Log.d("DEBUG", "User object" + arrayList.get(0).getImageFile());
                    setProfileDetails(arrayList);
                } else
                    Picasso.with(getContext()).load(R.mipmap.ic_profile_placeholder).transform(transformation).into(ivProfileImage);
            }
        });


        evEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileEditFragment profileEditFragment = new ProfileEditFragment();

                openFragment(profileEditFragment);
            }
        });

        tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                String phone_no = tvContact.getText().toString();
                callIntent.setData(Uri.parse("tel:" + phone_no));
            }
        });

        return  view;
    }

    private void setProfileDetails(ArrayList<User> arrayList) {

        ivbkgImage.setBackgroundResource(R.drawable.user_profile_gb);
        if(arrayList.get(0).getObjectId().equals(User.getLoggedInUser().getObjectId())){
            if (User.getLoggedInUser().getImageFile() != null) {
                Picasso.with(getContext()).load(User.getLoggedInUser().getImageFile().getUrl()).transform(transformation).into(ivProfileImage);
            } else
                Picasso.with(getContext()).load(R.mipmap.ic_profile_placeholder).transform(transformation).into(ivProfileImage);
        }else {
            if (arrayList.get(0).getImageFile() != null) {
                Picasso.with(getContext()).load(arrayList.get(0).getImageFile().getUrl()).transform(transformation).into(ivProfileImage);
            }else
                Picasso.with(getContext()).load(R.mipmap.ic_profile_placeholder).transform(transformation).into(ivProfileImage);
        }

        if(arrayList.get(0).getObjectId().equals(User.getLoggedInUser().getObjectId())) {
            if (User.getLoggedInUser().getProfileName() != null) {
                tvProfileName.setText(User.getLoggedInUser().getProfileName());
            }
        }else {
            if (arrayList.get(0).getProfileName() != null) {
                tvProfileName.setText(User.getLoggedInUser().getProfileName());
            }
        }

        if(arrayList.get(0).getObjectId().equals(User.getLoggedInUser().getObjectId())) {
            if (User.getLoggedInUser().getCity() != null) {
                tvLocation.setText(User.getLoggedInUser().getCity());
            }
        }else {
            if (arrayList.get(0).getCity() != null) {
                tvLocation.setText(User.getLoggedInUser().getCity());
            }
        }

        if(arrayList.get(0).getObjectId().equals(User.getLoggedInUser().getObjectId())) {
            if (User.getLoggedInUser().getSpeciality() != null) {
                tvSpeciality.setText(User.getLoggedInUser().getSpeciality());
            }
        }else {
            if (arrayList.get(0).getSpeciality() != null) {
                tvSpeciality.setText(User.getLoggedInUser().getSpeciality());
            }
        }

        if(arrayList.get(0).getObjectId().equals(User.getLoggedInUser().getObjectId())) {
            if (User.getLoggedInUser().getPhone() != null) {
                tvContact.setText(User.getLoggedInUser().getPhone());
                tvContact.setTextColor(Color.parseColor("#1976D2"));
            }
        }else {
            if (arrayList.get(0).getPhone() != null) {
                tvContact.setText(User.getLoggedInUser().getPhone());
                tvContact.setTextColor(Color.parseColor("#1976D2"));
            }
        }

        if(arrayList.get(0).getObjectId().equals(User.getLoggedInUser().getObjectId())) {
            if (User.getLoggedInUser().getEmail() != null) {
                tvEmail.setText(User.getLoggedInUser().getEmail());
            }
        }else {
            if (arrayList.get(0).getEmail() != null) {
                tvEmail.setText(User.getLoggedInUser().getEmail());
            }
        }

        if(arrayList.get(0).getObjectId().equals(User.getLoggedInUser().getObjectId())) {
            if (User.getLoggedInUser().getWebsite() != null) {
                tvWebsite.setText(User.getLoggedInUser().getWebsite());
            }
        }else {
            if (arrayList.get(0).getWebsite() != null) {
                tvWebsite.setText(User.getLoggedInUser().getWebsite());
            }
        }
    }
}