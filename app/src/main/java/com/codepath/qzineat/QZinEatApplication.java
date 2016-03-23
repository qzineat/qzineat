package com.codepath.qzineat;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.android.qzineat.R;
import com.codepath.qzineat.models.Attendee;
import com.codepath.qzineat.models.Event;
import com.codepath.qzineat.models.Review;
import com.codepath.qzineat.models.User;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.interceptors.ParseLogInterceptor;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Shyam Rokde on 3/2/16.
 */
public class QZinEatApplication extends Application {

    public static boolean isHostView;

    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models here
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Attendee.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Review.class);

        // Fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NotoSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        /*Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("myappid") // should correspond to APP_ID env variable
                .clientKey("simplechat9876")
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("https://simplechat9876.herokuapp.com/parse/")
                .build());*/

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("xImk5rYwJjO0a5n0XqCmeFth4xGtCpbXdetct6A2") // should correspond to APP_ID env variable
                .clientKey("vCeZuWyXpN9ifZGTszcy8EqtAX9Tw9OyogSRhQ27")
                .addNetworkInterceptor(new ParseLogInterceptor()).build());
        ParseInstallation.getCurrentInstallation().saveInBackground();

        // Facebook Initialize
        ParseFacebookUtils.initialize(getApplicationContext());



        // Drawer Image Loading
        //drawerImageLoader();

        //loadEventwithReview();
    }

    private void loadEventwithReview(){
        String eventObjectId = "l63bDS6eBy";
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.getInBackground(eventObjectId, new GetCallback<Event>() {

            @Override
            public void done(Event event, ParseException e) {
                if (e == null) {
                    String comment = "This was really good event. I enjoyed!! Love to go for your food in future!! Please host another event soon";
                    // Save Review
                    Review review = new Review();
                    review.setComment(comment);
                    review.setEvent(event);
                    review.setReviewedBy(User.getLoggedInUser());
                    review.setRating(4);
                    review.saveInBackground();

                    event.increment("numberOfReviews");
                    event.increment("ratingSum", 3);
                    event.saveInBackground();
                }
            }
        });
    }


    private void drawerImageLoader(){
        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }
                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()
                return super.placeholder(ctx, tag);
            }
        });
    }
}
