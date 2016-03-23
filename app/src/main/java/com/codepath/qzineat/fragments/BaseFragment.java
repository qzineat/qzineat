package com.codepath.qzineat.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.QZinEatApplication;
import com.codepath.qzineat.activities.HomeActivity;
import com.codepath.qzineat.activities.LoginActivity;
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
import com.parse.ParseException;
import com.parse.ParseFile;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/18/16.
 */
public class BaseFragment extends Fragment implements UserEventCountListener, DrawerDataUpdateCallback {

    @Nullable
    @Bind(R.id.toolbar) Toolbar toolbar;

    CommunicationChannel mCommunicationChannel = null;

    protected Drawer drawer;
    protected AccountHeader drawerHeader;
    protected ProfileDrawerItem profileAccountItem;
    protected PrimaryDrawerItem logInItem, profileItem, eventsItem,
            hostEventItem, userEventsItem,
            filterItem, logOutItem, switchItem;
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDrawer();

        //((AppCompatActivity) context).setSupportActionBar(toolbar);

        //drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, container);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void createDrawerHeader(){
        drawerHeader = new AccountHeaderBuilder()
                .withActivity(getActivity())
                .addProfiles(profileAccountItem)
                .withTextColor(getResources().getColor(R.color.drawer_profile_text))
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        Glide.with(this).load(R.drawable.drawer).into(drawerHeader.getHeaderBackgroundView());
    }


    protected void createDrawerItemsHost(){
        // if log in
        profileItem = new PrimaryDrawerItem().withName(getString(R.string.drawer_profile)).withIcon(R.mipmap.ic_account_white).withSelectedIcon(R.mipmap.ic_profile_image).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        userEventsItem = new PrimaryDrawerItem().withName(getString(R.string.drawer_my_event)).withIcon(R.mipmap.ic_food_drink_white).withSelectedIcon(R.mipmap.ic_food_drink_orange).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        // host
//        hostedEventsItem = new PrimaryDrawerItem().withName(getString(R.string.all_hosted_event)).withIcon(R.drawable.ic_hosted_events);
        hostEventItem = new PrimaryDrawerItem().withName(getString(R.string.host_event)).withIcon(R.mipmap.ic_event_check).withSelectedIcon(R.mipmap.ic_event_orange).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        // both
        logOutItem = new PrimaryDrawerItem().withName(getString(R.string.log_out)).withIcon(R.mipmap.ic_logout_white).withSelectedIcon(R.mipmap.ic_logout_orange).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        switchItem = new PrimaryDrawerItem().withName(getString(R.string.switch_host)).withIcon(R.mipmap.ic_account_switch_white).withSelectedIcon(R.mipmap.ic_account_switch_orange).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        // if log out
        logInItem = new PrimaryDrawerItem().withName(getString(R.string.log_in)).withIcon(R.mipmap.ic_account_white).withSelectedIcon(R.mipmap.ic_profile_image).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        // all
        eventsItem = new PrimaryDrawerItem().withName(getString(R.string.events)).withIcon(R.mipmap.ic_event_white).withSelectedIcon(R.mipmap.ic_events_orange).withSelectedTextColor(getResources().getColor(R.color.deep_accent));
        filterItem = new PrimaryDrawerItem().withName(getString(R.string.filters)).withIcon(R.mipmap.ic_setting_white).withSelectedIcon(R.mipmap.ic_settings_orange).withSelectedTextColor(getResources().getColor(R.color.deep_accent));

        // Profile Account
        if(User.isUserLoggedIn()){
            profileAccountItem = new ProfileDrawerItem()
                    .withName(User.getLoggedInUser().getProfileName())
                    .withEmail(User.getLoggedInUser().getEmail());
            if(User.getLoggedInUser().getImageFile() != null
                    && !User.getLoggedInUser().getImageFile().getUrl().isEmpty()){
                bitmap = getBitMapImage(User.getLoggedInUser().getImageFile());
                profileAccountItem.withIcon(bitmap);
            }else {
                profileAccountItem.withIcon(getResources().getDrawable(R.drawable.ic_profile_placeholder));
            }

        }else {
            profileAccountItem = new ProfileDrawerItem()
                    .withEnabled(false)
                    .withIcon(getResources().getDrawable(R.drawable.ic_profile_placeholder));
        }
    }


    protected void createDrawerItems(){
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
                bitmap = getBitMapImage(User.getLoggedInUser().getImageFile());
                profileAccountItem.withIcon(bitmap);
            }else {
                profileAccountItem.withIcon(getResources().getDrawable(R.drawable.ic_profile_placeholder));
            }

        }else {
            profileAccountItem = new ProfileDrawerItem()
                    .withEnabled(false)
                    .withIcon(getResources().getDrawable(R.drawable.ic_profile_placeholder));
        }
    }

    private Bitmap getBitMapImage(ParseFile imageFile) {

        ParseFile pFile = imageFile;
        byte[] bitmapdata = new byte[0];  // here it throws error
        try {
            bitmapdata = pFile.getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

        return bitmap;
    }

    protected void setupDrawer(){

        if(QZinEatApplication.isHostView){
            createDrawerItemsHost();
        }else{
            createDrawerItems();
        }

        createDrawerHeader();

        drawer = new DrawerBuilder()
                .withActivity(getActivity())
                .withAccountHeader(drawerHeader)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withOnDrawerItemClickListener(mDrawerItemClickListener)
                .withOnDrawerListener(mDrawerListener)
                .build();


        if(User.isUserLoggedIn()){
            drawer.addItem(profileItem);
            drawer.addItem(eventsItem);
            if(QZinEatApplication.isHostView){
                drawer.addItem(hostEventItem);
                switchItem.withName(getString(R.string.switch_search)).withIcon(R.mipmap.ic_swap_white); // Footer Change
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


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        drawer.setToolbar(getActivity(), toolbar);
    }

    private Drawer.OnDrawerItemClickListener mDrawerItemClickListener = new Drawer.OnDrawerItemClickListener(){
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {



            // No Search Bar
            /*if(llSearch != null){
                llSearch.setVisibility(View.GONE);
            }*/


            if(drawerItem.equals(switchItem)){
                QZinUtil.changeTheme(getActivity());

                return false;
            }


            if(drawerItem.equals(logInItem)){
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                return false;
            }

            if(drawerItem.equals(logOutItem)){
                User.getLoggedInUser().logout();
                getActivity().finish();
                getActivity().startActivity(new Intent(getActivity(), getActivity().getClass()));
                return false;
            }

            // Below This requires fragment
            Fragment fragment = null;
            if(drawerItem.equals(profileItem)){
                fragment = new ProfileFragment();
            }

            if(drawerItem.equals(userEventsItem)){
                fragment = new EnrollEventFragment();
            }

            if(drawerItem.equals(hostEventItem)){
                fragment = new HostFragment();
            }

            if(drawerItem.equals(eventsItem)){
                /*if(llSearch != null){
                    llSearch.setVisibility(View.VISIBLE); // Only on Event List
                }*/
                fragment = new HomeFragment();
            }


            if(drawerItem.equals(filterItem)){
                //TODO: fragment = new AdvanceFragment(); - hack
                fragment = new HomeFragment();
            }

            // Open Fragment
            openFragment(fragment);

            return false;
        }
    };


    private Drawer.OnDrawerListener mDrawerListener = new Drawer.OnDrawerListener() {
        @Override
        public void onDrawerOpened(View drawerView) {
            // Other updates
            Log.d("DEBUG", "Drawer Open...");
            // update host count - this is async so no worries
            QZinDataAccess.findUserEventsCount(BaseFragment.this);
        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof CommunicationChannel){
            mCommunicationChannel = (CommunicationChannel) context;
        }
    }

    protected void openFragment(Fragment fragment){
        mCommunicationChannel.openFragment(fragment);
    }


    @Override
    public void onUserEventCount(int count) {
        userEventsItem
                .withBadge(String.valueOf(count))
                .withBadgeStyle(getBadgeStyle());

        drawer.updateItem(userEventsItem);
    }

    private BadgeStyle getBadgeStyle() {

        if(QZinEatApplication.isHostView){
            return new BadgeStyle()
                    .withTextColor(getResources().getColor(R.color.badge_bg_color))
                    .withColorRes(R.color.badge_text_color);

        }else {
            return new BadgeStyle()
                    .withTextColor(getResources().getColor(R.color.badge_text_color))
                    .withColorRes(R.color.badge_bg_color);
        }

    }


    @Override
    public void onDrawerDataUpdate() {
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

}
