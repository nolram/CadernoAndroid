package com.lab11.nolram.cadernocamera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lab11.nolram.components.BitmapHelper;
import com.lab11.nolram.components.TouchImageView;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.FolhaDataSource;
import com.lab11.nolram.database.model.Folha;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.File;


/**
 * A placeholder fragment containing a simple view.
 */
public class FolhaActivityFragment extends Fragment {

    public static final int UPDATE = 12;

    private FolhaDataSource folhaDataSource;

    private TouchImageView imgFoto;
    private Toolbar toolbar;
    private Drawer menu;

    private String localImagem;
    private String data;
    private String tags;
    private String titulo;
    private String caderno_titulo;
    private int cor_principal;
    private int id_cor_principal;
    private int cor_secundaria;
    private int id_cor_secundaria;
    private long id_folha;
    private String badge;
    private long fk_caderno;

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

    public FolhaActivityFragment() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_delete_folha){
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert_attention)
                        .setMessage(R.string.alert_delete_paper_warning)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Folha folha = folhaDataSource.getFolha(id_folha);
                                File img = new File(folha.getLocal_folha());
                                if (img.exists()) {
                                    img.delete();
                                }
                                folhaDataSource.deleteFolha(folha);
                                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(
                                        R.string.alert_folha_deletada), Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
        }else if(id == R.id.action_edit_folha){
            Intent intentUpdate = new Intent(getActivity().getApplicationContext(),
                    EditarFolhaActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong(Database.FOLHA_FK_CADERNO, fk_caderno);
            bundle.putString(Database.CADERNO_TITULO, caderno_titulo);
            bundle.putLong(Database.FOLHA_ID, id_folha);
            bundle.putString(Database.FOLHA_TITULO, titulo);
            bundle.putString(Database.FOLHA_LOCAL_IMAGEM, localImagem);
            bundle.putString(Database.TAG_TAG, tags);
            bundle.putInt(Database.CADERNO_COR_PRINCIPAL, cor_principal);
            bundle.putInt(Database.CADERNO_COR_SECUNDARIA, cor_secundaria);
            intentUpdate.putExtras(bundle);
            startActivityForResult(intentUpdate, UPDATE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        IProfile profile;
        int id_badge = getResources().getIdentifier(badge, "drawable",
                getActivity().getPackageName());

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle(titulo);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(cor_principal);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profile = new ProfileDrawerItem().withName(caderno_titulo).withIcon(
                    getResources().getDrawable(id_badge, getActivity().getTheme()));
        }else{
            profile = new ProfileDrawerItem().withName(caderno_titulo).withIcon(
                    getResources().getDrawable(id_badge));
        }
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(getActivity())
                .addProfiles(profile)
                .withHeaderBackground(id_cor_principal)
                .withSelectionListEnabledForSingleProfile(false)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        menu = new DrawerBuilder(getActivity())
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withActivity(getActivity())
                .addDrawerItems(
                        new SectionDrawerItem().withName(R.string.txt_info),
                        new PrimaryDrawerItem().withName(titulo).withIcon(FontAwesome.Icon.faw_font),
                        new PrimaryDrawerItem().withName(data).withIcon(FontAwesome.Icon.faw_calendar),
                        new PrimaryDrawerItem().withName(tags).withIcon(FontAwesome.Icon.faw_tags)
                )
                .withAccountHeader(headerResult)
                .withSavedInstance(savedInstanceState)
                .build();

        //toolbar.setTitle(titulo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Integer.valueOf(cor_secundaria));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPDATE:
                if(resultCode == getActivity().RESULT_OK) {
                    menu.removeAllItems();
                    tags = data.getStringExtra(Database.TAG_TAG);
                    titulo = data.getStringExtra(Database.FOLHA_TITULO);
                    localImagem = data.getStringExtra(Database.FOLHA_LOCAL_IMAGEM);

                    File imgFile = new File(localImagem);
                    //Log.d("local", mCurrentPhotoPath);
                    if(imgFile.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        float scalingFactor = this.getBitmapScalingFactor(myBitmap);
                        Bitmap newBitmap = BitmapHelper.ScaleBitmap(myBitmap, scalingFactor);
                        imgFoto.setImageBitmap(newBitmap);
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),
                                getString(R.string.txt_mensage_remove_image), Toast.LENGTH_LONG).show();
                        imgFoto.setImageResource(R.drawable.picture_remove);
                    }

                    menu.addItems(
                            new SectionDrawerItem().withName(R.string.txt_info),
                            new PrimaryDrawerItem().withName(titulo).withIcon(FontAwesome.Icon.faw_font),
                            new PrimaryDrawerItem().withName(this.data).withIcon(FontAwesome.Icon.faw_calendar),
                            new PrimaryDrawerItem().withName(tags).withIcon(FontAwesome.Icon.faw_tags)
                    );

                    getActivity().setTitle(titulo);
                    toolbar.setBackgroundColor(cor_principal);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getActivity().getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(cor_secundaria);
                    }
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_folha, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        localImagem = bundle.getString(Database.FOLHA_LOCAL_IMAGEM);
        data = bundle.getString(Database.FOLHA_DATA);
        tags = bundle.getString(Database.TAG_TAG);
        titulo = bundle.getString(Database.FOLHA_TITULO);
        cor_principal = bundle.getInt(Database.CADERNO_COR_PRINCIPAL);
        id_cor_principal = bundle.getInt(Database.CADERNO_ID_COR_PRINCIPAL);
        cor_secundaria = bundle.getInt(Database.CADERNO_COR_SECUNDARIA);
        id_cor_secundaria = bundle.getInt(Database.CADERNO_ID_COR_SECUNDARIA);
        badge = bundle.getString(Database.CADERNO_BADGE);
        fk_caderno = bundle.getLong(Database.FOLHA_FK_CADERNO);
        caderno_titulo = bundle.getString(Database.CADERNO_TITULO);
        id_folha = bundle.getLong(Database.FOLHA_ID);

        folhaDataSource = new FolhaDataSource(view.getContext());
        folhaDataSource.open();

        setHasOptionsMenu(true);

        imgFoto = (TouchImageView) view.findViewById(R.id.img_foto);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        File imgFile = new File(localImagem);
        //Log.d("local", mCurrentPhotoPath);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            float scalingFactor = this.getBitmapScalingFactor(myBitmap);
            Bitmap newBitmap = BitmapHelper.ScaleBitmap(myBitmap, scalingFactor);
            imgFoto.setImageBitmap(newBitmap);
        }else{
            Toast.makeText(getActivity().getApplicationContext(),
                    getString(R.string.txt_mensage_remove_image), Toast.LENGTH_LONG).show();
            imgFoto.setImageResource(R.drawable.picture_remove);
        }

        return view;
    }

    private float getBitmapScalingFactor(Bitmap bm) {
        // Get display width from device
        WindowManager wm = (WindowManager) getActivity().getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        int displayWidth;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            displayWidth = size.x;
        }else {
            displayWidth = display.getWidth();  // deprecated
        }
        // Get margin to use it for calculating to max width of the ImageView
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) this.imgFoto.getLayoutParams();
        int leftMargin = layoutParams.leftMargin;
        int rightMargin = layoutParams.rightMargin;

        // Calculate the max width of the imageView
        int imageViewWidth = displayWidth - (leftMargin + rightMargin);

        // Calculate scaling factor and return it
        return ( (float) imageViewWidth / (float) bm.getWidth() );
    }
}
