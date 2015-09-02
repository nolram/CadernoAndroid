package com.lab11.nolram.cadernocamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.lab11.nolram.components.BitmapHelper;
import com.lab11.nolram.components.TouchImageView;
import com.lab11.nolram.database.Database;

import java.io.File;


/**
 * A placeholder fragment containing a simple view.
 */
public class FolhaActivityFragment extends Fragment {

    private TouchImageView imgFoto;
    private Toolbar toolbar;

    private String localImagem;
    private String data;
    private String tags;
    private String titulo;
    private String cor_principal;
    private String cor_secundaria;

    public FolhaActivityFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle(titulo);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(Integer.valueOf(cor_principal));

        //toolbar.setTitle(titulo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Integer.valueOf(cor_secundaria));
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
        cor_principal = bundle.getString(Database.CADERNO_COR_PRINCIPAL);
        cor_secundaria = bundle.getString(Database.CADERNO_COR_SECUNDARIA);

        imgFoto = (TouchImageView) view.findViewById(R.id.img_foto);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        WindowManager wm = (WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        int width;
        int height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            width = size.x;
            height = size.y;
        }else {
            width = display.getWidth();  // deprecated
            height = display.getHeight();  // deprecated
        }

        File imgFile = new  File(localImagem);
        //Log.d("local", mCurrentPhotoPath);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapHelper.decodeSampledBitmapFromLocal(imgFile.getAbsolutePath(), width, height);
            imgFoto.setImageBitmap(myBitmap);
        }

        return view;
    }
}
