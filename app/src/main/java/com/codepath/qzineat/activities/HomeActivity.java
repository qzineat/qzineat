package com.codepath.qzineat.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.fragments.HomeFragment;
import com.codepath.qzineat.interfaces.CommunicationChannel;
import com.codepath.qzineat.interfaces.DrawerDataUpdateCallback;
import com.codepath.qzineat.models.User;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity implements CommunicationChannel, DrawerDataUpdateCallback {

    AccountHeader drawerHeader;
    ProfileDrawerItem profileAccountItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ok Open Home Fragment
        HomeFragment fragment = new HomeFragment();
        openFragment(fragment);
    }



    @Override
    public void openFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @Override
    public void onDataUpdate() {
        Log.d("DEBUG", HomeActivity.class.toString() + " Lets update drawer before anyone clicks on it....");

        // Header
        profileAccountItem
                .withName(User.getLoggedInUser().getProfileName())
                .withEmail(User.getLoggedInUser().getEmail());

        if(!User.getLoggedInUser().getImageFile().getUrl().isEmpty()){
            profileAccountItem.withIcon(User.getLoggedInUser().getImageFile().getUrl());
        }else {
            profileAccountItem.withIcon(getResources().getDrawable(R.drawable.ic_profile_placeholder));
        }

        drawerHeader.updateProfile(profileAccountItem);


    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
