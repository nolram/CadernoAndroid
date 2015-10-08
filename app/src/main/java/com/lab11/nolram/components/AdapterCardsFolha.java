package com.lab11.nolram.components;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lab11.nolram.cadernocamera.R;
import com.lab11.nolram.database.controller.FolhaDataSource;
import com.lab11.nolram.database.model.Folha;
import com.lab11.nolram.database.model.Tag;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nolram on 25/08/15.
 */
public class AdapterCardsFolha extends RecyclerView.Adapter<AdapterCardsFolha.ViewHolder>
        implements ItemTouchHelperAdapter{
    private Context mContext;
    private List<Folha> mDataset = new ArrayList<>();
    private View layoutView;
    private FolhaDataSource folhaDataSource;

    public AdapterCardsFolha(List<Folha> myDataset, Context context, FolhaDataSource folhaDataSource) {
        mDataset.addAll(myDataset);
        mContext = context;
        this.folhaDataSource = folhaDataSource;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_folha, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Folha folha = mDataset.get(position);
        List<Tag> tags = folha.getTags();
        String tags_st = "";
        holder.mNumPageView.setText(String.valueOf(folha.getContador()));
        for(int i=0; i < tags.size(); i++){
            tags_st += tags.get(i) + "; ";
        }
        holder.mTagView.setText(tags_st);

        holder.mDate.setText(folha.getData());
        File file = new File(folha.getLocal_folha());
        if(file.exists()){
            //int screenWidth = DeviceDimensionsHelper.getDisplayWidth(mContext);
            //Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()),
            //        screenWidth, (int) DeviceDimensionsHelper.convertDpToPixel(150 ,mContext), true);
            //holder.mThumbFolhaView.setImageBitmap(myBitmap);
            loadBitmap(file.getAbsolutePath(), holder.mThumbFolhaView);
        }
        //holder.mThumbFolhaView
        holder.mTitleView.setText(folha.getTitulo());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onItemMove(int dePosicao, int paraPosicao) {
        //FIXME Quando movimenta mais de uma posição o contador fica bugado pode ser problema de sincronismo
        //Log.d("Elements Before", mDataset.toString());
        Folha deFolha = mDataset.remove(dePosicao);
        mDataset.add(paraPosicao, deFolha);
        notifyItemMoved(dePosicao, paraPosicao);
        Folha paraFolha = mDataset.get(dePosicao);
        //Log.d("Para", String.valueOf(paraPosicao) + " " + paraFolha.getTitulo());
        //Log.d("De", String.valueOf(dePosicao) + " " + deFolha.getTitulo());
        //Log.d("Elements After", mDataset.toString());
        paraFolha.setContador(paraPosicao + 1);
        deFolha.setContador(dePosicao + 1);
        folhaDataSource.moveItem(paraFolha, deFolha);
        //WorkerUpdate workerDatabase = new WorkerUpdate(mContext);
        //workerDatabase.execute(paraFolha, deFolha);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder{
        // each localImagem item is just a string in this case
        public TextView mTitleView;
        public TextView mTagView;
        public TextView mNumPageView;
        public ImageView mThumbFolhaView;
        public TextView mDate;

        public ViewHolder(View v) {
            super(v);
            mTagView = (TextView) v.findViewById(R.id.txt_tags);
            mTitleView = (TextView) v.findViewById(R.id.txt_title);
            mNumPageView = (TextView) v.findViewById(R.id.txt_num_pagina);
            mThumbFolhaView = (ImageView) v.findViewById(R.id.img_thumb_folha);
            mDate = (TextView) v.findViewById(R.id.txt_data);
        }

        @Override
        public void onItemSelected() {
            //((CardView) itemView).setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            //((CardView) itemView).setCardBackgroundColor(0);
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String localImagem = "";

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            localImagem = params[0];
            File file = new File(localImagem);
            if(file.exists()){
                int screenWidth = DeviceDimensionsHelper.getDisplayWidth(mContext);
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(localImagem), screenWidth,
                        (int) DeviceDimensionsHelper.convertDpToPixel(310, mContext));
                return ThumbImage;
            }
            return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }else if(imageViewReference != null && bitmap == null && !isCancelled()){
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageResource(R.drawable.picture_remove);
                }
            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.localImagem;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData.isEmpty() || !bitmapData.equalsIgnoreCase(data)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public void loadBitmap(String locaImagem, ImageView imageView) {
        if (cancelPotentialWork(locaImagem, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mContext.getResources(), task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(locaImagem);
        }
    }

    public void updateAll(List<Folha> folhas){
        mDataset = folhas;
    }

}
