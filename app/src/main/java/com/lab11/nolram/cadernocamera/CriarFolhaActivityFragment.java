package com.lab11.nolram.cadernocamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.lab11.nolram.components.BitmapHelper;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.FolhaDataSource;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


/**
 * A placeholder fragment containing a simple view.
 */
public class CriarFolhaActivityFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;


    private long fk_caderno;

    private EditText edtTitulo;
    private EditText edtTags;
    private ImageButton btnImagem;
    private Button btnAddFolha;

    private FolhaDataSource folhaDataSource;

    String mCurrentPhotoPath = "";

    public CriarFolhaActivityFragment() {
    }

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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getString(R.string.app_name));

        if(!mCurrentPhotoPath.isEmpty()){
            File mExistente = new File(mCurrentPhotoPath);
            if(mExistente.exists()){
                boolean temp = mExistente.delete();
                if(temp){
                    Log.d("img_deletado", "Imagem deletada");
                }
            }
        }

        if(!storageDir.isDirectory()){
            storageDir.mkdirs();
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            File imgFile = new  File(mCurrentPhotoPath);
            Log.d("local", mCurrentPhotoPath);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapHelper.decodeSampledBitmapFromLocal(imgFile.getAbsolutePath(), 100, 200);
                btnImagem.setImageBitmap(myBitmap);

            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("criar", "Não foi possível criar o arquivo");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_criar_folha, container, false);
        edtTitulo = (EditText) view.findViewById(R.id.edtxt_titulo_folha);
        edtTags = (EditText) view.findViewById(R.id.edtxt_tags);
        btnImagem = (ImageButton) view.findViewById(R.id.btn_add_imagem);
        btnAddFolha = (Button) view.findViewById(R.id.btn_adicionar_folha);

        folhaDataSource = new FolhaDataSource(view.getContext());
        folhaDataSource.open();

        Bundle bundle = getActivity().getIntent().getExtras();
        fk_caderno = bundle.getLong(Database.FOLHA_FK_CADERNO);

        btnImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnAddFolha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = edtTitulo.getText().toString();
                String tags = edtTags.getText().toString();
                String[] res_tags = null;
                if(!tags.isEmpty()){
                    res_tags = tags.split("[#.;,]");
                }
                //Toast.makeText(v.getContext(), Arrays.toString(res_tags), Toast.LENGTH_SHORT).show();
                if((!mCurrentPhotoPath.isEmpty()) || (!titulo.isEmpty())) {
                    folhaDataSource.criarFolha(mCurrentPhotoPath, fk_caderno, titulo, res_tags);
                    getActivity().finish();
                }else {
                    Toast.makeText(v.getContext(), "O Titulo não pode estar em branco",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
