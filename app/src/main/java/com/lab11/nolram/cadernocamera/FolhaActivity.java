package com.lab11.nolram.cadernocamera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.FolhaDataSource;
import com.lab11.nolram.database.model.Folha;

import java.io.File;
import java.util.List;


public class FolhaActivity extends AppCompatActivity {

    public static final String INDICE = "indice_list_folha";
    public static final int UPDATE = 12;
    private PagerAdapter mPagerAdapter;
    private ViewPager mPager;
    private Toolbar toolbar;
    private FolhaDataSource folhaDataSource;

    //private String localImagem;
    private int indice;
    //private String data;
    //private String tags;
    //private String titulo;
    private String caderno_titulo;
    private int cor_principal;
    //private int id_cor_principal;
    private int cor_secundaria;
    //private int id_cor_secundaria;
    //private long id_folha;
    //private String badge;
    private long fk_caderno;

    private List<Folha> folhas;

    @Override
    public void onResume() {
        folhaDataSource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        folhaDataSource.close();
        super.onPause();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(FolhaActivity.this).reportActivityStart(this);
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(FolhaActivity.this).reportActivityStop(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folha);

        /*  Analytics */
        Tracker t = ((FlynNoteApp) getApplication()).getTracker(FlynNoteApp.TrackerName.APP_TRACKER);
        t.setScreenName(FolhaActivity.class.getName());
        t.send(new HitBuilders.AppViewBuilder().build());
        /* Fim Analytics */

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(SearchTagActivity.class.getName())) {
            indice = bundle.getInt(Database.FOLHA_CONTADOR) - 1;
        } else {
            indice = bundle.getInt(INDICE);
        }
        cor_principal = bundle.getInt(Database.CADERNO_COR_PRINCIPAL);
        cor_secundaria = bundle.getInt(Database.CADERNO_COR_SECUNDARIA);
        fk_caderno = bundle.getLong(Database.FOLHA_FK_CADERNO);
        caderno_titulo = bundle.getString(Database.CADERNO_TITULO);

        folhaDataSource = new FolhaDataSource(getApplicationContext());
        folhaDataSource.open();
        folhas = folhaDataSource.getAllFolhas(fk_caderno);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new DepthPageTransformer());
        mPager.setCurrentItem(indice);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Folha folha = folhas.get(position);
                setTitle(folha.getTitulo());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        Folha folha = folhas.get(mPager.getCurrentItem());
        setSupportActionBar(toolbar);
        setTitle(folha.getTitulo());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(cor_principal);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Integer.valueOf(cor_secundaria));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_folha, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_folha) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.alert_attention)
                    .setMessage(R.string.alert_delete_paper_warning)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Folha folha = folhas.get(mPager.getCurrentItem());
                            File img = new File(folha.getLocal_folha());
                            if (img.exists()) {
                                img.delete();
                            }
                            folhaDataSource.deleteFolha(folha);
                            Toast.makeText(getApplicationContext(), getResources().getString(
                                    R.string.alert_folha_deletada), Toast.LENGTH_LONG).show();
                            finish();
                        }

                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
            return true;
        } else if (id == R.id.action_edit_folha) {
            Intent intentUpdate = new Intent(getApplicationContext(),
                    EditarFolhaActivity.class);
            Bundle bundle = new Bundle();
            Folha folha = folhas.get(mPager.getCurrentItem());
            bundle.putLong(Database.FOLHA_FK_CADERNO, fk_caderno);
            bundle.putString(Database.CADERNO_TITULO, caderno_titulo);
            bundle.putLong(Database.FOLHA_ID, folha.getId());
            bundle.putString(Database.FOLHA_TITULO, folha.getTitulo());
            bundle.putString(Database.FOLHA_DATA, folha.getDataBanco());
            bundle.putString(Database.FOLHA_LOCAL_IMAGEM, folha.getLocal_folha());
            bundle.putString(Database.TAG_TAG, folha.getTags().toString());
            bundle.putInt(Database.CADERNO_COR_PRINCIPAL, cor_principal);
            bundle.putInt(Database.CADERNO_COR_SECUNDARIA, cor_secundaria);
            intentUpdate.putExtras(bundle);
            startActivityForResult(intentUpdate, UPDATE);
            return true;
        } else if (id == R.id.action_share_folha) {
            Intent shareIntent = new Intent();
            Folha folha = folhas.get(mPager.getCurrentItem());
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new
                    File(folha.getLocal_folha())));
            shareIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPDATE:
                if (resultCode == RESULT_OK) {
                    String titulo = data.getStringExtra(Database.FOLHA_TITULO);
                    setTitle(titulo);
                }
                break;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            FolhaActivityFragment folhaActivityFragment = new FolhaActivityFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Database.FOLHA_LOCAL_IMAGEM, folhas.get(position).getLocal_folha());
            folhaActivityFragment.setArguments(bundle);
            return folhaActivityFragment;
        }

        @Override
        public int getCount() {
            return folhas.size();
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}
