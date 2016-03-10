package com.codepath.qzineat.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.models.User;
import com.codepath.qzineat.utils.FragmentCode;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static java.lang.Integer.parseInt;

/**
 * Created by glondhe on 3/1/16.
 */
public class HostFragment extends Fragment{

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
    @Bind(R.id.ivEventImage2)
    ImageView ivEventImage2;
    @Bind(R.id.tvTitle)
    TextView tvTitile;
    @Bind(R.id.etTitle)
    EditText etTitile;
    @Bind(R.id.btUpload)
    Button btUpload;
    @Bind(R.id.btCamera)
    Button btCamera;
    @Bind(R.id.tvDate)
    TextView tvDate;
    @Bind(R.id.tvDatePicker)
    TextView tvDatePicker;
    @Bind(R.id.tvTimePicker)
    TextView tvTimePicker;
    @Bind(R.id.tvGuest)
    TextView tvGuest;
    @Bind(R.id.spGuest)
    Spinner spGuest;
    @Bind(R.id.tvCharge)
    TextView tvCharge;
    @Bind(R.id.etCharge)
    EditText etCharge;
    @Bind(R.id.tvVenue)
    TextView tvVenue;
    @Bind(R.id.etVenue)
    EditText etVenue;
    @Bind(R.id.sMenuCategory)
    Spinner sMenuCategory;
    @Bind(R.id.tvDesc)
    TextView tvDesc;
    @Bind(R.id.etDesc)
    EditText etDesc;
    @Bind(R.id.tvAlcohol)
    TextView tvAlcohol;
    @Bind(R.id.spAlcohol)
    Spinner spAlcohol;
    @Bind(R.id.btSave)
    Button btSave;
    @Bind(R.id.btCancel)
    Button btCancel;
    private Intent intent;

    private Intent logInIntent;
    private ArrayAdapter<String> MenuCategoryAdapter;
    private Date TimeObject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.host_layout, container, false);
        ButterKnife.bind(this, view);


        if(logInIntent != null && !User.isUserLoggedIn()){
            // Send me to event list
            Fragment eventListFragment = new EventListFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.flContent, eventListFragment);
            transaction.commit();
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
        btUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        btCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveEvent(getContext());

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

            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                ivEventImage.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ivEventImage.setImageBitmap(photo);
                }
            } else {
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
        Event event = new Event();
        event.setTitle(etTitile.getText().toString());
        event.setGuestLimit(parseInt(String.valueOf(spGuest.getSelectedItem())));
        event.setCategory((String) sMenuCategory.getSelectedItem());
        event.setDescription(etDesc.getText().toString());
        dateObject = getDateObject();
        TimeObject = getTimeObject();
        event.setDate(dateObject);
        event.setTime(TimeObject);
        event.setAddress(etVenue.getText().toString());
        event.setPrice(parseInt(String.valueOf(etCharge.getText())));
        event.setGuestLimit(parseInt(String.valueOf(spGuest.getSelectedItem())));
        event.setAlcohol(spAlcohol.getSelectedItem().toString());
        bitmap = ((BitmapDrawable) ivEventImage.getDrawable()).getBitmap();
        byte[] text = BitMapToString(bitmap);
        ParseFile File = new ParseFile("EventImage.txt", text);
        event.setImageFile(File);
        event.setHost(ParseUser.getCurrentUser());
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)

                Toast.makeText(context, "Successfully created event on Parse", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Date getDateObject() {

        DateFormat formatter = new SimpleDateFormat("yyyy/mm/dd"); // Make sure user insert date into edittext in this format.
        Date dateObject = null;
        String dob_var=(tvDatePicker.getText().toString());
        try {
            dateObject = formatter.parse(dob_var);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return dateObject;
    }

    private Date getTimeObject() {

        DateFormat formatter = new SimpleDateFormat("hh:mm aa");
        Date dateObject = null;
        String dob_var=(tvTimePicker.getText().toString());

        try {
            dateObject = formatter.parse(dob_var);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return dateObject;
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

        list.add("List2");
        list.add("List2");
        list.add("List2");
        list.add("List2");
        list.add("List2");
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

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }



}
