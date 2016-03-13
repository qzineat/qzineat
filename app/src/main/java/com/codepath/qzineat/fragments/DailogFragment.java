package com.codepath.qzineat.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.android.qzineat.R;

import java.io.ByteArrayOutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/12/16.
 */
public class DailogFragment extends DialogFragment {

    @Bind(R.id.btGallary)
    Button btGallary;
    @Bind(R.id.btCamera)
    Button btCamera;

    private static final int RESULT_OK = -1 ;
    private View view;
    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMAGE = 1;
    private String imgDecodableString;
    private Bitmap photo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dialog, container, false);
        ButterKnife.bind(this, view);

        btGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // See if it comes from Login

        try {

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
//                ivEventImage.setImageBitmap(BitmapFactory
//                        .decodeFile(imgDecodableString));
                returnToHostfragment();

            } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                {
                     photo = (Bitmap) data.getExtras().get("data");
//                    ivEventImage.setImageBitmap(photo);
                    returnToHostfragment();
                }
            } else {
                Toast.makeText(getContext(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
                returnToHostfragment();
            }
        } catch (Exception e) {
            Log.d("DEBUG_class", e.toString());
            Log.d("DEBUG_class", getClass().toString());
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void returnToHostfragment() {

        Bundle bundle = new Bundle();

        if(imgDecodableString != null) {
            bundle.putString("imgDecodableString", imgDecodableString);
        }
        if(photo != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            bundle.putByteArray("bitMapPhoto", byteArray);
        }
        //HostFragment newFragment = new HostFragment();
        //newFragment.setArguments(bundle);

        Intent mIntent = new Intent();
        mIntent.putExtras(bundle);

        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, mIntent);
//        dismiss();
//        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.commit();
        dismiss();
    }

}
