package com.codepath.qzineat.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.parse.ParseGeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Shyam Rokde on 3/10/16.
 */
public class GeoUtil {

    public static Address getGeoAddress(Context context, String address){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            ArrayList<Address> addressList = (ArrayList<Address>) geocoder.getFromLocationName(address, 1);

            for(Address add : addressList){
                Log.d("DEBUG", add.toString());
            }

            if(addressList.size() > 0){
                return addressList.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ParseGeoPoint getLocation(Address address){
        // latitude of 40.0 degrees and -30.0 degrees longitude
        // ParseGeoPoint point = new ParseGeoPoint(40.0, -30.0);

        ParseGeoPoint point = new ParseGeoPoint(address.getLatitude(), address.getLongitude());

        return point;
    }

    public static String getLocality(Address address){
        if(address != null && address.getLocality() != null){
            return address.getLocality();
        }
        return "";
    }
}
