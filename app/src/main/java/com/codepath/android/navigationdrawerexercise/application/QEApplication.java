package com.codepath.android.navigationdrawerexercise.application;


import android.app.Application;

import com.codepath.android.navigationdrawerexercise.model.Message;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

/**
 * Created by glondhe on 2/18/16.
 */
public class QEApplication  extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Message.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("myappid") // should correspond to APP_ID env variable
                .clientKey("simplechat9876")
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("https://simplechat9876.herokuapp.com/parse/").build());
    }
}