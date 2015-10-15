package com.lab11.nolram.cadernocamera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lab11.nolram.Constants;

public class ThirdLicensesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if(!Constants.DEBUG) {
            GoogleAnalytics.getInstance(ThirdLicensesActivity.this).reportActivityStart(this);
        }
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if(!Constants.DEBUG) {
            GoogleAnalytics.getInstance(ThirdLicensesActivity.this).reportActivityStop(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_licenses);
        if(!Constants.DEBUG) {
        /*  Analytics */
            Tracker t = ((FlynNoteApp) getApplication()).getTracker(FlynNoteApp.TrackerName.APP_TRACKER);
            t.setScreenName(ThirdLicensesActivity.class.getName());
            t.send(new HitBuilders.AppViewBuilder().build());
        /* Fim Analytics */
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.list_view_licenses);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url;
                Intent i;
                switch (position) {
                    case 0:
                        url = "http://www.joda.org/joda-time/";
                        i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;

                    case 1:
                        url = "http://www.google.com/design/icons/";
                        i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;

                    case 2:
                        url = "https://github.com/hdodenhof/CircleImageView";
                        i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;

                    case 3:
                        url = "http://iconmonstr.com/";
                        i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;

                    default:
                        break;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_third_licenses, menu);
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
