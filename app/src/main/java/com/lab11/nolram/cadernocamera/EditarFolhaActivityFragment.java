package com.lab11.nolram.cadernocamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lab11.nolram.components.DatePickerFragment;
import com.lab11.nolram.components.DeviceDimensionsHelper;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.FolhaDataSource;
import com.lab11.nolram.database.model.Tag;

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
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditarFolhaActivityFragment extends Fragment implements OnClickListener, EditarFolhaActivity.Callbacks {


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int RESULT_LOAD_IMAGE = 2;
    String mCurrentPhotoPath = "";
    private long fk_caderno;
    private long id_folha;
    private String nomeCaderno;
    private List<String> velhas_tags;
    private int cor_principal;
    private int cor_secundaria;
    private EditText edtTitulo;
    private EditText edtTags;
    private ImageView imgThumb;
    private Button btnAddFolha;
    private Button btnCancelar;
    private Button date_chooser;
    private FloatingActionButton btnGetCamera;
    private FloatingActionButton btnGetGallery;
    private Toolbar toolbar;
    private FolhaDataSource folhaDataSource;
    private ProgressBar progressBarEditar;
    private DateTime dateTime;
    private CollapsingToolbarLayout collapsing_toolbar;

    public EditarFolhaActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
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
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    File imgFile = new File(mCurrentPhotoPath);
                    //Log.d("local", mCurrentPhotoPath);
                    if (imgFile.exists()) {
                        imgFile.delete();
                        mCurrentPhotoPath = "";
                        imgThumb.setImageDrawable(null);
                    }
                }
                break;
            case RESULT_LOAD_IMAGE:
                if (resultCode == Activity.RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    Log.d("path_gallery", selectedImage.getPath());
                    WorkerUpdateImage wU = new WorkerUpdateImage(getContext());
                    wU.execute(selectedImage);
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
        final View view = inflater.inflate(R.layout.fragment_editar_folha, container, false);
        edtTitulo = (EditText) view.findViewById(R.id.edtxt_titulo_folha);
        edtTags = (EditText) view.findViewById(R.id.edtxt_tags);
        imgThumb = (ImageView) view.findViewById(R.id.img_thumb);
        btnAddFolha = (Button) view.findViewById(R.id.btn_adicionar_folha);
        btnCancelar = (Button) view.findViewById(R.id.btn_cancelar);
        date_chooser = (Button) view.findViewById(R.id.date_chooser);
        btnGetCamera = (FloatingActionButton) view.findViewById(R.id.fab_cam);
        btnCancelar = (Button) view.findViewById(R.id.btn_cancelar);
        progressBarEditar = (ProgressBar) view.findViewById(R.id.progressBarEditar);
        collapsing_toolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        btnGetGallery = (FloatingActionButton) view.findViewById(R.id.fab_gal);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        folhaDataSource = new FolhaDataSource(view.getContext());
        folhaDataSource.open();

        Bundle bundle = getActivity().getIntent().getExtras();
        fk_caderno = bundle.getLong(Database.FOLHA_FK_CADERNO);
        nomeCaderno = bundle.getString(Database.CADERNO_TITULO);
        id_folha = bundle.getLong(Database.FOLHA_ID);
        dateTime = new DateTime(bundle.getString(Database.FOLHA_DATA));
        cor_principal = bundle.getInt(Database.CADERNO_COR_PRINCIPAL);
        cor_secundaria = bundle.getInt(Database.CADERNO_COR_SECUNDARIA);
        String titulo = bundle.getString(Database.FOLHA_TITULO);
        mCurrentPhotoPath = bundle.getString(Database.FOLHA_LOCAL_IMAGEM);
        String tags = bundle.getString(Database.TAG_TAG);
        List<Tag> tags_list = folhaDataSource.getFolha(id_folha).getTags();
        velhas_tags = new ArrayList<>();
        if (tags != null) {
            for (int i = 0; i < tags_list.size(); i++) {
                velhas_tags.add(tags_list.get(i).getTagMin());
            }
        }

        collapsing_toolbar.setContentScrimColor(cor_principal);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
        date_chooser.setText(dtf.print(dateTime));

        edtTitulo.setText(titulo);
        edtTags.setText(tags.replace("[", "").replace("]", ""));

        File imgFile = new File(mCurrentPhotoPath);

        if (imgFile.exists()) {
            loadBitmap(mCurrentPhotoPath, imgThumb);
        }

        btnGetCamera.setOnClickListener(this);
        //btnGetGallery.setOnClickListener(this);
        btnAddFolha.setOnClickListener(this);
        date_chooser.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        btnGetGallery.setOnClickListener(this);

        return view;
    }

    class WorkerUpdateImage extends AsyncTask<Uri, Void, Void> {
        ProgressDialog progressDialog;
        Bitmap ThumbImage;

        public WorkerUpdateImage(Context context) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle(R.string.txt_loading_image);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Uri... params) {
            try {
                Uri selectedImage = params[0];
                int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getActivity());
                if(isNewGooglePhotosUri(selectedImage)){
                    selectedImage = getImageUrlWithAuthority(getContext(), selectedImage);
                }
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        getActivity().getContentResolver(), selectedImage);
                ThumbImage = ThumbnailUtils.extractThumbnail(bitmap,
                        screenWidth, 500);
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
            return null;
        }

        @Override
        protected void onPostExecute(Void voidd) {
            imgThumb.setImageBitmap(ThumbImage);
            progressDialog.cancel();
        }
    }

    public void loadBitmap(String localImagem, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(localImagem);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_adicionar_folha) {
            String titulo = edtTitulo.getText().toString();
            String tags = edtTags.getText().toString();
            String[] res_tags = null;
            List<String> novasTagsList = new ArrayList<>();
            if (!tags.isEmpty()) {
                res_tags = tags.split("[#.;,]");
                for (int i = 0; i < res_tags.length; i++) {
                    novasTagsList.add(res_tags[i].toLowerCase().trim());
                }
            }
            //Toast.makeText(v.getContext(), Arrays.toString(res_tags), Toast.LENGTH_SHORT).show();
            if (!mCurrentPhotoPath.isEmpty()) {
                WorkerUpdate workerUpdate = new WorkerUpdate(getContext(), titulo, tags,
                        novasTagsList);
                workerUpdate.execute();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getString(R.string.alert_empty_img),
                        Toast.LENGTH_SHORT).show();
            }
        }else if (id == R.id.fab_cam) {
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
        }else if(id == R.id.btn_cancelar){
            getActivity().onBackPressed();
        }else if(id == R.id.date_chooser){
            showDatePickerDialog();
        }else if(id == R.id.fab_gal){
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
    }

    @Override
    public void onBackPressedCallback() {
        Toast.makeText(getContext(), R.string.txt_canceled_edit_sheet, Toast.LENGTH_SHORT).show();
    }

    class WorkerUpdate extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;
        String titulo;
        String tags;
        List<String> novasTagsList = new ArrayList<>();

        public WorkerUpdate(Context context, String titulo, String tags, List<String> novasTags) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle(context.getString(R.string.txt_alterando));
            progressDialog.show();
            this.titulo = titulo;
            this.tags = tags;
            this.novasTagsList = novasTags;
        }

        @Override
        protected Void doInBackground(Void... params) {
            folhaDataSource.editarFolha(mCurrentPhotoPath, id_folha, fk_caderno, titulo,
                    novasTagsList, velhas_tags, dateTime);
            return null;
        }

        @Override
        protected void onPostExecute(Void voidd) {
            Bundle b = new Bundle();
            b.putString(Database.FOLHA_TITULO, titulo);
            tags = "[" + tags + "]";
            b.putString(Database.TAG_TAG, tags);
            b.putString(Database.FOLHA_LOCAL_IMAGEM, mCurrentPhotoPath);
            Intent i = getActivity().getIntent();
            i.putExtras(b);
            getActivity().setResult(Activity.RESULT_OK, i);
            getActivity().finish();
            progressDialog.cancel();
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String data = "";

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected void onPreExecute() {
            progressBarEditar.setVisibility(View.VISIBLE);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            Bitmap newBitmap;
            Bitmap ThumbImage;
            try {
                int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getActivity());
                newBitmap = BitmapFactory.decodeFile(data);
                ThumbImage = ThumbnailUtils.extractThumbnail(newBitmap, screenWidth, 500);
            } catch (NullPointerException e) {
                ThumbImage = null;
            }
            return ThumbImage;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressBarEditar.setVisibility(View.GONE);
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                dateTime = new DateTime(year, month + 1, day, 0, 0);
                DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
                date_chooser.setText(dtf.print(dateTime));
            }
        };
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
}
