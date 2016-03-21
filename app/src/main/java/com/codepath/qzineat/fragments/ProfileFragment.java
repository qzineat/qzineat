package com.codepath.qzineat.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
<<<<<<< HEAD
=======
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
>>>>>>> ed99052a16021c0816275cf707448e818f49b284
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

<<<<<<< HEAD
=======
import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.codepath.qzineat.interfaces.CommunicationChannel;
>>>>>>> ed99052a16021c0816275cf707448e818f49b284
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
<<<<<<< HEAD
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
=======
    @Bind(R.id.tvLocation) TextView tvLocation;
    @Bind(R.id.tvSpeciality) TextView tvSpeciality;
    @Bind(R.id.tvContact) TextView tvContact;
    @Bind(R.id.tvEmail) TextView tvEmail;
    @Bind(R.id.tvWebsite) TextView tvWebsite;
    @Bind(R.id.evEdit) ImageView evEdit;
    @Bind(R.id.toolbar)
    Toolbar toolbar;


>>>>>>> ed99052a16021c0816275cf707448e818f49b284

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

<<<<<<< HEAD
=======
        ViewPager vpPager = (ViewPager) view.findViewById(R.id.pager);
        vpPager.setAdapter(new TweetsPagerAdapter(getActivity().getSupportFragmentManager()));
        vpPager.getAdapter().notifyDataSetChanged();
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);

        // This is for drawer
        mCommunicationChannelListener.attachDrawer(toolbar, true);

        //getActivity().getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
        //getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        return  view;
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

    public class TweetsPagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = {"Upcoming Events", "Hosted Events"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new UpComingEventsFragment();
            } else if (position == 1) {
                return new PastEventsFragment();
            } else return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
>>>>>>> ed99052a16021c0816275cf707448e818f49b284

        return view;
    }
}