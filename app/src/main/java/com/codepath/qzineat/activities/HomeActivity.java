package com.codepath.qzineat.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.QZinEatApplication;
import com.codepath.qzineat.fragments.AdvanceFragment;
import com.codepath.qzineat.fragments.EnrollEventFragment;
import com.codepath.qzineat.fragments.HomeFragment;
import com.codepath.qzineat.fragments.HostFragment;
import com.codepath.qzineat.fragments.ProfileFragment;
import com.codepath.qzineat.interfaces.CommunicationChannel;
import com.codepath.qzineat.interfaces.DrawerDataUpdateCallback;
import com.codepath.qzineat.interfaces.UserEventCountListener;
import com.codepath.qzineat.models.User;
import com.codepath.qzineat.utils.QZinDataAccess;
import com.codepath.qzineat.utils.QZinUtil;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.Bind;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity implements CommunicationChannel, DrawerDataUpdateCallback, UserEventCountListener {


    @Bind(R.id.toolbar) Toolbar toolbar;

    Drawer drawer;
    AccountHeader drawerHeader;
    ProfileDrawerItem profileAccountItem;
    PrimaryDrawerItem logInItem, profileItem, eventsItem,
            hostEventItem, userEventsItem,
            filterItem, logOutItem, switchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        ButterKnife.bind(this);

 //       toolbar.setVisibility(View.GONE);
/*
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Shyam");

        loadBackdrop();

        setupDrawer();*/


        setupDrawer();

        HomeFragment fragment = new HomeFragment();
        openFragment(fragment);
    }



    ActionBarDrawerToggle mActionBarDrawerToggle;
    public void createActionBarDrawerToggle(Toolbar toolbar) {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer.getDrawerLayout(), toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.getDrawerLayout().setDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    @Override
    public void attachDrawer(Toolbar toolbar){
        createActionBarDrawerToggle(toolbar);
    }

    @Override
    public void attachDrawer(Toolbar toolbar, boolean colorStatusBar) {
        createActionBarDrawerToggle(toolbar);
        getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
    }


    public void createDrawerHeader(){
        drawerHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(profileAccountItem)
                .withTextColor(getResources().getColor(R.color.drawer_profile_text))
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        Glide.with(this).load(R.drawable.drawer).into(drawerHeader.getHeaderBackgroundView());
    }

    private void createDrawerItems(){
        // if log in
        profileItem = new PrimaryDrawerItem().withName(getString(R.string.drawer_profile)).withIcon(R.mipmap.ic_profile_placeholder).withSelectedIcon(R.mipmap.ic_profile_image).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        userEventsItem = new PrimaryDrawerItem().withName(getString(R.string.drawer_my_event)).withIcon(R.mipmap.ic_food_drink).withSelectedIcon(R.mipmap.ic_food_drink_orange).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        // host
//        hostedEventsItem = new PrimaryDrawerItem().withName(getString(R.string.all_hosted_event)).withIcon(R.drawable.ic_hosted_events);
        hostEventItem = new PrimaryDrawerItem().withName(getString(R.string.host_event)).withIcon(R.mipmap.ic_event).withSelectedIcon(R.mipmap.ic_event_orange).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        // both
        logOutItem = new PrimaryDrawerItem().withName(getString(R.string.log_out)).withIcon(R.mipmap.ic_logout).withSelectedIcon(R.mipmap.ic_logout_orange).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        switchItem = new PrimaryDrawerItem().withName(getString(R.string.switch_host)).withIcon(R.mipmap.ic_account_switch).withSelectedIcon(R.mipmap.ic_account_switch_orange).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        // if log out
        logInItem = new PrimaryDrawerItem().withName(getString(R.string.log_in)).withIcon(R.mipmap.ic_profile_placeholder).withSelectedIcon(R.mipmap.ic_profile_image).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        // all
        eventsItem = new PrimaryDrawerItem().withName(getString(R.string.events)).withIcon(R.mipmap.ic_events).withSelectedIcon(R.mipmap.ic_events_orange).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        filterItem = new PrimaryDrawerItem().withName(getString(R.string.filters)).withIcon(R.mipmap.ic_settings).withSelectedIcon(R.mipmap.ic_settings_orange).withSelectedTextColor(getResources().getColor(R.color.deep_accent));

        // Profile Account
        if(User.isUserLoggedIn()){
            profileAccountItem = new ProfileDrawerItem()
                    .withName(User.getLoggedInUser().getProfileName())
                    .withEmail(User.getLoggedInUser().getEmail());
            if(User.getLoggedInUser().getImageFile() != null
                    && !User.getLoggedInUser().getImageFile().getUrl().isEmpty()){
                profileAccountItem.withIcon(User.getLoggedInUser().getImageFile().getUrl());
            }else {
                profileAccountItem.withIcon(getResources().getDrawable(R.drawable.ic_profile_placeholder));
            }

        }else {
            profileAccountItem = new ProfileDrawerItem()
                    .withEnabled(false)
                    .withIcon(getResources().getDrawable(R.drawable.ic_profile_placeholder));
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

    }

    // New Drawer using library
    private void setupDrawer(){
        createDrawerItems();
        createDrawerHeader();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(drawerHeader)
                .withActionBarDrawerToggle(true)
                .withOnDrawerItemClickListener(mDrawerItemClickListener)
                .withOnDrawerListener(mDrawerListener)
                .withDrawerLayout(R.layout.material_drawer)
                .build();

        if(User.isUserLoggedIn()){
            drawer.addItem(profileItem);
            drawer.addItem(eventsItem);
            if(QZinEatApplication.isHostView){
                drawer.addItem(hostEventItem);
                switchItem.withName(getString(R.string.switch_search)).withIcon(R.drawable.ic_swap); // Footer Change
                userEventsItem.withName(getString(R.string.drawer_hosted_event));
                drawer.addItem(userEventsItem);
                // drawer.setSelection(userEventsItem, true); // Set Default
            } else {
                drawer.addItem(userEventsItem);
                drawer.addItem(filterItem);

                //drawer.setSelection(eventsItem, true); // Set Default
            }
            drawer.addItem(logOutItem);
            drawer.addStickyFooterItem(switchItem);
        }else {
            drawer.addItem(logInItem);
            drawer.addItem(eventsItem);
            drawer.addItem(filterItem);

            //drawer.setSelection(eventsItem, true); // Set Default
        }
    }


    private Drawer.OnDrawerListener mDrawerListener = new Drawer.OnDrawerListener() {
        @Override
        public void onDrawerOpened(View drawerView) {
            // Other updates
            Log.d("DEBUG", "Drawer Open...");
            // update host count - this is async so no worries
            QZinDataAccess.findUserEventsCount(HomeActivity.this);
        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }
    };


    private Drawer.OnDrawerItemClickListener mDrawerItemClickListener = new Drawer.OnDrawerItemClickListener(){
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

            //toolbar.setVisibility(View.VISIBLE);

            // No Search Bar
            /*if(llSearch != null){
                llSearch.setVisibility(View.GONE);
            }*/


            if(drawerItem.equals(switchItem)){
                QZinUtil.changeTheme(HomeActivity.this);
                return false;
            }


            if(drawerItem.equals(logInItem)){
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                return false;
            }

            if(drawerItem.equals(logOutItem)){
                User.getLoggedInUser().logout();
                HomeActivity.this.finish();
                HomeActivity.this.startActivity(new Intent(HomeActivity.this, HomeActivity.this.getClass()));
                return false;
            }

            // Below This requires fragment
            Fragment fragment = null;
            if(drawerItem.equals(profileItem)){
                setTitle(profileItem.getName().toString());
                fragment = new ProfileFragment();
            }

            if(drawerItem.equals(userEventsItem)){
                setTitle(userEventsItem.getName().toString());
                fragment = new EnrollEventFragment();
            }

            if(drawerItem.equals(hostEventItem)){
                setTitle(hostEventItem.getName().toString());
                fragment = new HostFragment();
            }

            if(drawerItem.equals(eventsItem)){
                /*if(llSearch != null){
                    llSearch.setVisibility(View.VISIBLE); // Only on Event List
                }*/
                setTitle(eventsItem.getName().toString());
                fragment = new HomeFragment();
            }


            if(drawerItem.equals(filterItem)){
                fragment = new AdvanceFragment();
            }

            // Open Fragment
            openFragment(fragment);

            return false;
        }
    };

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
        Log.d("DEBUG", "Lets update drawer before anyone clicks on it....");

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
    public void onUserEventCount(int count) {
        Log.d("DEBUG", "I got call after db");

        userEventsItem
                .withBadge(String.valueOf(count))
                .withBadgeStyle(getBadgeStyle());

        drawer.updateItem(userEventsItem);
    }

    private BadgeStyle getBadgeStyle() {
        return new BadgeStyle()
                .withTextColor(getResources().getColor(R.color.badge_text_color))
                .withColorRes(R.color.badge_bg_color);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
