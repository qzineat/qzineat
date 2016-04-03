package com.codepath.qzineat.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.codepath.qzineat.R;
import com.codepath.qzineat.fragments.LoginFragment;

import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Open Login Fragment

        FragmentManager fragmentManager = getSupportFragmentManager();
        LoginFragment fragment = new LoginFragment();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }


}
