package com.codepath.qzineat.utils;

import android.app.Activity;
import android.content.Intent;

import com.codepath.qzineat.QZinEatApplication;
import com.codepath.qzineat.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Shyam Rokde on 3/12/16.
 */
public class QZinUtil {
    /**
     * Usage: 23 March @ 9:00pm
     *
     * @return
     */
    public static String getShortDate(Date inputDate) {
        String format = "d MMM @ h:mma"; // 23 Mar @ 9:00pm
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(inputDate).replace("AM", "am").replace("PM", "pm");
    }

    private static ArrayList<String> qzinImages = new ArrayList<>(
            Arrays.asList(
                    "http://i.imgur.com/3VgvbDo.png",
                    "http://i.imgur.com/umtpLWn.jpg",
                    "http://i.imgur.com/3VgvbDo.png",
                    "http://i.imgur.com/umtpLWn.jpg"
            )
    );

    /**
     * Get Random Image URL
     *
     * @return
     */
    public static String getQZinImageUrl() {
        Random rand = new Random();
        int randomNum = rand.nextInt((3 - 1) + 1);

        return qzinImages.get(randomNum);
    }

    public static void changeTheme(Activity activity) {
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // Set Application variables
        QZinEatApplication.isHostView = !QZinEatApplication.isHostView;
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        if (QZinEatApplication.isHostView) {
            activity.setTheme(R.style.HostQZinTheme);
        } else {
            activity.setTheme(R.style.AppTheme);
        }
    }


}
