package com.codepath.qzineat.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.fragments.EventDetailFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EventDetailActivity extends AppCompatActivity  {


    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Event Detail
        if(savedInstanceState == null){
            EventDetailFragment fragment = EventDetailFragment.newInstance(getIntent().getStringExtra("eventObjectId"));

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
