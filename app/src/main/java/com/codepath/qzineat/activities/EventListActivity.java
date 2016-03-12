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

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.fragments.EventListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EventListActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupSearch();

        setupFragment();

    }

    public void setupFragment(){
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
