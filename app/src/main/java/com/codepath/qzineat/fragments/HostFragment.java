package com.codepath.qzineat.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.models.User;
import com.codepath.qzineat.utils.FragmentCode;
import com.codepath.qzineat.utils.GeoUtil;
import com.codepath.qzineat.utils.QZinUtil;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static java.lang.Integer.parseInt;

/**
 * Created by glondhe on 3/1/16.
 */
public class HostFragment extends Fragment{

    public static final int DAILOG_FRAGMENT = 1;
    private static final int RESULT_OK = -1 ;
    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMAGE = 1;
    public static StringBuilder date;
    private int _year;
    private int _month;
    private int _day;
    private String imgDecodableString;
    private Date dateObject;
    Bitmap bitmap;

    ArrayAdapter arrayAdapter;
    @Bind(R.id.ivEventImage)
    ImageView ivEventImage;

    @Bind(R.id.tilTitle) TextInputLayout tilTitle;
    @Bind(R.id.tilCharge) TextInputLayout tilCharge;
    @Bind(R.id.tilVenue) TextInputLayout tilVenue;
    @Bind(R.id.tilDesc) TextInputLayout tilDesc;
    @Bind(R.id.tilDate)
    TextInputLayout tilDate;
    @Bind(R.id.tilTime)
    TextInputLayout tilTime;

    @Bind(R.id.etTitle)
    EditText etTitile;

    @Bind(R.id.tvDatePicker)
    TextView tvDatePicker;
    @Bind(R.id.tvTimePicker)
    TextView tvTimePicker;
    @Bind(R.id.spGuest)
    Spinner spGuest;

    @Bind(R.id.etCharge)
    EditText etCharge;

    @Bind(R.id.etVenue)
    EditText etVenue;

    @Bind(R.id.sMenuCategory)
    Spinner sMenuCategory;

    @Bind(R.id.etDesc)
    EditText etDesc;
    @Bind(R.id.spAlcohol)
    Spinner spAlcohol;
    @Bind(R.id.btSave)
    Button btSave;

    private Intent intent;

    private Intent logInIntent;
    private ArrayAdapter<String> MenuCategoryAdapter;
    private Date TimeObject;
    private String eventObjectId = null;
    private Event evt;
    private byte[] imageData;
    private String etTitile_string;

    public static HostFragment newInstance(String eventObjectId){
        HostFragment fragment = new HostFragment();
        Bundle args = new Bundle();
        args.putString("eventObjectId", eventObjectId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        int count = this.getFragmentManager().getBackStackEntryCount();
        final Fragment frag = getFragmentManager().getFragments().get(count>0?count-1:count);
        
        View view = inflater.inflate(R.layout.host_layout, container, false);
        ButterKnife.bind(this, view);


        if(logInIntent != null && !User.isUserLoggedIn()){
            // Send me to event list
            Fragment eventListFragment = new EventListFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.flContent, eventListFragment);
            transaction.commit();
        }

        if(getArguments() != null) {
            eventObjectId = getArguments().getString("eventObjectId");
            if (eventObjectId != null && !eventObjectId.isEmpty()) {
                getEvent();
            }
        }

        spGuest.setAdapter(arrayAdapter);
        sMenuCategory.setAdapter(MenuCategoryAdapter);
        tvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        tvTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "TimePicker");
            }
        });
        ivEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DailogFragment dailogFragment = new DailogFragment();
                dailogFragment.setTargetFragment(HostFragment.this, DAILOG_FRAGMENT);
                dailogFragment.show(getActivity().getSupportFragmentManager(), "Photo");
            }
        });

        Glide.with(this).load(QZinUtil.getQZinImageUrl()).centerCrop().into(ivEventImage);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveEvent(getContext());

//                ParsePush.subscribeInBackground("Giants");
//                ParsePush push = new ParsePush();
//                push.setChannel("Giants");
//                push.setMessage("The Giants just scored! It's now 2-2 against the Mets.");
//                push.sendInBackground();

            }
        });

        return view;

    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (getArguments() != null) {
//            Bundle b = getArguments();
//            if (b != null) {
//                Log.d("DEBUG", b.size() + "");
//                if (null != b.getString("imgDecodableString")) {
//                    imgDecodableString = b.getString("imgDecodableString");
//                    ivEventImage.setImageBitmap(BitmapFactory
//                            .decodeFile(imgDecodableString));
//                } else if (null != b.getByteArray("bitMapPhoto")) {
//                    imageData = b.getByteArray("bitMapPhoto");
//                    Bitmap photo = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
//                    ivEventImage.setImageBitmap(photo);
//                }
//            } else {
//                Log.d("DEBUG", "Bundle is null. selected photo not passed from Dailog fragment");
//            }
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // See if it comes from Login

        try {


            // This is login request
            if(requestCode == FragmentCode.HOST_FRAGMENT_LOGIN_CODE){
                logInIntent = data;
                return;
            }

            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                Log.d("DEBUG", b.size() + "");
                if (null != b.getString("imgDecodableString")) {
                    imgDecodableString = b.getString("imgDecodableString");
                    ivEventImage.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString));
                } else if (null != b.getByteArray("bitMapPhoto")) {
                    imageData = b.getByteArray("bitMapPhoto");
                    Bitmap photo = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    ivEventImage.setImageBitmap(photo);
                }
            } else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getContext(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void saveEvent(final Context context) {

        // Parse Save
        if(getArguments() != null) {
            eventObjectId = getArguments().getString("eventObjectId");
            if (eventObjectId != null && !eventObjectId.isEmpty()) {
                ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
                // TODO: check later that we can get Event with all attendees or not
                Log.d("DEBUG_eventObjectId", eventObjectId.toString());
                query.getInBackground(eventObjectId, new GetCallback<Event>() {
                    @Override
                    public void done(Event object, ParseException e) {
                        if (e == null) {
                            evt = object;
                        }
                    }
                });
            }
        } else {
            evt = new Event();
        }
        setEventDetails(evt, context);
    }

    private void setEventDetails(Event event, final Context context) {

        // Validations
        if (!validateTitle()) {
            return;
        }
        if (!validateCharge()) {
            return;
        }
        if (!validateVenue()) {
            return;
        }
        if (!validateDesc()) {
            return;
        }
        if (!validateDate()) {
            return;
        }
        if (!validateTime()) {
            return;
        }

        Boolean FLAG = true;

        String inputTitle = etTitile.getText().toString();
        event.setTitle(inputTitle);
        int inputCharge = Integer.valueOf(String.valueOf(etCharge.getText()));
        event.setPrice(inputCharge);
        String inputDesc = etDesc.getText().toString();
        event.setDescription(inputDesc);

        if (!String.valueOf(spGuest.getSelectedItem()).equalsIgnoreCase("Choose")) {
            event.setAttendeesMaxCount(parseInt(String.valueOf(spGuest.getSelectedItem())));
        }

        if (!String.valueOf(sMenuCategory.getSelectedItem()).equalsIgnoreCase("Choose")) {
            event.setCategory((String) sMenuCategory.getSelectedItem());
        }

        if (tvDatePicker.getText().toString() != null && !tvDatePicker.getText().toString().isEmpty() && tvTimePicker.getText().toString() != null && !tvTimePicker.getText().toString().isEmpty()) {
            String dateString = tvDatePicker.getText().toString();
            String timeString = tvTimePicker.getText().toString();
            dateString = dateString + " " + timeString;
            dateObject = getDateObject(dateString);
            event.setDate(dateObject);
        }

        // Get Location & Correct Locality with Country code
        // 500 Walnut Ave #G204, Fremont, CA, 94538
        String inputAddress = etVenue.getText().toString();
        Address address = GeoUtil.getGeoAddress(getContext(), inputAddress);
        event.setLocation(GeoUtil.getLocation(address)); // This will be used as location search
        event.setLocality(GeoUtil.getLocality(address));
        event.setAddress(inputAddress);


        if (!String.valueOf(spAlcohol.getSelectedItem()).equalsIgnoreCase("Choose")) {
            event.setAlcohol(spAlcohol.getSelectedItem().toString());
        }
        try{
            if ( ivEventImage.getDrawable() != null) {
                bitmap = ((BitmapDrawable) ivEventImage.getDrawable()).getBitmap();
                byte[] text = BitMapToString(bitmap);
                ParseFile File = new ParseFile("EventImage.txt", text);
                event.setImageFile(File);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            // TODO: GlideBitmapDrawable cannot be cast to android.graphics.drawable.BitmapDrawable
        }


        if (FLAG == true) {
            event.setHost(User.getLoggedInUser());
            User.getLoggedInUser().setIsHost(true);
            event.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null)
                        Log.d("DEBUG", "Successfully created event on Parse");
                    Toast.makeText(context, "Successfully created event on Parse", Toast.LENGTH_SHORT).show();
                }
            });

            HostListFragment hostListFragment = new HostListFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.flContent, hostListFragment);
            transaction.commit();

        }else Toast.makeText(getContext(), "All entries are Mandatory!!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateTitle() {
        if (etTitile.getText().toString().trim().isEmpty()) {
            tilTitle.setError(getString(R.string.err_msg_required));
            requestFocus(etTitile);
            return false;
        } else {
            tilTitle.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateDate() {
        if (tvDatePicker.getText().toString().trim().isEmpty()) {
            tilDate.setError(getString(R.string.err_msg_required));
            requestFocus(tvDatePicker);
            return false;
        } else {
            tilTitle.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateTime() {
        if (tvTimePicker.getText().toString().trim().isEmpty()) {
            tilTime.setError(getString(R.string.err_msg_required));
            requestFocus(tvTimePicker);
            return false;
        } else {
            tilTitle.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateCharge() {
        if (etCharge.getText().toString().trim().isEmpty()) {
            tilCharge.setError(getString(R.string.err_msg_required));
            requestFocus(etCharge);
            return false;
        } else {
            tilCharge.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateVenue() {
        if (etVenue.getText().toString().trim().isEmpty()) {
            tilVenue.setError(getString(R.string.err_msg_required));
            requestFocus(etVenue);
            return false;
        } else {
            tilVenue.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateDesc() {
        if (etDesc.getText().toString().trim().isEmpty()) {
            tilDesc.setError(getString(R.string.err_msg_required));
            requestFocus(etDesc);
            return false;
        } else {
            tilDesc.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Redirect User to LoginFragment
        if (!User.isUserLoggedIn()) {
            Fragment fragment = new LoginFragment();
            fragment.setTargetFragment(HostFragment.this, FragmentCode.HOST_FRAGMENT_LOGIN_CODE);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.flContent, fragment);
            // Commit the transaction
            transaction.commit();
        }

        setMenuCategory();
        setListAdapter();

    }

    private Event getEvent() {
            // Construct query to execute
            ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
            // TODO: check later that we can get Event with all attendees or not
            Log.d("DEBUG_eventObjectId", eventObjectId.toString());
            query.getInBackground(eventObjectId, new GetCallback<Event>() {
                @Override
                public void done(Event object, ParseException e) {
                    if (e == null) {
                        evt = object;
                        setValues(evt);
                    }
                }
            });
        return evt;
    }

    private void setValues(Event evnt) {

        if (evnt != null) {
            ParseFile pf = evnt.getImageFile();
            String imgUrl;
            if(pf != null && !pf.getUrl().isEmpty()){
                imgUrl = pf.getUrl();
            }else {
                imgUrl = QZinUtil.getQZinImageUrl();
            }

            Glide.with(getContext()).load(imgUrl).asBitmap().centerCrop().into(ivEventImage);
            etTitile.setText(evnt.getTitle());
            etTitile.setCursorVisible(false);
            Log.d("DEBUG_date", evnt.getDate().toString());
            tvDatePicker.setText(getDate(evnt.getDate().toString()));
            tvTimePicker.setText(getTime(evnt.getDate().toString()));

            Log.d("DEBUG_charge", String.valueOf(evnt.getPrice()));

            etCharge.setText(String.valueOf(evnt.getPrice()));
            etVenue.setText(evnt.getAddress());

            etDesc.setText(evnt.getDescription().toString());
            int pos;
            if (evnt.getAlcohol().toString() == "Yes") pos = 2;
                    else pos = 1;
            spAlcohol.setSelection(pos);
            spGuest.setSelection(arrayAdapter.getPosition(evnt.getAttendeesMaxCount()));
            sMenuCategory.setSelection(MenuCategoryAdapter.getPosition(evnt.getCategory()));

        } else Log.d("DEBUG", "Event returned null");

    }

    private Date getDateObject(String dateString) {

        DateFormat formatter = new SimpleDateFormat("dd MMM yy hh:mm aa"); // Make sure user insert date into edittext in this format.
        Date dateObject = null;
        String dob_var=(dateString);
        try {
            dateObject = formatter.parse(dob_var);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return dateObject;
    }

    private String getTime(String dateString) {

        String plainFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        String qzinFormat = "hh:mm aa";
        SimpleDateFormat sf = new SimpleDateFormat(plainFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeTime = "";
        try {
            Date date = sf.parse(dateString);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(qzinFormat);
            relativeTime = simpleDateFormat.format(date).toString();

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeTime;
    }

    private String getDate(String dateString) {

        String plainFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        String qzinFormat = "dd MMM yy";
        SimpleDateFormat sf = new SimpleDateFormat(plainFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            Date date = sf.parse(dateString);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(qzinFormat);
            relativeDate = simpleDateFormat.format(date).toString();

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

//    private Date getTimeObject(String timeString) {
//
//        DateFormat formatter = new SimpleDateFormat("hh:mm aa");
//        Date dateObject = null;
//        String dob_var=(timeString);
//
//        try {
//            dateObject = formatter.parse(dob_var);
//        } catch (java.text.ParseException e) {
//            e.printStackTrace();
//        }
//        return dateObject;
//    }

    private void setMenuCategory() {

        List<String> list = new ArrayList<String>();
        list.add("Choose");
        list.add("American");
        list.add("Chinese");
        list.add("French");
        list.add("Japanese");
        list.add("Korean");
        list.add("Italian");
        list.add("Indian");
        list.add("Mediterranean");
        list.add("Mexican");
        list.add("Thai");
        list.add("Vegan");
        list.add("Vegetarian");
        list.add("vietnamese");

        MenuCategoryAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuCategoryAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
    }

    private void setListAdapter() {
        List list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i == 0) list.add("Choose");
            else list.add(i);
        }
        arrayAdapter = new ArrayAdapter<>(this.getActivity(),
        android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
    }

    public byte [] BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return b;
    }

//    public static Bitmap decodeBase64(String input)
//    {
//        byte[] decodedByte = Base64.decode(input, 0);
//        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
//    }



}
