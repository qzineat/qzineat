package com.codepath.qzineat.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/11/16.
 */
public class ProfileEditFragment  extends Fragment {

    @Bind(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @Bind(R.id.etProfileName)
    EditText etProfileName;
    @Bind(R.id.etLocation)
    EditText etLocation;
    @Bind(R.id.etSpeciality)
    EditText etSpeciality;
    @Bind(R.id.etContact)
    EditText etContact;
    @Bind(R.id.etEmail)
    EditText etEmail;
    @Bind(R.id.etWebsite)
    EditText etWebsite;
    @Bind(R.id.btSave)
    Button btSave;

    Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        ButterKnife.bind(this, view);

        setValues();

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewValues();
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.flContent, profileFragment);
                fragmentTransaction.commit();

            }
        });

        return view;
    }

    private void setNewValues() {

        bitmap = ((BitmapDrawable) ivProfileImage.getDrawable()).getBitmap();
        byte[] text = BitMapToString(bitmap);
        ParseFile File = new ParseFile("EventImage.txt", text);
        User.getLoggedInUser().setImageFile(File);
        User.getLoggedInUser().setProfileName(etProfileName.getText().toString());
        User.getLoggedInUser().setCity(etLocation.getText().toString());
        User.getLoggedInUser().setSpeciality(etSpeciality.getText().toString());
        User.getLoggedInUser().setPhone(etContact.getText().toString());
        User.getLoggedInUser().setEmail(etEmail.getText().toString());
        User.getLoggedInUser().setWebsite(etWebsite.getText().toString());
        User.getLoggedInUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    Log.d("DEBUG", "Successfully created event on Parse");
                  // Toast.makeText(getContext(), "Successfully created event on Parse", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setValues() {
        ParseFile pf = User.getLoggedInUser().getImageFile();

        if (pf != null) {
            Glide.with(this).load(pf.getUrl()).asBitmap().centerCrop().into(ivProfileImage);
        }
        if (User.getLoggedInUser().getProfileName() != null) {
            etProfileName.setText(User.getLoggedInUser().getProfileName());
        }
        if (User.getLoggedInUser().getCity() != null) {
            etLocation.setText(User.getLoggedInUser().getCity());
        }
        if (User.getLoggedInUser().getSpeciality() != null) {
            etSpeciality.setText(User.getLoggedInUser().getSpeciality());
        }
        if (User.getLoggedInUser().getPhone() != null) {
            etContact.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
            etContact.setText(User.getLoggedInUser().getPhone());
        }
        if (User.getLoggedInUser().getEmail() != null) {
            etEmail.setText(User.getLoggedInUser().getEmail());
        }
        if (User.getLoggedInUser().getWebsite() != null) {
            etWebsite.setText(User.getLoggedInUser().getWebsite());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public byte [] BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return b;
    }
}
