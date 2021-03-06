package com.codepath.qzineat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.qzineat.R;
import com.codepath.qzineat.activities.HomeActivity;
import com.codepath.qzineat.models.User;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/19/16.
 */
public class SignUpFragment extends Fragment {

    @Bind(R.id.create_account)
    Button createAccountButton;
    @Bind(R.id.signup_email_input)
    EditText etUsername;
    @Bind(R.id.signup_password_input)
    EditText etPassword;
    @Bind(R.id.signup_confirm_password_input)
    EditText etConfirmPassword;
    @Bind(R.id.signup_name_input)
    EditText etName;

    public ParseInstallation installation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_signup_fragment, container, false);
        ButterKnife.bind(this, view);

        setupSignUp();

        return view;
    }

    private void setupSignUp() {
        createAccountButton.setOnClickListener(mCreateAccountListener);
    }

    private View.OnClickListener mCreateAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Validate fields
            if (isValid()) {
                User user = new User();
                user.setUsername(etUsername.getText().toString().trim());
                user.setPassword(etPassword.getText().toString().trim());
                user.setEmail(etUsername.getText().toString().trim());
                user.setProfileName(etName.getText().toString().trim());

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            installation = ParseInstallation.getCurrentInstallation();
                            installation.put("username", User.getLoggedInUser().getUsername());
                            installation.saveInBackground();

                            // Hooray! Let them use the app now.
                            Intent intent = new Intent(getContext(), HomeActivity.class);
                            //intent.putExtra("result", User.U);
                            startActivity(intent);
                        } else {
                            switch (e.getCode()) {
                                case ParseException.INVALID_EMAIL_ADDRESS:
                                    Toast.makeText(getContext(), getString(R.string.signup_invalid_email_toast), Toast.LENGTH_SHORT).show();
                                    break;
                                case ParseException.USERNAME_TAKEN:
                                case ParseException.EMAIL_TAKEN:
                                    Toast.makeText(getContext(), getString(R.string.signup_email_taken_toast), Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(getContext(), getString(R.string.signup_signup_failed_unknown_toast), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }
    };

    private boolean isValid() {

        if (etUsername.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.signup_no_email_toast), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.login_no_password_toast), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!etPassword.getText().toString().trim().equals(etConfirmPassword.getText().toString().trim())) {
            Toast.makeText(getContext(), getString(R.string.signup_mismatch_confirm_password_toast), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.signup_no_name_toast), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
