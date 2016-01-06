package com.lab11.nolram.cadernocamera;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lab11.nolram.Constants;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.CadernoDataSource;


public class MainActivity extends AppCompatActivity{

    public static final int SDK = android.os.Build.VERSION.SDK_INT;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private int option_sort = 0;

    private MainActivityFragment fragment;

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if(!Constants.DEBUG) {
            GoogleAnalytics.getInstance(MainActivity.this).reportActivityStart(this);
        }
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if(!Constants.DEBUG) {
            GoogleAnalytics.getInstance(MainActivity.this).reportActivityStop(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        option_sort = 0;

        if(!Constants.DEBUG) {
        /*  Analytics */
            Tracker t = ((FlynNoteApp) getApplication()).getTracker(FlynNoteApp.TrackerName.APP_TRACKER);
            t.setScreenName(MainActivity.class.getName());
            t.send(new HitBuilders.AppViewBuilder().build());
        /* Fim Analytics */
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.txt_meus_cadernos));
        navigationView = (NavigationView) findViewById(R.id.navigation_main);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.menu_tags:
                        intent = new Intent(MainActivity.this, TagsActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_contato:
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.my_email)});
                        i.putExtra(Intent.EXTRA_SUBJECT, "[" + getString(R.string.app_name) + "]");
                        try {
                            startActivity(Intent.createChooser(i, getString(R.string.title_msg_enviar_email)));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MainActivity.this,
                                    R.string.txt_msg_email,
                                    Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.menu_about:
                        intent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu_arquivo:
                        intent = new Intent(MainActivity.this, ArquivadosActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), R.string.msg_erro_menu, Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        if (SDK >= Build.VERSION_CODES.HONEYCOMB){
            getMenuInflater().inflate(R.menu.menu_main, menu);
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search_main).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_sort_notebooks){
            createPopUp();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createPopUp(){
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setTitle(getString(R.string.title_dialog_sort_notebooks));

        final CharSequence[] sequences = {getString(R.string.txt_r_last_modification),
                getString(R.string.txt_l_last_modification),
                getString(R.string.txt_data_criacao_r),
                getString(R.string.txt_data_criacao_l),
                getString(R.string.txt_ordem_alfabetica)};

        builderSingle.setSingleChoiceItems(sequences, option_sort,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        int op;
                        option_sort = which;
                        switch (which){
                            case 0:
                                op = CadernoDataSource.RECENTES_MODIFICADOS;
                                break;
                            case 1:
                                op = CadernoDataSource.ULTIMOS_MODIFICADOS;
                                break;
                            case 2:
                                op = CadernoDataSource.RECENTES_CRIADOS;
                                break;
                            case 3:
                                op = CadernoDataSource.ULTIMOS_CRIADOS;
                                break;
                            case 4:
                                op = CadernoDataSource.ORDEM_ALFABETICA;
                                break;
                            default:
                                op = 6;
                                break;
                        }
                        fragment.updateList(op);
                        dialog.dismiss();
                    }

                }
        );
        builderSingle.show();
    }
}
