package com.codepath.qzineat.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.fragments.HomeFragment;
import com.codepath.qzineat.interfaces.CommunicationChannel;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity implements CommunicationChannel {

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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    /*private void setupSearch(){
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
    }*/

}
