package com.codepath.qzineat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.android.qzineat.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/5/16.
 */
public class SignUpDialogFragment extends DialogFragment {

    @Bind(R.id.btnLogin) Button btnLogin;
    @Bind(R.id.btnSingUp) Button btnSingUp;
    @Bind(R.id.etEmail) EditText etEmail;
    @Bind(R.id.etPassword) EditText etPassword;
    @Bind(R.id.login_button) LoginButton loginButton;

    private OnLoginSignUpListener listener;
    CallbackManager callbackManager;

    public SignUpDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container);

        ButterKnife.bind(this, view);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                listener.onLoginSignUp();
                dismiss();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();




                // TODO: This is problem - always return exception
              /*  ParseUser.logInInBackground(username, password, new LogInCallback() {

                    @Override
                    public void done(ParseUser user, ParseException e) {

                        if(e!=null){
                            e.printStackTrace();
                            Toast.makeText(getContext(), getString(R.string.somthing_went_wrong), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(), "Username/Password Incorrect", Toast.LENGTH_SHORT).show();
                            Log.d("DEBUG", ParseUser.getCurrentUser().getObjectId());
                            Log.d("DEBUG", ParseUser.getCurrentUser().getUsername());
                            // cool...
                            // Hooray! Sing them for event now.
                            //listener.onLoginSignUp();
                            //dismiss();
                        }
                        if (e == null && user != null) {

                        } else if (user == null) {

                        } else {

                        }

                    }
                });*/
            }
        });

        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSingUp.setEnabled(false);

                try {
                    // Lets do the processing
                    ParseUser user = ParseUser.getCurrentUser();
                    user.setUsername(etEmail.getText().toString());
                    user.setPassword(etPassword.getText().toString());
                    user.setEmail(etEmail.getText().toString());

                    // other fields can be set just like with ParseObject
                    //user.setPhone("650-253-0000");
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Sing them for event now.
                                listener.onLoginSignUp();
                                dismiss();
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginSignUpListener) {
            listener = (OnLoginSignUpListener) context;
        } else {
            throw new ClassCastException(context + " must implement SignUpDialogFragment.OnSignUpListener");
        }
    }

    public interface OnLoginSignUpListener {
        public void onLoginSignUp();
    }
}
