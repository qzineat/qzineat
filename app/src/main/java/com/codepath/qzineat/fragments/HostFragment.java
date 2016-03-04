package com.codepath.qzineat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.Event;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static java.lang.Integer.parseInt;

/**
 * Created by glondhe on 3/1/16.
 */
public class HostFragment extends Fragment {

    ArrayAdapter arrayAdapter;
    @Bind(R.id.tvTitle)
    TextView tvTitile;
    @Bind(R.id.etTitle)
    EditText etTitile;
    @Bind(R.id.tvDate)  TextView tvDate;
    @Bind(R.id.tvDatePicker) TextView tvDatePicker;
    @Bind(R.id.tvGuest) TextView tvGuest;
    @Bind(R.id.spGuest)
    Spinner spGuest;
    @Bind(R.id.tvCharge) TextView tvCharge;
    @Bind(R.id.etCharge) EditText etCharge;
    @Bind(R.id.tvVenue) TextView tvVenue;
    @Bind(R.id.etVenue) EditText etVenue;
    @Bind(R.id.tvDesc) TextView tvDesc;
    @Bind(R.id.etDesc) EditText etDesc;
    @Bind(R.id.tvAlcohol) TextView tvAlcohol;
    @Bind(R.id.spAlcohol) Spinner spAlcohol;
    @Bind(R.id.btSave) Button btSave;
    @Bind(R.id.btCancel) Button btCancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.host_layout, container, false);
        ButterKnife.bind(this, view);

        spGuest.setAdapter(arrayAdapter);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent(getContext());
            }
        });
        return view;
    }

    private void saveEvent(final Context context) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Parse Save
        Event event = new Event();
        event.setTitle(etTitile.getText().toString());
        event.setGuestLimit(parseInt(String.valueOf(spGuest.getSelectedItem())));
        event.setDescription(etDesc.getText().toString());
        event.setAddress(etVenue.getText().toString());
        event.setPrice(parseInt(String.valueOf(etCharge.getText())));
        event.setGuestLimit(parseInt(String.valueOf(spGuest.getSelectedItem())));
        event.setAlcohol(spAlcohol.getSelectedItem().toString());
        event.setImageUrl("http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2012/10/Food.jpg");
        event.setHost(currentUser);


        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                    Toast.makeText(context, "Successfully created event on Parse", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter();

    }

    private void setListAdapter() {
        List list = new ArrayList<>();
        for(int i=0;i<100;i++) {
            if (i == 0)list.add("Choose");
            else list.add(i);
        }
        arrayAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item,list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
    }
}
