package com.codepath.qzineat.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.codepath.qzineat.models.User;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.parse.ParseFile;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/13/16.
 */
public class ProfileFragment extends Fragment {


    @Bind(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @Bind(R.id.tvProfileName)
    TextView tvProfileName;
    @Bind(R.id.ivbkgImage)
    ImageView ivbkgImage;
    @Bind(R.id.tvLocation)
    TextView tvLocation;
    @Bind(R.id.tvSpeciality)
    TextView tvSpeciality;
    @Bind(R.id.tvContact)
    TextView tvContact;
    @Bind(R.id.tvEmail)
    TextView tvEmail;
    @Bind(R.id.tvWebsite)
    TextView tvWebsite;
    @Bind(R.id.evEdit)
    ImageView evEdit;

    Transformation transformation = new RoundedTransformationBuilder()
            .oval(true).cornerRadius(1)
            .borderColor(Color.WHITE)
            .borderWidth(3)
            .build();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        String imgUrl = null;
        ParseFile pf = null;

        ivbkgImage.setBackgroundResource(R.drawable.user_profile_gb);
        if (User.getLoggedInUser().getImageFile() != null) {
            pf = User.getLoggedInUser().getImageFile();
            if (pf != null && !pf.getUrl().isEmpty()) {
                imgUrl = pf.getUrl();
            }
        }

        if (pf != null) {
            Picasso.with(getContext()).load(imgUrl).transform(transformation).into(ivProfileImage);
        } else
            Picasso.with(getContext()).load(R.mipmap.ic_profile_placeholder).transform(transformation).into(ivProfileImage);

        if (User.getLoggedInUser().getProfileName() != null) {
            tvProfileName.setText(User.getLoggedInUser().getProfileName());
        }
        if (User.getLoggedInUser().getCity() != null) {
            tvLocation.setText(User.getLoggedInUser().getCity());
        }
        if (User.getLoggedInUser().getSpeciality() != null) {
            tvSpeciality.setText(User.getLoggedInUser().getSpeciality());
        }
        if (User.getLoggedInUser().getPhone() != null) {
            tvContact.setText(User.getLoggedInUser().getPhone());
            tvContact.setTextColor(Color.parseColor("#1976D2"));
        }
        if (User.getLoggedInUser().getEmail() != null) {
            tvEmail.setText(User.getLoggedInUser().getEmail());
        }
        if (User.getLoggedInUser().getWebsite() != null) {
            tvWebsite.setText(User.getLoggedInUser().getWebsite());
        }

        evEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileEditFragment profileEditFragment = new ProfileEditFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.flContent, profileEditFragment);
                fragmentTransaction.commit();

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


        return view;
    }
}