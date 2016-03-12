package com.codepath.qzineat.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
import com.codepath.qzineat.fragments.AdvanceFragment;
import com.codepath.qzineat.fragments.EnrollEventFragment;
import com.codepath.qzineat.fragments.EventListFragment;
import com.codepath.qzineat.fragments.HostFragment;
import com.codepath.qzineat.fragments.HostListFragment;
import com.codepath.qzineat.fragments.LoginFragment;
import com.codepath.qzineat.fragments.ProfileFragment;
import com.codepath.qzineat.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.nvView) NavigationView nvDrawer;

    LinearLayout llSearch;
    EditText etSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setupDrawerContent(nvDrawer);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(drawerToggle);

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
                if(hasFocus){
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
        // Do something on Menu items
        if(User.isUserLoggedIn()){
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
        }else {
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
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
    public boolean onCreateOptionsMenu(Menu menu) {

        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setIconified(false);
        searchView.clearFocus();*/





        /*searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "Clicked on me .. redirect me ...");
            }
        });*/

        //MenuItem searchMenuItem = menu.findItem( R.id.menu_search);
        //searchMenuItem.expandActionView();


        /*btnSearchContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "q-food:" + searchView1.getQuery());
                Log.d("DEBUG", "q-location:" + searchView2.getQuery());
                try {
                    EventListFragment fragment = new EventListFragment();
                    String backStateName = EventListFragment.class.getName();
                    FragmentManager manager = getSupportFragmentManager();
                    boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
                    FragmentTransaction ft = manager.beginTransaction();
                    if (!fragmentPopped) { //fragment not in back stack, create it.
                        ft.replace(R.id.flContent, fragment);
                        ft.addToBackStack(backStateName);
                    }
                    Bundle args = new Bundle();
                    args.putString("searchFood", searchView1.getQuery().toString());
                    args.putString("searchLocality", searchView2.getQuery().toString());
                    fragment.setArguments(args);
                    ft.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

        return super.onCreateOptionsMenu(menu);
    }

}
