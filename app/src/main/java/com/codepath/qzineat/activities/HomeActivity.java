package com.codepath.qzineat.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.codepath.qzineat.QZinEatApplication;
import com.codepath.qzineat.R;
import com.codepath.qzineat.fragments.EnrollEventFragment;
import com.codepath.qzineat.fragments.EventDetailFragment;
import com.codepath.qzineat.fragments.HomeFragment;
import com.codepath.qzineat.interfaces.CommunicationChannel;
import com.codepath.qzineat.utils.QZinUtil;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity implements CommunicationChannel {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QZinUtil.onActivityCreateSetTheme(this); // Change Theme
        setContentView(R.layout.activity_home);


        String eventObjectId = getIntent().getStringExtra("eventObjectId");

        if(eventObjectId != null && !eventObjectId.isEmpty()){ // Open Detail Fragment
            EventDetailFragment fragment = EventDetailFragment.newInstance(eventObjectId);
            openFragment(fragment);
        }else {
            // Ok Open Home Fragment or Hosted Events
            if(QZinEatApplication.isHostView){
                EnrollEventFragment fragment = new EnrollEventFragment();
                openFragment(fragment);
            }else {
                HomeFragment fragment = new HomeFragment();
                openFragment(fragment);
            }
        }


    }



    @Override
    public void openFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent, fragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            Log.d("DEBUG", this.getClass().toString()+"::"+ "Back stack count 0");
            this.finish();
        } else {
            // TODO: Handle This
            Log.d("DEBUG", "Please fix this -----");
            Log.d("DEBUG", this.getClass().getName()+"::"+ "Back stack support count "+ getSupportFragmentManager().getBackStackEntryCount());
            Log.d("DEBUG", this.getClass().getName()+"::"+ "Back stack count "+ getFragmentManager().getBackStackEntryCount());
        }
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
                    Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(i);
                }
            }
        });
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send to another activity
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(i);
            }
        });
    }*/


}
