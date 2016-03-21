package com.codepath.qzineat.interfaces;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

/**
 * Created by Shyam Rokde on 3/20/16.
 */
public interface CommunicationChannel {
    void attachDrawer(Toolbar toolbar);
    void attachDrawer(Toolbar toolbar, boolean colorStatusBar);
    void openFragment(Fragment fragment);
}
