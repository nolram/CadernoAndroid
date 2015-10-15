package com.lab11.nolram.cadernocamera;

import android.app.AlertDialog;
import android.content.Context;
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
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lab11.nolram.components.DatePickerFragment;
import com.lab11.nolram.components.DeviceDimensionsHelper;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.FolhaDataSource;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A placeholder fragment containing a simple view.
 */
public class CriarFolhaActivityFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RESULT_LOAD_IMAGE = 2;
    String mCurrentPhotoPath = "";
    private int cor_principal;
    private int cor_secundaria;
    private long fk_caderno;
    private String nomeCaderno;
    private EditText edtTitulo;
    private EditText edtTags;
    private ImageView imgThumb;
    private Button btnAddFolha;
    private Button btnCancelar;
    private Button date_chooser;
    private FloatingActionButton btnGetCamera;
    private FloatingActionButton btnGetGallery;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsing_toolbar;
    private FolhaDataSource folhaDataSource;
    private DateTime dateTime;

    public CriarFolhaActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().setTitle("");
        //toolbar.setBackgroundColor(cor_principal);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(cor_secundaria);
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
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == getActivity().RESULT_OK) {
                    File imgFile = new File(mCurrentPhotoPath);
                    int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getActivity());
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
                                myBitmap, screenWidth, 500);
                        imgThumb.setImageBitmap(ThumbImage);
                    }
                    //Bitmap photo = (Bitmap) data.getExtras().get("data");
                    //saveBitmap(photo);
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    File imgFile = new File(mCurrentPhotoPath);
                    //Log.d("local", mCurrentPhotoPath);
                    if (imgFile.exists()) {
                        imgFile.delete();
                        mCurrentPhotoPath = "";
                        imgThumb.setImageResource(R.drawable.header_image);
                    }
                }
                break;
            case RESULT_LOAD_IMAGE:
                if (resultCode == getActivity().RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    Log.d("path_gallery", selectedImage.getPath());

                    try {
                        int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getActivity());
                        if(isNewGooglePhotosUri(selectedImage)){
                            selectedImage = getImageUrlWithAuthority(getContext(), selectedImage);
                        }
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                getActivity().getContentResolver(), selectedImage);
                        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bitmap,
                                screenWidth, 500);
                        imgThumb.setImageBitmap(ThumbImage);

                        File f = createImageFile();

                        try {
                            f.createNewFile();
                            copyFile(new File(getStringFromUri(selectedImage)), f);
                            mCurrentPhotoPath = f.getAbsolutePath();
                            Log.d("path_gallery_copy", mCurrentPhotoPath);
                        } catch (IOException e) {
                            mCurrentPhotoPath = "";
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException f) {
                        mCurrentPhotoPath = "";
                        Toast.makeText(getActivity().getApplicationContext(),
                                getString(R.string.txt_error_file_not_found),
                                Toast.LENGTH_SHORT).show();
                        f.printStackTrace();
                    } catch (IOException e) {
                        mCurrentPhotoPath = "";
                        e.printStackTrace();
                    }
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

    public static boolean isNewGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

    public static Uri getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private String getStringFromUri(Uri contentUri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
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

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }


    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(getString(R.string.time_stamp)).format(new Date());
        String imageFileName = nomeCaderno.toUpperCase() + "_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getString(R.string.app_name));

        if (!storageDir.isDirectory()) {
            storageDir.mkdirs();
        }

        if (!mCurrentPhotoPath.isEmpty()) {
            File mExistente = new File(mCurrentPhotoPath);
            if (mExistente.exists()) {
                boolean temp = mExistente.delete();
                if (temp) {
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
        final View view = inflater.inflate(R.layout.fragment_criar_folha, container, false);
        date_chooser = (Button) view.findViewById(R.id.date_chooser);
        edtTitulo = (EditText) view.findViewById(R.id.edtxt_titulo_folha);
        edtTags = (EditText) view.findViewById(R.id.edtxt_tags);
        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        btnAddFolha = (Button) view.findViewById(R.id.btn_adicionar_folha);
        btnCancelar = (Button) view.findViewById(R.id.btn_cancelar);
        btnGetCamera = (FloatingActionButton) view.findViewById(R.id.fab_cam);
        collapsing_toolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        btnGetGallery = (FloatingActionButton) view.findViewById(R.id.fab_gal);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        folhaDataSource = new FolhaDataSource(view.getContext());
        folhaDataSource.open();

        Bundle bundle = getActivity().getIntent().getExtras();
        fk_caderno = bundle.getLong(Database.FOLHA_FK_CADERNO);
        nomeCaderno = bundle.getString(Database.CADERNO_TITULO);
        cor_principal = bundle.getInt(Database.CADERNO_COR_PRINCIPAL);
        cor_secundaria = bundle.getInt(Database.CADERNO_COR_SECUNDARIA);

        collapsing_toolbar.setContentScrimColor(cor_principal);

        dateTime = new DateTime();
        DateTimeFormatter dtf = DateTimeFormat.forPattern(getString(R.string.date_format));
        date_chooser.setText(dtf.print(dateTime));

        btnGetCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentPhotoPath.isEmpty()) {
                    dispatchTakePictureIntent();
                } else {
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
        });

        btnGetGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                    Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                                }

                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }
            }
        });

        btnAddFolha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarFolha();
            }
        });

        date_chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void adicionarFolha() {
        String titulo = edtTitulo.getText().toString();
        String tags = edtTags.getText().toString();
        String[] res_tags = null;
        if (!tags.isEmpty()) {
            res_tags = tags.split("[#.;,]");
            for (int i = 0; i < res_tags.length; i++) {
                res_tags[i] = res_tags[i].trim();
            }
        }
        if (!mCurrentPhotoPath.isEmpty()) {
            folhaDataSource.criarFolha(mCurrentPhotoPath, fk_caderno, titulo, res_tags, dateTime);
            getActivity().finish();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.alert_empty_img),
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                dateTime = new DateTime(year, month + 1, day, 0, 0);
                DateTimeFormatter dtf = DateTimeFormat.forPattern(getString(R.string.date_format));
                date_chooser.setText(dtf.print(dateTime));
            }
        };
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

}
