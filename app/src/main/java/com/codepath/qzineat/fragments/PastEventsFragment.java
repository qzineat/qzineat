package com.codepath.qzineat.fragments;

import android.os.Bundle;

import com.codepath.qzineat.QZinEatApplication;
import com.codepath.qzineat.utils.QZinDataAccess;

/**
 * Created by glondhe on 3/13/16.
 */
public class PastEventsFragment extends UserEventsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getEvents() {
        if(QZinEatApplication.isHostView){
            QZinDataAccess.findPastHostEvents(lastCreatedAt, this);
        }else {
            QZinDataAccess.findPastSubscribedEvents(lastCreatedAt, this);
        }
    }
}
