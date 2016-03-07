package com.codepath.qzineat.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.utils.UserUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by glondhe on 3/1/16.
 */
public class LoginFragment extends Fragment {

    @Bind(R.id.login_button) LoginButton loginButton;

    CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();
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
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken.setCurrentAccessToken(loginResult.getAccessToken());
                Log.d("DEBUG_Activity",getActivity().toString());

                try {
                    // Go back to called fragment..
                    if(getTargetFragment() != null){
                        Intent intent = new Intent();
                        intent.putExtra("result", UserUtil.USER_LOG_IN_SUCCESS);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        getFragmentManager().popBackStack();
                    }else {
                        Intent intent = new Intent(getContext(), getActivity().getClass());
                        intent.putExtra("result", UserUtil.USER_LOG_IN_SUCCESS);
                        startActivity(intent);
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "User Canceled", Toast.LENGTH_SHORT).show();

                try {

                    if(getTargetFragment() != null){
                        Intent intent = new Intent();
                        intent.putExtra("result", UserUtil.USER_LOG_IN_CANCEL);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, intent);
                        getFragmentManager().popBackStack();
                    }else {
                        Intent intent = new Intent(getContext(), getActivity().getClass());
                        intent.putExtra("result", UserUtil.USER_LOG_IN_CANCEL);
                        startActivity(intent);
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            @Override
            public void onError(FacebookException exception) {
                // TODO: Change this later
                Toast.makeText(getContext(), "Error here", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}