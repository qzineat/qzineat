package com.codepath.qzineat.activities;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.fragments.EventListFragment;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.utils.GeoUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SearchActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    private EventListFragment eventListFragment;
    private SupportMapFragment mapFragment;
    private EditText etSearch1;
    private EditText etSearch2;
    private ImageButton btnSearchContent;
    private ImageView ivSearchClear1;
    private ImageView ivSearchClear2;
    private ImageButton ibListView;
    private ImageButton ibMapView;


    HashMap<String, String> mMarkersToEventIdMap = new HashMap<String, String>();

    private Double maxSearchDistance = 30d;
    private int resultLimit = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupSearch();

        isMapView = true;
        setupMap(null, null);
        showhideIcons();
    }

    public void setupMap(final String searchFood, final String searchLocality){

        FragmentManager fragmentManager = getSupportFragmentManager();
        mapFragment = SupportMapFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.flContent, mapFragment).commit();

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    loadMap(map, searchFood, searchLocality);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: based on current location
    protected void loadMap(final GoogleMap map, final String searchFood, final String searchLocality){
        // Load Event Near to San Francisco
        final BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);

        Double sLatitude = null;
        Double sLongitude = null;
        if(searchLocality != null && !searchLocality.isEmpty()){
            // Get Location & Correct Locality with Country code
            Address address = GeoUtil.getGeoAddress(getApplicationContext(), searchLocality);
            if(address != null){
                sLatitude = address.getLatitude();
                sLongitude = address.getLongitude();
            }

        }else {
            // Default San Francisco
            sLatitude = 37.773972;
            sLongitude = -122.431297;
            /*map.addMarker(new MarkerOptions()
                    .title("San Francisco")
                    .snippet("The most beautiful city in USA.")
                    .position(SanFrancisco));*/

        }
        if(sLatitude == null || sLongitude == null){
            return;
        }

        // Get Events
        final ParseGeoPoint point = new ParseGeoPoint();
        point.setLatitude(sLatitude);
        point.setLongitude(sLongitude);
        final LatLng pointLatLang = new LatLng(sLatitude, sLongitude);

        ParseQuery<Event> locationQuery = ParseQuery.getQuery(Event.class);
        if(searchFood != null && !searchFood.isEmpty()){
            locationQuery.whereEqualTo("category", searchFood);
        }
        locationQuery.whereWithinMiles("location", point, maxSearchDistance);
        //locationQuery.whereNear("location", point); // this is 100 miles - but we can do whereWithinMiles if needed less
        locationQuery.setLimit(resultLimit);
        locationQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Events Count - " + events.size());
                    boolean first = false;
                    mMarkersToEventIdMap.clear();

                    for (Event ev : events) {
                        if (ev.getLocation() != null) {
                            LatLng place = new LatLng(ev.getLocation().getLatitude(), ev.getLocation().getLongitude());
                            Marker m = map.addMarker(new MarkerOptions()
                                    .title(ev.getLocality())
                                    .snippet(ev.getTitle())
                                    .position(place)
                                    .icon(defaultMarker));

                            if (!first) {
                                // Move camera to nearest place
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 12));
                            }

                            mMarkersToEventIdMap.put(m.getId(), ev.getObjectId());
                        }
                        first = true;
                    }

                    if(!first){
                        // Move camera to default place
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pointLatLang, 12));
                    }

                } else {
                    Log.e("ERROR", "Error Loading events" + e); // Don't notify this to user..
                }
            }
        });

        map.setOnInfoWindowClickListener(mInfoWindowClickListener);
        map.setInfoWindowAdapter(mInfoWindowAdapter);
    }

    /**
     * EventList - List View
     * @param args
     */
    public void setupEventList(Bundle args){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        if(fragmentManager.findFragmentByTag("eventListFragment") != null){
            fragmentTransaction.remove(fragmentManager.findFragmentByTag("eventListFragment"));
        }

        eventListFragment = new EventListFragment();

        if(args != null){
            eventListFragment.setArguments(args);
        }

        fragmentTransaction.replace(R.id.flContent, eventListFragment, "eventListFragment")
                .commit();
    }

    private boolean isMapView;

    private void setupSearch() {
        View view = getLayoutInflater().inflate(R.layout.search_bar, null);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(view);

        ibListView = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.ibListView);
        ibMapView = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.ibMapView);

        btnSearchContent = (ImageButton) getSupportActionBar().getCustomView().findViewById(R.id.btnSearchContent);

        etSearch1 = (EditText) getSupportActionBar().getCustomView().findViewById(R.id.etSearch1);
        etSearch2 = (EditText) getSupportActionBar().getCustomView().findViewById(R.id.etSearch2);
        ivSearchClear1 = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.ivSearchClear1);
        ivSearchClear2 = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.ivSearchClear2);

        if(etSearch2.getText().toString().isEmpty()){
            etSearch2.setText("San Francisco");
        }


        etSearch1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Enable clear button
                ivSearchClear1.setVisibility(View.VISIBLE);
            }
        });

        ivSearchClear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch1.setText("");
                ivSearchClear1.setVisibility(View.INVISIBLE);
            }
        });

        etSearch2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ivSearchClear2.setVisibility(View.VISIBLE);
            }
        });

        ivSearchClear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch2.setText("");
                ivSearchClear2.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        btnSearchContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "q-food:" + etSearch1.getText());
                Log.d("DEBUG", "q-location:" + etSearch2.getText());

                try {
                    if(isMapView){
                        setupMap(etSearch1.getText().toString(), etSearch2.getText().toString());
                    }else {
                        Bundle args = new Bundle();
                        args.putString("searchFood", etSearch1.getText().toString());
                        args.putString("searchLocality", etSearch2.getText().toString());
                        setupEventList(args);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        ibMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMapView = true;
                setupMap(null, null);
                showhideIcons();
            }
        });
        ibListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMapView = false;
                setupEventList(null);
                showhideIcons();
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    GoogleMap.OnInfoWindowClickListener mInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            // Check if we have eventObjectId for this marker or not and then redirect to activity
            String eventObjectId = mMarkersToEventIdMap.get(marker.getId());
            if(eventObjectId != null && !eventObjectId.isEmpty()){
                // Redirect to detail event view
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("eventObjectId", eventObjectId);
                startActivity(intent);
            }
        }
    };

    GoogleMap.InfoWindowAdapter mInfoWindowAdapter = new GoogleMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    };

    private void showhideIcons(){
        if(isMapView){
            ibMapView.setVisibility(View.GONE);
            ibListView.setVisibility(View.VISIBLE);
        }else {
            ibMapView.setVisibility(View.VISIBLE);
            ibListView.setVisibility(View.GONE);
        }
    }
}
