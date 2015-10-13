package com.lab11.nolram.cadernocamera;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.lab11.nolram.components.AdapterCardsFolha;
import com.lab11.nolram.components.BitmapHelper;
import com.lab11.nolram.components.RecyclerItemClickListener;
import com.lab11.nolram.components.SimpleItemTouchHelperCallback;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.FolhaDataSource;
import com.lab11.nolram.database.model.Folha;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import com.melnykov.fab.FloatingActionButton;


/**
 * A placeholder fragment containing a simple view.
 */
public class NotesActivityFragment extends Fragment {

    public static final int UPDATE = 11;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private long fk_caderno;
    private int cor_principal;
    private int id_cor_principal;
    private int cor_secundaria;
    private int id_cor_secundaria;
    private String titulo;
    private String badge;

    private Intent intentUpdate;

    private RecyclerView mRecyclerView;
    private FloatingActionButton btnAddFolha;
    private FloatingActionButton btnCamera;
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsFolha mAdapter;
    private Toolbar toolbar;

    private ItemTouchHelper mItemTouchHelper;

    private FolhaDataSource folhaDataSource;

    private List<Folha> folhas;
    private String mCurrentPhotoPath;
    private boolean salvarImagem = false;


    public NotesActivityFragment() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_generate_pdf) {
            if (folhas.size() > 0) {
                AsyncGeneratePDF gerar = new AsyncGeneratePDF(getActivity());
                gerar.execute();
                //printDocument();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.alert_no_paper), Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_imprimir_caderno) {
            if (folhas.size() > 0) {
                printDocument();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.alert_no_paper), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_delete_caderno) {
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.alert_attention)
                    .setMessage(R.string.alert_delete_paper_warning)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (Folha f : folhas) {
                                File img = new File(f.getLocal_folha());
                                if (img.exists()) {
                                    img.delete();
                                }
                            }
                            folhaDataSource.deleteCaderno(fk_caderno);
                            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(
                                    R.string.alert_caderno_deletado), Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }

                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
            return true;
        } else if (id == R.id.action_edit_caderno) {
            intentUpdate = new Intent(getActivity().getApplicationContext(), EditarCadernoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong(Database.CADERNO_ID, fk_caderno);
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
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(cor_secundaria);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPDATE:
                if (resultCode == getActivity().RESULT_OK) {
                    id_cor_principal = getResources().getIdentifier(
                            data.getStringExtra(Database.CADERNO_COR_PRINCIPAL), "drawable",
                            getActivity().getPackageName());

                    id_cor_secundaria = getResources().getIdentifier(
                            data.getStringExtra(Database.CADERNO_COR_SECUNDARIA), "drawable",
                            getActivity().getPackageName());

                    cor_principal = getResources().getColor(id_cor_principal);
                    cor_secundaria = getResources().getColor(id_cor_secundaria);

                    titulo = data.getStringExtra(Database.CADERNO_TITULO);
                    getActivity().setTitle(titulo);
                    toolbar.setBackgroundColor(cor_principal);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getActivity().getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(cor_secundaria);
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == getActivity().RESULT_OK) {
                    salvarImagem = true;
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                    File imgFile = new File(mCurrentPhotoPath);
                    if (imgFile.exists()) {
                        imgFile.delete();
                        mCurrentPhotoPath = "";
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        folhaDataSource.open();
        folhas = folhaDataSource.getAllFolhas(fk_caderno);
        if (salvarImagem) {
            Folha folha = folhaDataSource.criarFolhaERetornar(mCurrentPhotoPath, fk_caderno,
                    "");
            folhas.add(folha);
            salvarImagem = false;
            mCurrentPhotoPath = "";
        }
        mAdapter.updateAll(folhas);
        mAdapter.notifyDataSetChanged();

        super.onResume();
    }

    @Override
    public void onPause() {
        folhaDataSource.close();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notes, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view_folhas);
        btnAddFolha = (FloatingActionButton) view.findViewById(R.id.fab);
        btnCamera = (FloatingActionButton) view.findViewById(R.id.fab_cam);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        setHasOptionsMenu(true);

        mCurrentPhotoPath = "";

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        Bundle bundle = getActivity().getIntent().getExtras();
        fk_caderno = bundle.getLong(Database.FOLHA_FK_CADERNO);
        id_cor_principal = getResources().getIdentifier(
                bundle.getString(Database.CADERNO_COR_PRINCIPAL), "drawable",
                getActivity().getPackageName());
        id_cor_secundaria = getResources().getIdentifier(
                bundle.getString(Database.CADERNO_COR_SECUNDARIA), "drawable",
                getActivity().getPackageName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cor_principal = getResources().getColor(id_cor_principal, getActivity().getTheme());
            cor_secundaria = getResources().getColor(id_cor_secundaria, getActivity().getTheme());
        } else {
            cor_principal = getResources().getColor(id_cor_principal);
            cor_secundaria = getResources().getColor(id_cor_secundaria);
        }

        titulo = bundle.getString(Database.CADERNO_TITULO);
        badge = bundle.getString(Database.CADERNO_BADGE);

        folhaDataSource = new FolhaDataSource(view.getContext());
        folhaDataSource.open();

        folhas = folhaDataSource.getAllFolhas(fk_caderno);

        mAdapter = new AdapterCardsFolha(folhas, view.getContext(),
                folhaDataSource);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(view.getContext(), FolhaActivity.class);
                        Bundle bundle = new Bundle();
                        Folha folha = folhas.get(position);
                        bundle.putInt(FolhaActivity.INDICE, position);
                        bundle.putLong(Database.FOLHA_FK_CADERNO, folha.getFk_caderno());
                        bundle.putString(Database.CADERNO_TITULO, titulo);
                        bundle.putString(Database.CADERNO_BADGE, badge);
                        bundle.putString(Database.FOLHA_DATA, folha.getData());
                        //bundle.putStringArray(Database.TAG_TAG, folha.getTags().toArray(new
                        //        String[folha.getTags().size()]));
                        bundle.putInt(Database.CADERNO_COR_SECUNDARIA, cor_secundaria);
                        bundle.putInt(Database.CADERNO_ID_COR_SECUNDARIA, id_cor_secundaria);
                        bundle.putInt(Database.CADERNO_COR_PRINCIPAL, cor_principal);
                        bundle.putInt(Database.CADERNO_ID_COR_PRINCIPAL, id_cor_principal);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
        );

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnAddFolha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFolha(view.getContext());
            }
        });
        return view;
    }

    private void addFolha(Context context) {
        Intent a = new Intent(context, CriarFolhaActivity.class);
        Bundle b = new Bundle();
        b.putLong(Database.FOLHA_FK_CADERNO, fk_caderno);
        b.putString(Database.CADERNO_TITULO, titulo);
        b.putInt(Database.CADERNO_COR_PRINCIPAL, cor_principal);
        b.putInt(Database.CADERNO_COR_SECUNDARIA, cor_secundaria);
        a.putExtras(b);
        startActivity(a);
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
        String imageFileName = titulo.toUpperCase() + "_" + timeStamp + "_";
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

    private void pdfDocument(Uri uri) {
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(uri, "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent intent = Intent.createChooser(target, getString(R.string.txt_open_file));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity().getApplicationContext(),
                    getString(R.string.alert_no_pdf_reader),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void printDocument() {
        PrintManager printManager = (PrintManager) getActivity()
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) + " Documento";

        printManager.print(jobName, new MyPrintDocumentAdapter(getActivity()),
                null);
    }

    class AsyncGeneratePDF extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;
        GeneratePDF generatePDF;
        Context mContext;

        public AsyncGeneratePDF(Context context) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle(context.getString(R.string.txt_title_async_pdf));
            progressDialog.setMessage(context.getString(R.string.txt_message_async_pdf));
            progressDialog.show();
            this.mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            generatePDF = new GeneratePDF(getActivity().getApplicationContext());
            generatePDF.gerarPDF();
            return null;
        }

        @Override
        protected void onPostExecute(Void voidd) {
            progressDialog.dismiss();
        }
    }

    public class GeneratePDF {
        private static final String PRINT_SERVICE = "gerar_pdf";
        private static final String DOTS = " .............................................. ";
        public PdfDocument myPdfDocument;
        public int summaryPagesAll = 0;
        public int contSummaryPages = 0;
        public int contListFolha = 0;
        public int totalpages = folhas.size() + 1; //Folhas + Qtd páginas de sumário + Capa
        Context context;
        List<List<Folha>> groupFolhas = new ArrayList<List<Folha>>();
        private int pageHeight;
        private int pageWidth;
        private OutputStream os;


        public GeneratePDF(Context context) {
            this.context = context;
            onLayout();
        }

        public void onLayout() {
            PrintAttributes printAttrs = new PrintAttributes.Builder().
                    setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                    setMediaSize(PrintAttributes.MediaSize.ISO_A4).
                    setResolution(new PrintAttributes.Resolution("zooey", PRINT_SERVICE, 300, 300)).
                    setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                    build();
            myPdfDocument = new PrintedPdfDocument(context, printAttrs);

            pageHeight = printAttrs.getMediaSize().getHeightMils() / 1000 * 72;
            pageWidth = printAttrs.getMediaSize().getWidthMils() / 1000 * 72;

            int inicio = 0;
            int fim = 0;
            do {
                if (fim + 15 <= folhas.size())
                    fim += 15;
                else
                    fim = folhas.size();
                groupFolhas.add(folhas.subList(inicio, fim));
                inicio = fim;
                //Log.d("inicio", String.valueOf(inicio));
                //Log.d("fim", String.valueOf(fim));
            } while (fim < folhas.size());

            summaryPagesAll = groupFolhas.size();
            totalpages += summaryPagesAll;

        }

        private void drawHomePage(PdfDocument.Page page, int pagenumber) {
            Canvas canvas = page.getCanvas();

            pagenumber++;

            PdfDocument.PageInfo pageInfo = page.getInfo();

            int y = pageInfo.getPageHeight() / 2;
            int x = pageInfo.getPageWidth() / 2;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(titulo, x, y, paint);

            DateTime now = new DateTime();
            DateTimeFormatter dtf = DateTimeFormat.forPattern(getString(R.string.date_format));
            //canvas.drawText();

            paint.setTextSize(20);

            Paint paintRect = new Paint();
            paintRect.setColor(cor_principal);
            Rect rect = new Rect(pageInfo.getPageWidth() - 50, 0, pageInfo.getPageWidth() - 100, 150);
            canvas.drawRect(rect, paintRect);

            canvas.drawText(dtf.print(now), x, y + 50, paint);
            canvas.drawText(getString(R.string.txt_generate_by), x, y + 200, paint);
        }


        private void drawSummaryPage(PdfDocument.Page page, int pagenumber) {
            Canvas canvas = page.getCanvas();
            pagenumber++;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(40);

            PdfDocument.PageInfo pageInfo = page.getInfo();
            int y = 72;
            int x = canvas.getWidth() / 2;
            int ySummary = y + 35;
            int xSummary = 54;

            canvas.drawText(getString(R.string.txt_summary), x, y, paint);

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(18);


            for (int i = 0; i < groupFolhas.get(contSummaryPages).size(); i++) {
                File imgFile = new File(groupFolhas.get(contSummaryPages).get(i).getLocal_folha());
                if (imgFile.exists()) {
                    Folha folha = groupFolhas.get(contSummaryPages).get(i);
                    canvas.drawText(String.valueOf(folha.getContador() + summaryPagesAll + 1) + DOTS +
                            folha.getTitulo(), xSummary, ySummary, paint);
                    ySummary = ySummary + 35;
                }
            }
            contSummaryPages++;
        }

        private void drawPage(PdfDocument.Page page, int pagenumber) {
            Canvas canvas = page.getCanvas();
            File imgFile;
            if (contListFolha < folhas.size()) {
                imgFile = new File(folhas.get(contListFolha).getLocal_folha());
                contListFolha++;
                if (imgFile.exists()) {
                    pagenumber++; // Make sure page numbers start at 1

                    PdfDocument.PageInfo pageInfo = page.getInfo();

                    int titleBaseLine = pageInfo.getPageHeight();
                    int leftMargin = pageInfo.getPageWidth() - 20;

                    Paint paint = new Paint();
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(15);
                    //paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(Integer.toString(pagenumber), leftMargin, titleBaseLine,
                            paint);

                    //Log.d("local", mCurrentPhotoPath);

                    //canvas.drawCircle(pageInfo.getPageWidth() / 2, pageInfo.getPageHeight() / 2, 150, paint);

                    Bitmap myBitmap = BitmapHelper.decodeSampledBitmapFromLocal(imgFile.getAbsolutePath(),
                            pageInfo.getPageWidth(), pageInfo.getPageHeight());

                    RectF content = new RectF(page.getInfo().getContentRect());
                    Matrix matrix = new Matrix();
                    // Compute and apply scale to fill the page.
                    float scale = content.width() / myBitmap.getWidth();
                    //if (fittingMode == SCALE_MODE_FILL) {
                    //    scale = Math.max(scale, content.height() / myBitmap.getHeight());
                    //} else {
                    scale = Math.min(scale, content.height() / myBitmap.getHeight());
                    //}
                    matrix.postScale(scale, scale);

                    // Center the content.
                    final float translateX = (content.width()
                            - myBitmap.getWidth() * scale) / 2;
                    final float translateY = (content.height()
                            - myBitmap.getHeight() * scale) / 2;
                    matrix.postTranslate(translateX, translateY);

                    canvas.drawBitmap(myBitmap, matrix, paint);
                }
            }

        }

        public void gerarPDF() {
            for (int i = 0; i < totalpages; i++) {
                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                        pageHeight, i).create();

                PdfDocument.Page page =
                        myPdfDocument.startPage(newPage);
                if (i == 0) {
                    drawHomePage(page, i);
                } else if (summaryPagesAll != contSummaryPages) {
                    drawSummaryPage(page, i);
                } else {
                    //Log.d("acessado", "acessado");
                    drawPage(page, i);
                }
                myPdfDocument.finishPage(page);

            }
            contListFolha = 0;
            try {
                File tempPDF = File.createTempFile("temp", ".pdf", getActivity().getExternalCacheDir());
                tempPDF.deleteOnExit();
                os = new FileOutputStream(tempPDF);
                myPdfDocument.writeTo(os);
                myPdfDocument.close();
                os.close();

                pdfDocument(Uri.fromFile(tempPDF));
            } catch (IOException e) {
                Toast.makeText(getActivity().getApplicationContext(),
                        R.string.txt_error_pdf_io, Toast.LENGTH_LONG).show();
                //throw new RuntimeException("Error generating file", e);
            } catch (NullPointerException n) {
                Log.e("error", "Processo de gerar PDF cancelado.");
            } finally {
                myPdfDocument.close();
            }
        }
    }

    public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
        private static final String DOTS = " .............................................. ";
        public PdfDocument myPdfDocument;
        public int summaryPagesAll = 0;
        public int contSummaryPages = 0;
        public int contListFolha = 0;
        public int totalpages = folhas.size() + 1; //Folhas + Qtd páginas de sumário + Capa
        Context context;
        List<List<Folha>> groupFolhas = new ArrayList<List<Folha>>();
        private int pageHeight;
        private int pageWidth;

        public MyPrintDocumentAdapter(Context context) {
            this.context = context;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes,
                             PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback,
                             Bundle metadata) {

            myPdfDocument = new PrintedPdfDocument(context, newAttributes);

            pageHeight = newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
            pageWidth = newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

            int inicio = 0;
            int fim = 0;
            do {
                if (fim + 15 <= folhas.size())
                    fim += 15;
                else
                    fim = folhas.size();
                groupFolhas.add(folhas.subList(inicio, fim));
                inicio = fim;
                //Log.d("inicio", String.valueOf(inicio));
                //Log.d("fim", String.valueOf(fim));
            } while (fim < folhas.size());

            summaryPagesAll = groupFolhas.size();
            totalpages += summaryPagesAll;

            //Log.d("contListaGroup", String.valueOf(groupFolhas.size()));
            //Log.d("contLista", String.valueOf(folhas.size()));

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder(titulo + ".pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_PHOTO)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }

        private boolean pageInRange(PageRange[] pageRanges, int page) {
            for (int i = 0; i < pageRanges.length; i++) {
                if ((page >= pageRanges[i].getStart()) &&
                        (page <= pageRanges[i].getEnd()))
                    return true;
            }
            return false;
        }


        private void drawHomePage(PdfDocument.Page page, int pagenumber) {
            Canvas canvas = page.getCanvas();

            pagenumber++;

            PdfDocument.PageInfo pageInfo = page.getInfo();

            int y = pageInfo.getPageHeight() / 2;
            int x = pageInfo.getPageWidth() / 2;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(titulo, x, y, paint);

            DateTime now = new DateTime();
            DateTimeFormatter dtf = DateTimeFormat.forPattern(getString(R.string.date_format));
            //canvas.drawText();

            paint.setTextSize(20);

            Paint paintRect = new Paint();
            paintRect.setColor(cor_principal);
            Rect rect = new Rect(pageInfo.getPageWidth() - 50, 0, pageInfo.getPageWidth() - 100, 150);
            canvas.drawRect(rect, paintRect);

            canvas.drawText(dtf.print(now), x, y + 50, paint);
            canvas.drawText(getString(R.string.txt_generate_by), x, y + 200, paint);
        }


        private void drawSummaryPage(PdfDocument.Page page, int pagenumber) {
            Canvas canvas = page.getCanvas();
            pagenumber++;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(40);

            PdfDocument.PageInfo pageInfo = page.getInfo();
            int y = 72;
            int x = canvas.getWidth() / 2;
            int ySummary = y + 35;
            int xSummary = 54;

            canvas.drawText(getString(R.string.txt_summary), x, y, paint);

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(18);


            for (int i = 0; i < groupFolhas.get(contSummaryPages).size(); i++) {
                File imgFile = new File(groupFolhas.get(contSummaryPages).get(i).getLocal_folha());
                if (imgFile.exists()) {
                    Folha folha = groupFolhas.get(contSummaryPages).get(i);
                    canvas.drawText(String.valueOf(folha.getContador() + summaryPagesAll + 1) + DOTS +
                            folha.getTitulo(), xSummary, ySummary, paint);
                    ySummary = ySummary + 35;
                }
            }
            contSummaryPages++;
        }

        private void drawPage(PdfDocument.Page page, int pagenumber) {
            Canvas canvas = page.getCanvas();
            File imgFile;
            if (contListFolha < folhas.size()) {
                imgFile = new File(folhas.get(contListFolha).getLocal_folha());
                contListFolha++;
                if (imgFile.exists()) {
                    pagenumber++; // Make sure page numbers start at 1

                    PdfDocument.PageInfo pageInfo = page.getInfo();

                    int titleBaseLine = pageInfo.getPageHeight();
                    int leftMargin = pageInfo.getPageWidth() - 20;

                    Paint paint = new Paint();
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(15);
                    //paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(Integer.toString(pagenumber), leftMargin, titleBaseLine,
                            paint);

                    //Log.d("local", mCurrentPhotoPath);

                    //canvas.drawCircle(pageInfo.getPageWidth() / 2, pageInfo.getPageHeight() / 2, 150, paint);

                    Bitmap myBitmap = BitmapHelper.decodeSampledBitmapFromLocal(imgFile.getAbsolutePath(),
                            pageInfo.getPageWidth(), pageInfo.getPageHeight());

                    RectF content = new RectF(page.getInfo().getContentRect());
                    Matrix matrix = new Matrix();
                    // Compute and apply scale to fill the page.
                    float scale = content.width() / myBitmap.getWidth();
                    //if (fittingMode == SCALE_MODE_FILL) {
                    //    scale = Math.max(scale, content.height() / myBitmap.getHeight());
                    //} else {
                    scale = Math.min(scale, content.height() / myBitmap.getHeight());
                    //}
                    matrix.postScale(scale, scale);

                    // Center the content.
                    final float translateX = (content.width()
                            - myBitmap.getWidth() * scale) / 2;
                    final float translateY = (content.height()
                            - myBitmap.getHeight() * scale) / 2;
                    matrix.postTranslate(translateX, translateY);

                    canvas.drawBitmap(myBitmap, matrix, paint);
                }
            }
        }

        @Override
        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal cancellationSignal,
                            final WriteResultCallback callback) {

            for (int i = 0; i < totalpages; i++) {
                if (pageInRange(pageRanges, i)) {
                    PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                            pageHeight, i).create();

                    PdfDocument.Page page =
                            myPdfDocument.startPage(newPage);

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }
                    //Log.d("summaryPagesAll", String.valueOf(summaryPagesAll));
                    //Log.d("contSummaryPages", String.valueOf(contSummaryPages));
                    if (i == 0) {
                        drawHomePage(page, i);
                    } else if (summaryPagesAll != contSummaryPages) {
                        drawSummaryPage(page, i);
                    } else {
                        //Log.d("acessado", "acessado");
                        drawPage(page, i);
                    }
                    myPdfDocument.finishPage(page);
                }
            }
            contListFolha = 0; // Quando o arquivo é enviado ou gerado pdf esse método é chamado novamente
            // ou seja
            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            callback.onWriteFinished(pageRanges);
        }

    }

}
