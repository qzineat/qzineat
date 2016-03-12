package com.codepath.qzineat.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.fragments.EventListFragment;
import com.codepath.qzineat.models.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class EventListActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupSearch();

        // TODO: implement flag for which to show when
        if(true){
            setupMap();
        }else {
            setupEventList();
        }
    }

    public void setupMap(){

        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = SupportMapFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.flContent, mapFragment).commit();

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    //LatLng sydney = new LatLng(-33.867, 151.206);

                    //map.setMyLocationEnabled(true);
                    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

                    /*map.addMarker(new MarkerOptions()
                            .title("Sydney")
                            .snippet("The most populous city in Australia.")
                            .position(sydney));*/


                    loadMap(map);

                    // set the info window adapter
                    //map.setInfoWindowAdapter(new CustomWindowAdapter(getLayoutInflater()));
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void loadMap(final GoogleMap map){
        // Load Event Near to San Francisco
        final BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
        Double sfLatitude = 37.773972;
        Double sfLongitude = -122.431297;
        LatLng SanFrancisco = new LatLng(sfLatitude, sfLongitude);
        map.addMarker(new MarkerOptions()
                .title("San Francisco")
                .snippet("The most beautiful city in USA.")
                .position(SanFrancisco));

        // Get Events
        ParseGeoPoint point = new ParseGeoPoint();
        point.setLatitude(sfLatitude);
        point.setLongitude(sfLongitude);
        ParseQuery<Event> locationQuery = ParseQuery.getQuery(Event.class);
        locationQuery.whereWithinMiles("location", point, 50);
        //locationQuery.whereNear("location", point); // this is 100 miles - but we can do whereWithinMiles if needed less
        locationQuery.setLimit(100);
        locationQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Events Count - " + events.size());
                    boolean first = true;
                    for(Event ev: events){
                        if(ev.getLocation()!=null){
                            LatLng place = new LatLng(ev.getLocation().getLatitude(), ev.getLocation().getLongitude());
                            map.addMarker(new MarkerOptions()
                                    .title(ev.getLocality())
                                    .snippet(ev.getTitle())
                                    .position(place)
                                    .icon(defaultMarker));
                            if(first){
                                // Move camera to nearest place
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 10));
                            }
                        }
                        first = false;
                    }
                } else {
                    Log.e("ERROR", "Error Loading events" + e); // Don't notify this to user..
                }
            }
        });

    }


    public void setupEventList(){
        Fragment fragment = new EventListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, fragment)
                .commit();
    }

    //MenuItem searchItem;
    SearchView searchView1;
    SearchView searchView2;
    ImageButton btnSearchContent;
    private void setupSearch() {
        View view = getLayoutInflater().inflate(R.layout.qzin_search, null);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(view);
        searchView1 = (SearchView) getSupportActionBar().getCustomView().findViewById(R.id.search1);
        searchView2 = (SearchView) getSupportActionBar().getCustomView().findViewById(R.id.search2);

        searchView1.setIconified(false);
        searchView1.setFocusable(true);
        searchView2.setIconified(false);
        searchView2.clearFocus();

        btnSearchContent = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.btnSearchContent);

        searchView1.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                closeSearch();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        showSearch();

        btnSearchContent.setOnClickListener(new View.OnClickListener() {
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
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void showSearch(){

        btnSearchContent.setVisibility(View.VISIBLE); // show new button
        //searchItem.setVisible(false); // hide me

        EditText etSearch1 = (EditText) searchView1.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        etSearch1.setHintTextColor(getResources().getColor(R.color.hint_color_light));
        etSearch1.setTextColor(Color.WHITE);

        searchView1.setIconified(false);
        searchView1.setQueryHint(getString(R.string.primary_search_hint));
        searchView1.setVisibility(View.VISIBLE);


        EditText etSearch2 = (EditText) searchView2.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        etSearch2.setHintTextColor(getResources().getColor(R.color.hint_color_light));
        etSearch2.setTextColor(Color.WHITE);

        searchView2.setIconified(false);
        searchView2.setVisibility(View.VISIBLE);
        searchView2.setQueryHint(getString(R.string.location_search_hint));
        searchView2.clearFocus();
    }

    private void hideSearch(){
        searchView1.setVisibility(View.INVISIBLE);
        searchView2.setVisibility(View.INVISIBLE);
        //searchItem.setVisible(true);
        btnSearchContent.setVisibility(View.INVISIBLE); // hide new button
    }

    private void closeSearch(){

    }


}
