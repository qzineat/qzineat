package com.codepath.qzineat;

import android.app.Application;
import android.util.Log;

import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.Attendee;
import com.codepath.qzineat.models.Event;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Shyam Rokde on 3/2/16.
 */
public class QZinEatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models here
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Attendee.class);

        // Fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("myappid") // should correspond to APP_ID env variable
                .clientKey("simplechat9876")
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("https://simplechat9876.herokuapp.com/parse/").build());


        // Facebook Initialize
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                if(AccessToken.getCurrentAccessToken() == null){
                    Log.d("DEBUG", "not logged in yet");
                } else {
                    Log.d("DEBUG", "Logged in");
                }
            }
        });
    }
}
