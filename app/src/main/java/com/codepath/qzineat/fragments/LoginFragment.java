package com.codepath.qzineat.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.User;
import com.parse.CountCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/1/16.
 */
public class LoginFragment extends Fragment {

    @Bind(R.id.login_button) Button loginButton;

    List<String> permissions = new ArrayList<>();
    public ParseInstallation installation;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(ParseUser.getCurrentUser()!=null){
            Log.d("DEBUG", "Your are Username - " + ParseUser.getCurrentUser().getUsername());
            Log.d("DEBUG", "Your are ObjectId - " + ParseUser.getCurrentUser().getObjectId());

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        setupLogin();

        return view;
    }

    private void setupLogin(){

        permissions.add("user_friends");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginFragment.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else {
                            if (user.isNew()) {
                                Log.d("MyApp", "User signed up and logged in through Facebook!");

                            } else {
                                Log.d("MyApp", "User logged in through Facebook!");
                                installation = ParseInstallation.getCurrentInstallation();
                                installation.put("username", User.getLoggedInUser().getUsername());
                                installation.saveInBackground();
                                //checkIfAlreadyInstalled();
                            }

                            try {
                                // Go back to called fragment..
                                if (getTargetFragment() != null) {
                                    Intent intent = new Intent();
                                    intent.putExtra("result", User.USER_LOG_IN_SUCCESS);
                                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                                    getFragmentManager().popBackStack();
                                } else {
                                    Intent intent = new Intent(getContext(), getActivity().getClass());
                                    intent.putExtra("result", User.USER_LOG_IN_SUCCESS);
                                    startActivity(intent);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    private void checkIfAlreadyInstalled() {
        // Check User Already reviewed or not
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("username", User.getLoggedInUser());
        pushQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (count == 0) {
                    // I haven't reviewed yet
                    installation = ParseInstallation.getCurrentInstallation();
                    installation.put("username", User.getLoggedInUser().getUsername());
                    installation.saveInBackground();
                }
                else Log.d("DEBUG_Count", String.valueOf(count));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

    }
}