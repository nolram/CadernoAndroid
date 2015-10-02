package com.lab11.nolram.cadernocamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lab11.nolram.components.DeviceDimensionsHelper;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.FolhaDataSource;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditarFolhaActivityFragment extends Fragment implements OnClickListener{


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RESULT_LOAD_IMAGE = 2;

    private long fk_caderno;
    private long id_folha;
    private String nomeCaderno;
    private String[] velhas_tags;

    private int cor_principal;
    private int cor_secundaria;

    private EditText edtTitulo;
    private EditText edtTags;
    private ImageView imgThumb;
    private Button btnAddFolha;
    private Button btnGetCamera;
    //private Button btnGetGallery;
    private Toolbar toolbar;

    private FolhaDataSource folhaDataSource;

    String mCurrentPhotoPath = "";

    public EditarFolhaActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(cor_principal);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Integer.valueOf(cor_secundaria));
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode == getActivity().RESULT_OK){
                    File imgFile = new  File(mCurrentPhotoPath);
                    int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getActivity());
                    if(imgFile.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
                                myBitmap, screenWidth, 500);
                        imgThumb.setImageBitmap(ThumbImage);
                    }
                    //Bitmap photo = (Bitmap) data.getExtras().get("data");
                    //saveBitmap(photo);
                }else if (resultCode == getActivity().RESULT_CANCELED){
                    File imgFile = new File(mCurrentPhotoPath);
                    //Log.d("local", mCurrentPhotoPath);
                    if(imgFile.exists()){
                        imgFile.delete();
                        mCurrentPhotoPath = "";
                        imgThumb.setImageDrawable(null);
                    }
                }
                break;
            case RESULT_LOAD_IMAGE:
                if(resultCode == getActivity().RESULT_OK && null != data){
                    Uri selectedImage = data.getData();
                    try {
                        int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getActivity());
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
                                bitmap, screenWidth, 500);
                        imgThumb.setImageBitmap(ThumbImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCurrentPhotoPath = getStringFromUri(selectedImage);
                    //File imgFile = new  File(mCurrentPhotoPath);
                    //Log.d("local", mCurrentPhotoPath);
                    //if(imgFile.exists()){
                    //    Bitmap myBitmap = BitmapHelper.decodeSampledBitmapFromLocal(imgFile.getAbsolutePath(), 100, 200);
                    //}
                }
                break;
            default:
                break;
        }
    }

    private String getStringFromUri(Uri contentUri) {
        String path = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(columnIndex);
        }
        cursor.close();
        return path;
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


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = nomeCaderno.toUpperCase()+"_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getString(R.string.app_name));

        if(!storageDir.isDirectory()){
            storageDir.mkdirs();
        }

        if(!mCurrentPhotoPath.isEmpty()){
            File mExistente = new File(mCurrentPhotoPath);
            if(mExistente.exists()){
                boolean temp = mExistente.delete();
                if(temp){
                    Log.d("img_deletado", "Imagem deletada");
                }
            }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_editar_folha, container, false);
        edtTitulo = (EditText) view.findViewById(R.id.edtxt_titulo_folha);
        edtTags = (EditText) view.findViewById(R.id.edtxt_tags);
        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        btnAddFolha = (Button) view.findViewById(R.id.btn_adicionar_folha);
        btnGetCamera = (Button) view.findViewById(R.id.btn_camera);
        //btnGetGallery = (Button) view.findViewById(R.id.btn_galeria);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        folhaDataSource = new FolhaDataSource(view.getContext());
        folhaDataSource.open();

        Bundle bundle = getActivity().getIntent().getExtras();
        fk_caderno = bundle.getLong(Database.FOLHA_FK_CADERNO);
        nomeCaderno = bundle.getString(Database.CADERNO_TITULO);
        id_folha = bundle.getLong(Database.FOLHA_ID);
        cor_principal = bundle.getInt(Database.CADERNO_COR_PRINCIPAL);
        cor_secundaria = bundle.getInt(Database.CADERNO_COR_SECUNDARIA);
        String titulo = bundle.getString(Database.FOLHA_TITULO);
        mCurrentPhotoPath = bundle.getString(Database.FOLHA_LOCAL_IMAGEM);
        String tags = bundle.getString(Database.TAG_TAG);
        if (tags != null) {
            tags = tags.replace("[","").replace("]", "");
            velhas_tags = tags.split(",");
        }

        edtTitulo.setText(titulo);
        edtTags.setText(tags);

        File imgFile = new  File(mCurrentPhotoPath);
        int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getActivity());
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
                    myBitmap, screenWidth, 500);
            imgThumb.setImageBitmap(ThumbImage);
        }

        btnGetCamera.setOnClickListener(this);
        //btnGetGallery.setOnClickListener(this);
        btnAddFolha.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btn_adicionar_folha){
            String titulo = edtTitulo.getText().toString();
            String tags = edtTags.getText().toString();
            String[] res_tags = null;
            //HashMap<String, String> novas_tags = new HashMap<>();
            if(!tags.isEmpty()){
                res_tags = tags.split("[#.;,]");
                for(int i=0; i < res_tags.length; i++){
                    res_tags[i] = res_tags[i].trim();
                }
            }
            //Toast.makeText(v.getContext(), Arrays.toString(res_tags), Toast.LENGTH_SHORT).show();
            if(!mCurrentPhotoPath.isEmpty()) {
                folhaDataSource.editarFolha(mCurrentPhotoPath, id_folha, fk_caderno, titulo, res_tags,
                        velhas_tags);
                Bundle b = new Bundle();
                b.putString(Database.FOLHA_TITULO, titulo);
                tags = "["+tags+"]";
                b.putString(Database.TAG_TAG, tags);
                b.putString(Database.FOLHA_LOCAL_IMAGEM, mCurrentPhotoPath);
                Intent i = getActivity().getIntent();
                i.putExtras(b);
                getActivity().setResult(Activity.RESULT_OK, i);
                getActivity().finish();
            }else {
                Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getString(R.string.alert_empty_img),
                        Toast.LENGTH_SHORT).show();
            }
        }/*else if(id == R.id.btn_galeria){
            if(mCurrentPhotoPath.isEmpty()){
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }else{
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert_attention)
                        .setMessage(R.string.alert_mensage_img)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, RESULT_LOAD_IMAGE);
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        }*/
        else if(id == R.id.btn_camera){
            if(mCurrentPhotoPath.isEmpty()){
                dispatchTakePictureIntent();
            }else{
                //Log.d("local", mCurrentPhotoPath);
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert_attention)
                        .setMessage(R.string.alert_mensage_img)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dispatchTakePictureIntent();
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        }
    }
}
