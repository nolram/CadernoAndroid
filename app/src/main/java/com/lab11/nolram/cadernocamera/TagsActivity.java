package com.lab11.nolram.cadernocamera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lab11.nolram.Constants;

public class TagsActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if(!Constants.DEBUG) {
            GoogleAnalytics.getInstance(TagsActivity.this).reportActivityStart(this);
        }
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if(!Constants.DEBUG) {
            GoogleAnalytics.getInstance(TagsActivity.this).reportActivityStop(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        if(!Constants.DEBUG) {
        /*  Analytics */
            Tracker t = ((FlynNoteApp) getApplication()).getTracker(FlynNoteApp.TrackerName.APP_TRACKER);
            t.setScreenName(TagsActivity.class.getName());
            t.send(new HitBuilders.AppViewBuilder().build());
        /* Fim Analytics */
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tags, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
