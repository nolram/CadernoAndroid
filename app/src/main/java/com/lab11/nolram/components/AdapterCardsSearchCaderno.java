package com.lab11.nolram.components;

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
import com.lab11.nolram.database.model.Caderno;
import com.lab11.nolram.database.model.CadernoTagFolha;
import com.lab11.nolram.database.model.Folha;
import com.lab11.nolram.database.model.Tag;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by nolram on 24/08/15.
 */
public class AdapterCardsSearchCaderno extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOLHAS = 1;
    private static final int CADERNO = 2;
    private static final int TAGS = 3;
    public Context mContext;
    private List<CadernoTagFolha> myDataset;
    private View layoutView;

    public AdapterCardsSearchCaderno(List<CadernoTagFolha> myDataset, Context mContext) {
        this.myDataset = myDataset;
        this.mContext = mContext;
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (FOLHAS == viewType) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_folha, parent, false);
            ViewHolderFolha vhc = new ViewHolderFolha(layoutView);
            return vhc;
        } else if (CADERNO == viewType) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_caderno, parent, false);
            ViewHolderCaderno vhc = new ViewHolderCaderno(layoutView);
            return vhc;
        } else {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tag, parent, false);
            ViewHolderTag vhc = new ViewHolderTag(layoutView);
            return vhc;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == CADERNO) {
            ViewHolderCaderno holderCaderno = (ViewHolderCaderno) holder;
            Caderno caderno = myDataset.get(position).getCaderno();
            holderCaderno.mTitleView.setText(caderno.getTitulo());
            holderCaderno.mTextView.setText(caderno.getDescricao());
            holderCaderno.mDateView.setText(caderno.getUltimaModificacao());
            holderCaderno.mCor.setBackgroundColor(mContext.getResources().getColor(mContext.getResources().getIdentifier(
                    caderno.getCorPrincipal(), "drawable", mContext.getPackageName())));
            holderCaderno.mBadge.setImageResource(mContext.getResources().getIdentifier(caderno.getBadge(),
                    "drawable", mContext.getPackageName()));
        } else if (getItemViewType(position) == FOLHAS) {
            ViewHolderFolha holderFolha = (ViewHolderFolha) holder;
            Folha folha = myDataset.get(position).getFolha();
            File file = new File(folha.getLocal_folha());
            if (file.exists()) {
                loadBitmap(file.getAbsolutePath(), holderFolha.mThumbFolhaView);
            }
            holderFolha.mTitleView.setText(folha.getTitulo());
            holderFolha.mDate.setText(folha.getData());
        } else {
            ViewHolderTag holderTag = (ViewHolderTag) holder;
            Tag tag = myDataset.get(position).getTag();
            holderTag.mTagView.setText(tag.getTag());
            //holderTag.mContadorView.setText(String.valueOf(tag.getContador()));
        }
    }

    public int getItemViewType(int position) {
        CadernoTagFolha ctf = myDataset.get(position);
        if (ctf.getFolha() == null && ctf.getCaderno() == null) {
            return TAGS;
        } else if (ctf.getTag() == null && ctf.getCaderno() == null) {
            return FOLHAS;
        } else {
            return CADERNO;
        }
    }

    @Override
    public int getItemCount() {
        return myDataset.size();
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

    public static class ViewHolderCaderno extends RecyclerView.ViewHolder {
        public TextView mTitleView;
        public TextView mTextView;
        public TextView mDateView;
        public ImageView mCor;
        public ImageView mBadge;

        public ViewHolderCaderno(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.txt_descricao);
            mTitleView = (TextView) v.findViewById(R.id.txt_title);
            mDateView = (TextView) v.findViewById(R.id.txt_modificacao);
            mCor = (ImageView) v.findViewById(R.id.img_cor);
            mBadge = (ImageView) v.findViewById(R.id.img_badge);
        }
    }

    public static class ViewHolderFolha extends RecyclerView.ViewHolder {
        // each localImagem item is just a string in this case
        public TextView mTitleView;
        public TextView mTagView;
        public TextView mNumPageView;
        public TextView mDate;
        public ImageView mThumbFolhaView;

        public ViewHolderFolha(View v) {
            super(v);
            mTagView = (TextView) v.findViewById(R.id.txt_tags);
            mTitleView = (TextView) v.findViewById(R.id.txt_title);
            mNumPageView = (TextView) v.findViewById(R.id.txt_num_pagina);
            mThumbFolhaView = (ImageView) v.findViewById(R.id.img_thumb_folha);
            mDate = (TextView) v.findViewById(R.id.txt_data);
        }
    }

    public static class ViewHolderTag extends RecyclerView.ViewHolder {
        public TextView mTagView;
        public TextView mContadorView;

        public ViewHolderTag(View v) {
            super(v);
            mTagView = (TextView) v.findViewById(R.id.txt_label);
            mContadorView = (TextView) v.findViewById(R.id.txt_contador);
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
            if (file.exists()) {
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
            } else if (imageViewReference != null && bitmap == null && !isCancelled()) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageResource(R.drawable.picture_remove);
                }
            }
        }
    }
}
