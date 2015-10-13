package com.lab11.nolram.cadernocamera;

/**
 * Created by nolram on 13/10/15.
 */
import java.util.HashMap;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class FlynNoteApp extends Application {

    private static final String PROPERTY_ID = "UA-68774918-1";

    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, GLOBAL_TRACKER,
    }

    public HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    public FlynNoteApp() {
        super();
    }

    public synchronized Tracker getTracker(TrackerName appTracker) {
        if (!mTrackers.containsKey(appTracker)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (appTracker == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID) : analytics.newTracker(R.xml.global_tracker);
            t.enableAutoActivityTracking(true);
            t.enableExceptionReporting(true);
            mTrackers.put(appTracker, t);
        }
        return mTrackers.get(appTracker);
    }
}