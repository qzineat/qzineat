package com.codepath.qzineat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.nvView) NavigationView nvDrawer;
    @Bind(R.id.llSwitch) LinearLayout llSwitch;
    @Bind(R.id.llSwitchHost) LinearLayout llSwitchHost;

    LinearLayout llSearch;
    EditText etSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QZinUtil.onActivityCreateSetTheme(this); // Change Theme

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setupDrawerContent(nvDrawer);

        // Disable scroll bar in NavigationView
        disableNavigationViewScrollbars(nvDrawer);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);

        // Switch View
        llSwitch.setOnClickListener(mSwitchListener);
        llSwitchHost.setOnClickListener(mSwitchListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new EventListFragment()).commit();

        setTitle(R.string.drawer_event);

        setupSearch();
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

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void updateMenu(Menu menu){
        MenuItem loginItem = menu.findItem(R.id.nav_login);
        MenuItem logoutItem = menu.findItem (R.id.nav_logout);
        MenuItem navItemEvents =  menu.findItem (R.id.nav_events);
        MenuItem navItemHostedEvents =  menu.findItem (R.id.nav_all_hosted_event);
        MenuItem navItemHostEvent =  menu.findItem (R.id.nav_host_event);
        MenuItem navItemMyEvents =  menu.findItem (R.id.nav_my_event);
        MenuItem navItemFilters =  menu.findItem (R.id.nav_advance_filter);

        // Do something on Menu items
        if(User.isUserLoggedIn()){
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
        }else {
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
        }
        // If I am host then don't show me menu items
        if(QZinEatApplication.isHostView){
            // hide
            navItemEvents.setVisible(false);
            navItemMyEvents.setVisible(false);
            navItemFilters.setVisible(false);
            llSwitch.setVisibility(View.GONE);
            // show
            navItemHostedEvents.setVisible(true);
            navItemHostEvent.setVisible(true);
            llSwitchHost.setVisibility(View.VISIBLE);
            nvDrawer.setItemTextColor(getResources().getColorStateList(R.color.drawer_state_list_host));
            nvDrawer.setItemIconTintList(getResources().getColorStateList(R.color.drawer_state_list_host));
        }
    }


    public void selectDrawerItem(MenuItem menuItem) {
        llSearch.setVisibility(View.GONE);
        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = null;

        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_login:
                fragmentClass = LoginFragment.class;
                break;
            case R.id.nav_logout:
                User.getLoggedInUser().logout();
                fragmentClass = EventListFragment.class;
                break;
            case R.id.nav_all_hosted_event:
                fragmentClass = HostListFragment.class;
                break;
            case R.id.nav_host_event:
                fragmentClass = HostFragment.class;
                break;
            case R.id.nav_my_event:
                fragmentClass = EnrollEventFragment.class;
                break;
            case R.id.nav_advance_filter:
                fragmentClass = AdvanceFragment.class;
                break;
            case R.id.nav_profile:
                fragmentClass = ProfileFragment.class;
                break;
            default:
                fragmentClass = EventListFragment.class;
                llSearch.setVisibility(View.VISIBLE);
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, fragment)
                .addToBackStack(fragmentClass.getName())
                .commit();


        Log.d("DEBUG", "Fragments in stack - " + fragmentManager.getBackStackEntryCount());
        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                updateMenu(nvDrawer.getMenu());
                super.onDrawerStateChanged(newState);
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        // Handle other toolbar actions here
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }




    private View.OnClickListener mSwitchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            QZinUtil.changeTheme(MainActivity.this);
        }
    };
}
