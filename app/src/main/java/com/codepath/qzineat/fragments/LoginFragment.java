package com.codepath.qzineat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.activities.HomeActivity;
import com.codepath.qzineat.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/19/16.
 */
public class LoginFragment extends Fragment {

    @Bind(R.id.facebook_login) Button facebookLoginButton;
    @Bind(R.id.parse_login_button) Button loginButton;
    @Bind(R.id.parse_signup_button) Button signupButton;
    @Bind(R.id.login_username_input) EditText etUsername;
    @Bind(R.id.login_password_input) EditText etPassword;



    List<String> permissions = new ArrayList<>();
    public ParseInstallation installation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        ButterKnife.bind(this, view);

        setupLogin();

        return view;
    }

    private void setupLogin(){

        signupButton.setOnClickListener(mSignUpButtonListener);

        loginButton.setOnClickListener(mLoginButtonListener);

        facebookLoginButton.setOnClickListener(mFacebookButtonListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

    }

    private boolean isValid(){
        if(etUsername.getText().toString().trim().isEmpty()){
            Toast.makeText(getContext(), getString(R.string.login_no_username_toast), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etPassword.getText().toString().trim().isEmpty()){
            Toast.makeText(getContext(), getString(R.string.login_no_password_toast), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    View.OnClickListener mLoginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(!isValid()){
                return;
            }

            ParseUser.logInInBackground(etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        // Hooray! The user is logged in.
                        Intent intent = new Intent(getContext(), HomeActivity.class);
                        intent.putExtra("result", User.USER_LOG_IN_SUCCESS);
                        startActivity(intent);
                    } else {
                        if (e != null) {
                            if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                                Toast.makeText(getContext(), getString(R.string.login_invalid_credentials_toast), Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getContext(), getString(R.string.login_failed_unknown_toast), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    };


    View.OnClickListener mFacebookButtonListener = new View.OnClickListener() {
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
                            Intent intent = new Intent(getContext(), HomeActivity.class);
                            intent.putExtra("result", User.USER_LOG_IN_SUCCESS);
                            startActivity(intent);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
    };


    View.OnClickListener mSignUpButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Redirect to SignUp Fragment
            FragmentManager fragmentManager = getFragmentManager();
            SignUpFragment fragment = new SignUpFragment();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    };



}
