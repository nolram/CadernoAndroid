package com.lab11.nolram.cadernocamera;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.lab11.nolram.components.AdapterCardsCaderno;
import com.lab11.nolram.components.AdapterCardsFolha;
import com.lab11.nolram.components.BitmapHelper;
import com.lab11.nolram.components.RecyclerItemClickListener;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.FolhaDataSource;
import com.lab11.nolram.database.model.Caderno;
import com.lab11.nolram.database.model.Folha;
import com.melnykov.fab.FloatingActionButton;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class NotesActivityFragment extends Fragment {

    public static final int UPDATE = 11;

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
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsFolha mAdapter;
    private Toolbar toolbar;

    private FolhaDataSource folhaDataSource;

    private List<Folha> folhas;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_generate_pdf){
            if (folhas.size() > 0) {
                printDocument();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Esse caderno não possui folhas!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if(id == R.id.action_add_folha) {
            addFolha(getActivity().getApplicationContext());
            return true;
        }else if(id == R.id.action_delete_caderno){
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert_attention)
                        .setMessage(R.string.alert_delete_paper_warning)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(Folha f: folhas) {
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
        }else if(id == R.id.action_edit_caderno){
            intentUpdate = new Intent(getActivity().getApplicationContext(), EditarCadernoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong(Database.CADERNO_ID, fk_caderno);
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
                if(resultCode == getActivity().RESULT_OK) {
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
        }
    }

    @Override
    public void onResume() {
        folhaDataSource.open();
        mAdapter.updateAll(folhaDataSource.getAllFolhas(fk_caderno));
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onPause() {
        folhaDataSource.close();
        super.onPause();
    }

    public NotesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notes, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view_folhas);
        btnAddFolha = (FloatingActionButton) view.findViewById(R.id.fab_imagem);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        setHasOptionsMenu(true);

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

        cor_principal = getResources().getColor(id_cor_principal);
        cor_secundaria = getResources().getColor(id_cor_secundaria);

        titulo = bundle.getString(Database.CADERNO_TITULO);
        badge = bundle.getString(Database.CADERNO_BADGE);

        folhaDataSource = new FolhaDataSource(view.getContext());
        folhaDataSource.open();

        folhas = folhaDataSource.getAllFolhas(fk_caderno);

        mAdapter = new AdapterCardsFolha(folhas, getActivity().getApplicationContext());
        mRecyclerView.swapAdapter(mAdapter, true);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(view.getContext(), FolhaActivity.class);
                        Bundle bundle = new Bundle();
                        Folha folha = folhas.get(position);
                        bundle.putString(Database.FOLHA_LOCAL_IMAGEM, folha.getLocal_folha());
                        bundle.putString(Database.FOLHA_TITULO, folha.getTitulo());
                        bundle.putLong(Database.FOLHA_ID, folha.getId());
                        bundle.putString(Database.CADERNO_TITULO, titulo);
                        bundle.putString(Database.CADERNO_BADGE, badge);
                        bundle.putString(Database.FOLHA_DATA, folha.getData_adicionado());
                        bundle.putString(Database.TAG_TAG, folha.getTags().toString());
                        bundle.putInt(Database.CADERNO_COR_SECUNDARIA, cor_secundaria);
                        bundle.putInt(Database.CADERNO_ID_COR_SECUNDARIA, id_cor_secundaria);
                        bundle.putInt(Database.CADERNO_COR_PRINCIPAL, cor_principal);
                        bundle.putInt(Database.CADERNO_ID_COR_PRINCIPAL, id_cor_principal);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
        );

        btnAddFolha.attachToRecyclerView(mRecyclerView);

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
        a.putExtras(b);
        startActivity(a);
    }


    public class MyPrintDocumentAdapter extends PrintDocumentAdapter
    {
        Context context;
        private int pageHeight;
        private int pageWidth;
        public PdfDocument myPdfDocument;
        public int totalpages = folhas.size()+2;
        private static final String DOTS = " .............................................. ";

        public MyPrintDocumentAdapter(Context context)
        {
            this.context = context;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes,
                             PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback,
                             Bundle metadata) {
            myPdfDocument = new PrintedPdfDocument(context, newAttributes);

            pageHeight = newAttributes.getMediaSize().getHeightMils()/1000 * 72;
            pageWidth = newAttributes.getMediaSize().getWidthMils()/1000 * 72;

            if (cancellationSignal.isCanceled() ) {
                callback.onLayoutCancelled();
                return;
            }

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder("print_output.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }

        private boolean pageInRange(PageRange[] pageRanges, int page)
        {
            for (int i = 0; i<pageRanges.length; i++)
            {
                if ((page >= pageRanges[i].getStart()) &&
                        (page <= pageRanges[i].getEnd()))
                    return true;
            }
            return false;
        }


        private void drawHomePage(PdfDocument.Page page, int pagenumber){
            Canvas canvas = page.getCanvas();

            pagenumber++;

            PdfDocument.PageInfo pageInfo = page.getInfo();

            int y = pageInfo.getPageHeight()/2;
            int x = pageInfo.getPageWidth()/2;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(titulo, x, y, paint);

            DateTime now = new DateTime();
            DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
            //canvas.drawText();

            paint.setTextSize(20);

            Paint paintRect = new Paint();
            paintRect.setColor(cor_principal);
            Rect rect = new Rect(pageInfo.getPageWidth()-50, 0, pageInfo.getPageWidth()-100, 150);
            canvas.drawRect(rect, paintRect);

            canvas.drawText(dtf.print(now), x, y + 50, paint);
            canvas.drawText("Gerado por: "+getString(R.string.app_name), x, y+200, paint);
        }


        private void drawSummaryPage(PdfDocument.Page page, int pagenumber){
            Canvas canvas = page.getCanvas();
            pagenumber++;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(40);

            PdfDocument.PageInfo pageInfo = page.getInfo();
            int y = 72;
            int x = canvas.getWidth()/2;
            int ySummary = y + 35;
            int xSummary = 54;

            canvas.drawText("Sumário", x, y, paint);

            //ALERTA: Modificar para que possa suportar multiplas páginas de sumário
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(18);

            for(int i=0; i < folhas.size(); i++){
                File imgFile = new File(folhas.get(i).getLocal_folha());
                if(imgFile.exists()) {
                    canvas.drawText(String.valueOf(i + 3) + DOTS + folhas.get(i).getTitulo(), xSummary,
                            ySummary, paint);
                    ySummary = ySummary + 35;
                }
            }
        }

        private void drawPage(PdfDocument.Page page, int pagenumber) {
            Canvas canvas = page.getCanvas();

            File imgFile = new File(folhas.get(pagenumber - 2).getLocal_folha());

            if(imgFile.exists()) {
                pagenumber++; // Make sure page numbers start at 1

                PdfDocument.PageInfo pageInfo = page.getInfo();

                int titleBaseLine = pageInfo.getPageHeight();
                int leftMargin = pageInfo.getPageWidth()-20;


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

        @Override
        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal cancellationSignal,
                            final WriteResultCallback callback) {

            for (int i = 0; i < totalpages; i++) {
                if (pageInRange(pageRanges, i))
                {
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
                    if(i == 0){
                        drawHomePage(page, i);
                    }else if(i == 1){
                        drawSummaryPage(page, i);
                    }else {
                        drawPage(page, i);
                    }
                    myPdfDocument.finishPage(page);
                }
            }

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

    public void printDocument()
    {
        PrintManager printManager = (PrintManager) getActivity()
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = this.getString(R.string.app_name) +" Documento";

        printManager.print(jobName, new MyPrintDocumentAdapter(getActivity()),
                null);
    }

}
