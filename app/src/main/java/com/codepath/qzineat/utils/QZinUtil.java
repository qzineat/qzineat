package com.codepath.qzineat.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Shyam Rokde on 3/12/16.
 */
public class QZinUtil {
    /**
     * Usage: 23 March @ 9:00pm
     * @return
     */
    public static String getShortDate(Date inputDate){
        String format = "d MMM @ h:mma"; // 23 Mar @ 9:00pm
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(inputDate).replace("AM", "am").replace("PM","pm");
    }
}
