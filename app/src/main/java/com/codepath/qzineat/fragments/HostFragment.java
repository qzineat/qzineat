package com.codepath.qzineat.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.activities.LoginActivity;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.models.User;
import com.codepath.qzineat.utils.FragmentCode;
import com.codepath.qzineat.utils.GeoUtil;
import com.codepath.qzineat.utils.QZinUtil;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
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
public class HostFragment extends BaseFragment {



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
    EditText tvDatePicker;
    @Bind(R.id.tvTimePicker)
    EditText tvTimePicker;

    @Bind(R.id.spGuest)
    Spinner spGuest;

    @Bind(R.id.etCharge)
    EditText etCharge;

    @Bind(R.id.etVenue)
    EditText etVenue;

    @Bind(R.id.sMenuCategory)
    Spinner sMenuCategory;

    @Bind(R.id.sMenuItem)
    Spinner sMenuItem;

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
    private ParseObject pObject;
    private List<Bitmap> mediaListImages = new ArrayList<Bitmap>();
    private View cell;
    private View view;
    private LinearLayout mainLayout;
    private boolean flag = false;
    private long mLastClickTime = 0;

    public static HostFragment newInstance(String eventObjectId){
        HostFragment fragment = new HostFragment();
        Bundle args = new Bundle();
        args.putString("eventObjectId", eventObjectId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Selected item in drawer..
        drawer.setSelection(hostEventItem, false);

        // Redirect User to LoginFragment
        if (!User.isUserLoggedIn()) {
            /*Fragment fragment = new LoginFragment();
            fragment.setTargetFragment(HostFragment.this, FragmentCode.HOST_FRAGMENT_LOGIN_CODE);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.flContent, fragment);
            // Commit the transaction
            transaction.commit();*/
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }

        setMenuCategory();
        setListAdapter();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        int count = this.getFragmentManager().getBackStackEntryCount();
        final Fragment frag = getFragmentManager().getFragments().get(count > 0 ? count - 1 : count);
        
        view = inflater.inflate(R.layout.host_layout, container, false);
        mainLayout = (LinearLayout) view.findViewById(R.id._linearLayout);

        ButterKnife.bind(this, view);

//        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView);
//        scrollView.setFocusableInTouchMode(true);
//        scrollView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        spGuest.isFocusable();
        spGuest.setFocusableInTouchMode(true);
        sMenuCategory.isFocusable();
        sMenuCategory.setFocusableInTouchMode(true);
        sMenuItem.isFocusable();
        sMenuItem.setFocusableInTouchMode(true);
        spAlcohol.isFocusable();
        spAlcohol.setFocusableInTouchMode(true);

        ivEventImage.setImageResource(R.drawable.ic_camera);
        //Glide.with(this).load(getResources().(R.drawable.ic_camera)).centerCrop().into(ivEventImage);

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
        sMenuCategory.setOnItemSelectedListener(new MenuItemOnClickListener());
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

        spGuest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // changes here
                if (flag == false)
                    flag = true;
                else
                    etCharge.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spAlcohol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // changes here
                if (flag == false)
                    flag = true;
                else
                    btSave.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                saveEvent(getContext());
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


            }
        });


        return view;

    }

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
//                  Glide.with(this).load(QZinUtil.getQZinImageUrl()).centerCrop().into(ivEventImage);
                    ivEventImage.setImageResource(R.drawable.ic_camera);
                    mediaListImages.add(BitmapFactory
                            .decodeFile(imgDecodableString));
                    setImages(BitmapFactory
                            .decodeFile(imgDecodableString));
                } else if (null != b.getByteArray("bitMapPhoto")) {
                    imageData = b.getByteArray("bitMapPhoto");
                    Bitmap photo = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    mediaListImages.add(photo);
                    setImages(photo);
//                  Glide.with(this).load(QZinUtil.getQZinImageUrl()).centerCrop().into(ivEventImage);
                    ivEventImage.setImageResource(R.drawable.ic_camera);
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

    private void setImages(Bitmap photo) {

        cell = LayoutInflater.from(getContext()).inflate(R.layout.cell, null);

        final ImageView imageView = (ImageView) cell.findViewById(R.id._image);

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mainLayout.removeView(cell);
                Toast.makeText(getContext(),
                        "Image Deleted", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

//        imageView.setTag("#" + (1));

        TextView text = (TextView) cell.findViewById(R.id._imageName);
        //  Glide.with(mContext).load(imgUrl).asBitmap().centerCrop().into(images[i]);
        imageView.setImageBitmap(photo);
  //      text.setText("#" + (1));
        mainLayout.addView(cell);
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

        if (!String.valueOf(sMenuItem.getSelectedItem()).equalsIgnoreCase("Choose")) {
            event.setCategory((String) sMenuItem.getSelectedItem());
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
            if ( mediaListImages.size() > 0 ) {

                pObject = new ParseObject("mediaFiles");
                ArrayList<ParseFile> pFileList = new ArrayList<ParseFile>();
                ParseFile pFile;
                for ( int i=0; i<mediaListImages.size(); i++ ) {
                    byte[] imgData = BitMapToString(mediaListImages.get(i));
                    pFile = new ParseFile("EventImage.txt", imgData);
                    if (i==0) event.setImageFile(pFile);
                    pFile.save();
                    pFileList.add(pFile);
                }

                pObject.addAll("mediaFiles", pFileList);
                pObject.saveEventually();
                event.setMediaObject(pObject);
            }else {
                pObject = new ParseObject("mediaFiles");
                event.setMediaObject(pObject);
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
                        Snackbar snackbar = Snackbar
                            .make(view, "    Event created Successfully!!", Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.accent));

                        snackbar.show();
                        //Toast.makeText(context, "Successfully created event on Parse", Toast.LENGTH_SHORT).show();
                        HostListFragment hostListFragment = new HostListFragment();
                        openFragment(hostListFragment);
                    }
            });

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



    private Event getEvent() {
            // Construct query to execute
            ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
            // TODO: check later that we can get Event with all attendees or not
            Log.d("DEBUG_eventObjectId", eventObjectId.toString());
            query.include("mediaFiles");
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

            getImages(evnt);
            Glide.with(getContext()).load(QZinUtil.getQZinImageUrl()).asBitmap().centerCrop().into(ivEventImage);
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
            if (evnt.getAlcohol().toString() == "Yes") pos = 3;
                    else pos = 2;
            spAlcohol.setSelection(pos);
            spGuest.setSelection(arrayAdapter.getPosition(evnt.getAttendeesMaxCount())+1);
        //    sMenuCategory.setSelection(MenuCategoryAdapter.getPosition(evnt.getCategory())+1);
            sMenuCategory.setSelection(3);
            setMenuItemJapanese();
            sMenuItem.setSelection(1);
        } else Log.d("DEBUG", "Event returned null");

    }


    private void getImages(Event evnt) {

        pObject = evnt.getMediaObject();
        List<ParseFile> pFileList = null;
        if (null != pObject) {
            try {
                pFileList = (ArrayList<ParseFile>) pObject.fetchIfNeeded().get("mediaFiles");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (null != pFileList && !pFileList.isEmpty()) {
            mediaListImages.clear();
            for (int i = 0; i < pFileList.size(); i++) {
                ParseFile pFile = pFileList.get(i);
                byte[] bitmapdata = new byte[0];  // here it throws error
                try {
                    bitmapdata = pFile.getData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                cell = LayoutInflater.from(getContext()).inflate(R.layout.cell, null);

                final ImageView imageView = (ImageView) cell.findViewById(R.id._image);

                mediaListImages.add(bitmap);
                TextView text = (TextView) cell.findViewById(R.id._imageName);
                //Glide.with(getContext()).load(pFile.getUrl()).centerCrop().into(imageView);
                imageView.setImageBitmap(bitmap);
                text.setText("#" + (i + 1));
                mainLayout.addView(cell);

                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mainLayout.removeView(cell);

                        Toast.makeText(getContext(),
                                "Image Deleted", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

            }
        }

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


    private void setMenuCategory() {

        List<String> list = new ArrayList<String>();
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

        MenuCategoryAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuCategoryAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
    }

    private void setMenuItemAmerican() {

        List<String> list = new ArrayList<String>();
        list.add("Cheese Burger");
        list.add("Hamburgers");
        list.add("BBQ Pork Ribs");
        list.add("Steak");
        list.add("Pork Chop");
        list.add("Blackened Ribeye");
        list.add("New York Steak");
        list.add("Fish Tacos");
        list.add("Grilled Salmon");
        list.add("Fried Chicken");
        list.add("Ribs");
        list.add("Potato Salad With Bacon");
        list.add("Meat Loaf");

        ArrayAdapter<String> MenuitemAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuitemAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sMenuItem.setAdapter(MenuitemAdapter);
    }


    private void setMenuItemChinese() {

        List<String> list = new ArrayList<String>();
        list.add("Chicken Chow Mein");
        list.add("Pork Fried Rice");
        list.add("Vegetable Egg Rolls");
        list.add("Broccoli & Beef");
        list.add("Mongolian Chicken");
        list.add("Mongolian Beef");
        list.add("Lemon Chicken");
        list.add("General Chicken");

        ArrayAdapter<String> MenuitemAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuitemAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sMenuItem.setAdapter(MenuitemAdapter);
    }

    private void setMenuItemFrench() {

        List<String> list = new ArrayList<String>();
        list.add("Poulet aux Porto");
        list.add("Poulet à la Provençale");
        list.add("Gingembre et Citron Vert");
        list.add("Saumon Sauce Endives");
        list.add("Bar au Beurre Blanc et Crabe");
        list.add("Truite aux Amandes ");

        ArrayAdapter<String> MenuitemAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuitemAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sMenuItem.setAdapter(MenuitemAdapter);
    }

    private void setMenuItemJapanese() {

        List<String> list = new ArrayList<String>();
        list.add("Sushi");
        list.add("California Roll");
        list.add("Tuna Sashimi");
        list.add("Beef Teriyaki");
        list.add("Chicken Teriyaki");
        list.add("Salmon Teriyaki");
        list.add("Tempura");
        list.add("Sashimi");
        list.add("Negima");
        list.add("Kokoro");
        list.add("Bonjiri");

        ArrayAdapter<String> MenuitemAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuitemAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sMenuItem.setAdapter(MenuitemAdapter);
    }

    private void setMenuItemKorean() {

        List<String> list = new ArrayList<String>();
        list.add("SAMGYUBSAL");
        list.add("GEN CAJUN CHICKEN");
        list.add("GARLIC CHICKEN");
        list.add("GEN ROAST BEEF");
        list.add("GEN WOO BAESAL");
        list.add("GEN PORK BULGOGI");
        list.add("GEN BEEF BULGOGI");
        list.add("DWENJANG");
        list.add("SOONDOBOO");


        ArrayAdapter<String> MenuitemAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuitemAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sMenuItem.setAdapter(MenuitemAdapter);
    }

    private void setMenuItemItalian() {

        List<String> list = new ArrayList<String>();
        list.add("Pasta");
        list.add("Pizza");
        list.add("Italian Sausage");
        list.add("Rigatoni");
        list.add("Lobster Carbonara");
        list.add("Chicken Parmesan");
        list.add("Meatballs");
        list.add("Lasagna");

        ArrayAdapter<String> MenuitemAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuitemAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sMenuItem.setAdapter(MenuitemAdapter);
    }

    private void setMenuItemIndian() {

        List<String> list = new ArrayList<String>();
        list.add("Samosa");
        list.add("Chicken Curry");
        list.add("Goat Curry");
        list.add("Chicken Biryani");
        list.add("Goat Biryani");
        list.add("Chettinad");
        list.add("Panner Tikka Masala");
        list.add("Chicken Tikka Masala");

        ArrayAdapter<String> MenuitemAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuitemAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sMenuItem.setAdapter(MenuitemAdapter);
    }

    private void setMenuItemMediterranean() {

        List<String> list = new ArrayList<String>();
        list.add("FALAFEL");
        list.add("CHICKEN SHAWARMA");
        list.add("SHALAFEL");
        list.add("CHICKEN SKEWERS");
        list.add("STEAK SKEWERS");
        list.add("SALMON SKEWERS");
        list.add("Hummus and Pita");

        ArrayAdapter<String> MenuitemAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuitemAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sMenuItem.setAdapter(MenuitemAdapter);
    }

    private void setMenuItemMexican() {

        List<String> list = new ArrayList<String>();
        list.add("STEAK FAJITAS");
        list.add("CHICKEN FAJITAS");
        list.add("SHRIMP FAJITAS");
        list.add("ENCHILADAS");
        list.add("TACOS");
        list.add("BURRITO");
        list.add("TILAPIA VERACRUZ");
        list.add("CHICKEN ZUCCHINI");
        list.add("QUESADILLA");

        ArrayAdapter<String> MenuitemAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuitemAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sMenuItem.setAdapter(MenuitemAdapter);
    }

    private void setMenuItemThai() {

        List<String> list = new ArrayList<String>();
        list.add("Yellow Curry");
        list.add("Red Curry");
        list.add("Green Curry");
        list.add("Pad See-EW");
        list.add("Fried Rice");
        list.add("Noodle Soup");

        ArrayAdapter<String> MenuitemAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuitemAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sMenuItem.setAdapter(MenuitemAdapter);
    }

    private void setMenuItemVegetarian() {

        List<String> list = new ArrayList<String>();
        list.add("Tofu Rice");
        list.add("Eggplant Sandwich");
        list.add("Coconut Curry");
        list.add("Lemon Rice");
        list.add("Broccoli Panner");
        list.add("Pasta with Green");
        list.add("Spinach Gnocchi");
        list.add("Falafel burgers");

        ArrayAdapter<String> MenuitemAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuitemAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sMenuItem.setAdapter(MenuitemAdapter);
    }
    
    private void setMenuItemVegan() {

        List<String> list = new ArrayList<String>();
        list.add("Avocado Reuben");
        list.add("Corn Tacos");
        list.add("Whole Wheat pasta");
        list.add("Mashed Potatoes");
        list.add("Grilled Tofu");
        list.add("Mushroom Steaks");
        list.add("Louisiana Gumbo");

        ArrayAdapter<String> MenuitemAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_list_item_1, list);
        MenuitemAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sMenuItem.setAdapter(MenuitemAdapter);
    }

    private class MenuItemOnClickListener implements android.widget.AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            parent.getItemAtPosition(position);
            if (-1 == position);
            else {
                String choice = MenuCategoryAdapter.getItem(position);

                if (choice.equalsIgnoreCase("American")) {
                    setMenuItemAmerican();

                } else if (choice.equalsIgnoreCase("Chinese")) {
                    setMenuItemChinese();
                } else if (choice.equalsIgnoreCase("French")) {
                    setMenuItemFrench();
                } else if (choice.equalsIgnoreCase("Japanese")) {
                    setMenuItemJapanese();
                } else if (choice.equalsIgnoreCase("Korean")) {
                    setMenuItemKorean();
                } else if (choice.equalsIgnoreCase("Italian")) {
                    setMenuItemItalian();
                } else if (choice.equalsIgnoreCase("Indian")) {
                    setMenuItemIndian();
                } else if (choice.equalsIgnoreCase("Mediterranean")) {
                    setMenuItemMediterranean();
                } else if (choice.equalsIgnoreCase("Mexican")) {
                    setMenuItemMexican();
                } else if (choice.equalsIgnoreCase("Thai")) {
                    setMenuItemThai();
                } else if (choice.equalsIgnoreCase("Vegan")) {
                    setMenuItemVegan();
                } else if (choice.equalsIgnoreCase("Vegetarian")) {
                    setMenuItemVegetarian();
                }
                sMenuItem.requestFocus();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

//    public static Bitmap decodeBase64(String input)
//    {
//        byte[] decodedByte = Base64.decode(input, 0);
//        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
//    }

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
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte [] b=baos.toByteArray();
            String temp=Base64.encodeToString(b, Base64.DEFAULT);
            return b;
        }



}
