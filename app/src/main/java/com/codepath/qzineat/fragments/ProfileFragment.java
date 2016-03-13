package com.codepath.qzineat.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
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

    @Bind(R.id.ivProfileImage) ImageView ivProfileImage;
    @Bind(R.id.tvProfileName) TextView tvProfileName;
    @Bind(R.id.tvLocation) TextView tvLocation;
    @Bind(R.id.tvSpeciality) TextView tvSpeciality;
    @Bind(R.id.tvContact) TextView tvContact;
    @Bind(R.id.tvEmail) TextView tvEmail;
    @Bind(R.id.tvWebsite) TextView tvWebsite;
    @Bind(R.id.evEdit) ImageView evEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Event List
//        if(savedInstanceState == null){
//            UserEventsFragment userEventsFragment = new UserEventsFragment();
//            Bundle args = new Bundle();
//            args.putBoolean("isSubscriberView", true);
//            userEventsFragment.setArguments(args);
//
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.replace(R.id.flContainer, userEventsFragment);
//            ft.commit();
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        ViewPager vpPager = (ViewPager) view.findViewById(R.id.pager);
        vpPager.setAdapter(new TweetsPagerAdapter(getActivity().getSupportFragmentManager()));
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);

        ParseFile pf = null;
        if (User.getLoggedInUser().getImageFile() != null) {
            pf = User.getLoggedInUser().getImageFile();
        }

        if (pf != null) {
            Glide.with(this).load(pf.getUrl()).asBitmap().centerCrop().into(ivProfileImage);
        }
        if (User.getLoggedInUser().getProfileName() != null) {
            tvProfileName.setText(User.getLoggedInUser().getProfileName());
        }
        if (User.getLoggedInUser().getCity() != null) {
            tvLocation.setText(User.getLoggedInUser().getCity());
        }
        if (User.getLoggedInUser().getSpeciality() != null) {
            tvSpeciality.setText("Speciality: " + User.getLoggedInUser().getSpeciality());
        }
        if (User.getLoggedInUser().getPhone() != null) {
            tvContact.setText("Contact: " +User.getLoggedInUser().getPhone());
        }
        if (User.getLoggedInUser().getEmail() != null) {
            tvEmail.setText("Email: " +User.getLoggedInUser().getEmail());
        }
        if (User.getLoggedInUser().getWebsite() != null) {
            tvWebsite.setText("Website: " +User.getLoggedInUser().getWebsite());
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

    public class TweetsPagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return null;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

    }


}
