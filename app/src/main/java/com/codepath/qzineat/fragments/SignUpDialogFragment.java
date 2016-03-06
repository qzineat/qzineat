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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shyam Rokde on 3/5/16.
 */
public class SignUpDialogFragment extends DialogFragment {

    @Bind(R.id.btnSingUp) Button btnSingUp;
    @Bind(R.id.etEmail) EditText etEmail;
    @Bind(R.id.etPassword) EditText etPassword;

    private OnSignUpListener listener;

    public SignUpDialogFragment(){
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container);

        ButterKnife.bind(this, view);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSingUp.setEnabled(false);

                try{
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
                                listener.onSignUp();
                                dismiss();
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                                Toast.makeText(getContext(), "Somthing went wrong!! Please try Again", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        }
                    });
                }catch (Exception ex){
                    ex.printStackTrace();
                }


            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSignUpListener){
            listener = (OnSignUpListener) context;
        }else {
            throw new ClassCastException(context + " must implement SignUpDialogFragment.OnSignUpListener");
        }
    }

    public interface OnSignUpListener {
        public void onSignUp();
    }
}
