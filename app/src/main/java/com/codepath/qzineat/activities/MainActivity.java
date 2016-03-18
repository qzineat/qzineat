package com.codepath.qzineat.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.QZinEatApplication;
import com.codepath.qzineat.fragments.AdvanceFragment;
import com.codepath.qzineat.fragments.EnrollEventFragment;
import com.codepath.qzineat.fragments.EventListFragment;
import com.codepath.qzineat.fragments.HostFragment;
import com.codepath.qzineat.fragments.HostListFragment;
import com.codepath.qzineat.fragments.LoginFragment;
import com.codepath.qzineat.fragments.ProfileFragment;
import com.codepath.qzineat.models.User;
import com.codepath.qzineat.utils.QZinUtil;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {


    @Bind(R.id.toolbar) Toolbar toolbar;

    LinearLayout llSearch;
    EditText etSearch;

    Drawer drawer;
    AccountHeader drawerHeader;
    ProfileDrawerItem profileAccountItem;
    PrimaryDrawerItem logInItem, profileItem, eventsItem,
            hostedEventsItem, hostEventItem, subscribedEventItem,
            filterItem, logOutItem, switchItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QZinUtil.onActivityCreateSetTheme(this); // Change Theme

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle(R.string.drawer_event);


        setupSearch(); // Search

        setupDrawer(); // Drawer

    }

    public void createDrawerHeader(){
        drawerHeader = new AccountHeaderBuilder()
                .withActivity(this)
                //.withHeaderBackground(R.drawable.drawer) TODO: Taking too long hence animation lost
                .addProfiles(
                        profileAccountItem
                )
                .withTextColor(getResources().getColor(R.color.primary_text))
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        Glide.with(getApplicationContext()).load(R.drawable.drawer).into(drawerHeader.getHeaderBackgroundView());
    }

    private void createDrawerItems(){
        // if log in
        profileItem = new PrimaryDrawerItem().withName("Profile").withIcon(R.drawable.ic_profile_placeholder);
        subscribedEventItem = new PrimaryDrawerItem().withName("Subscribed Events").withIcon(R.drawable.ic_food_fork_drink);
        // host
        hostedEventsItem = new PrimaryDrawerItem().withName(getString(R.string.all_hosted_event)).withIcon(R.drawable.ic_hosted_events);
        hostEventItem = new PrimaryDrawerItem().withName(getString(R.string.host_event)).withIcon(R.drawable.ic_host_event);
        // both
        logOutItem = new PrimaryDrawerItem().withName(getString(R.string.log_out)).withIcon(R.drawable.ic_logout);
        switchItem = new PrimaryDrawerItem().withName(getString(R.string.switch_host)).withIcon(R.drawable.ic_account_switch);
        // if log out
        logInItem = new PrimaryDrawerItem().withName(getString(R.string.log_in)).withIcon(R.drawable.ic_profile_placeholder);
        // all
        eventsItem = new PrimaryDrawerItem().withName(getString(R.string.events)).withIcon(R.drawable.ic_drawer_event);
        filterItem = new PrimaryDrawerItem().withName(getString(R.string.filters)).withIcon(R.drawable.ic_filters);

        // Profile Account
        if(User.isUserLoggedIn()){
            profileAccountItem = new ProfileDrawerItem()
                    .withName(User.getLoggedInUser().getProfileName())
                    .withEmail(User.getLoggedInUser().getEmail())
                    .withIcon(getResources().getDrawable(R.drawable.ic_profile_placeholder));
        }else {
            profileAccountItem = new ProfileDrawerItem()
                    .withEnabled(false)
                    .withIcon(getResources().getDrawable(R.drawable.ic_profile_placeholder));
        }

    }

    // New Drawer using library
    private void setupDrawer(){
        createDrawerItems();
        createDrawerHeader();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(drawerHeader)
                .withOnDrawerItemClickListener(mDrawerItemClickListener)
                .withOnDrawerListener(mDrawerListener)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View clickedView) {
                        return false;
                    }
                })
                .build();

        if(User.isUserLoggedIn()){
            drawer.addItem(profileItem);
            drawer.addItem(eventsItem);
            if(QZinEatApplication.isHostView){
                drawer.addItem(hostedEventsItem);
                drawer.addItem(hostEventItem);
                switchItem.withName(getString(R.string.switch_search)).withIcon(R.drawable.ic_swap); // Footer Change

                drawer.setSelection(hostedEventsItem, true); // Set Default
            }else {
                drawer.addItem(subscribedEventItem);
                drawer.addItem(filterItem);

                drawer.setSelection(eventsItem, true); // Set Default
            }
            drawer.addItem(logOutItem);
            drawer.addStickyFooterItem(switchItem);
        }else {
            drawer.addItem(logInItem);
            drawer.addItem(eventsItem);
            drawer.addItem(filterItem);

            drawer.setSelection(eventsItem, true); // Set Default
        }
    }

    private void setupSearch(){
        View view = getLayoutInflater().inflate(R.layout.search_bar_home, null);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(view);

        etSearch = (EditText) findViewById(R.id.etSearch);
        llSearch = (LinearLayout) findViewById(R.id.llSearch);

        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.clearFocus();
                    // Send to another activity
                    Intent i = new Intent(getApplicationContext(), EventListActivity.class);
                    startActivity(i);
                }
            }
        });
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send to another activity
                Intent i = new Intent(getApplicationContext(), EventListActivity.class);
                startActivity(i);
            }
        });
    }


    private void openFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private Drawer.OnDrawerListener mDrawerListener = new Drawer.OnDrawerListener() {
        @Override
        public void onDrawerOpened(View drawerView) {
            // Profile update
            profileAccountItem.withName(User.getLoggedInUser().getProfileName());
            drawer.updateItem(profileAccountItem);

            // Other updates
            Log.d("DEBUG", "You clicked on me");
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

            // No Search Bar
            if(llSearch != null){
                llSearch.setVisibility(View.GONE);
            }


            if(drawerItem.equals(switchItem)){
                QZinUtil.changeTheme(MainActivity.this);
                return false;
            }

            if(drawerItem.equals(logOutItem)){
                User.getLoggedInUser().logout();
                MainActivity.this.finish();
                MainActivity.this.startActivity(new Intent(MainActivity.this, MainActivity.this.getClass()));
                return false;
            }

            // Below This requires fragment
            Fragment fragment = null;
            if(drawerItem.equals(profileItem)){
                setTitle(profileItem.getName().toString());
                fragment = new ProfileFragment();
            }

            if(drawerItem.equals(subscribedEventItem)){
                setTitle(subscribedEventItem.getName().toString());
                fragment = new EnrollEventFragment();
            }

            if(drawerItem.equals(hostedEventsItem)){
                setTitle(hostedEventsItem.getName().toString());
                fragment = new HostListFragment();
            }

            if(drawerItem.equals(hostEventItem)){
                setTitle(hostEventItem.getName().toString());
                fragment = new HostFragment();
            }

            if(drawerItem.equals(eventsItem)){
                if(llSearch != null){
                    llSearch.setVisibility(View.VISIBLE); // Only on Event List
                }
                setTitle(eventsItem.getName().toString());
                fragment = new EventListFragment();
            }

            if(drawerItem.equals(logInItem)){
                fragment = new LoginFragment();
            }

            if(drawerItem.equals(filterItem)){
                fragment = new AdvanceFragment();
            }

            // Open Fragment
            openFragment(fragment);

            return false;
        }
    };


}
