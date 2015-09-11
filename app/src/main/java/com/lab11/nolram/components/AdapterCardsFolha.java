package com.lab11.nolram.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lab11.nolram.cadernocamera.R;
import com.lab11.nolram.database.model.Folha;
import com.lab11.nolram.database.model.Tag;

import java.io.File;
import java.util.List;

/**
 * Created by nolram on 25/08/15.
 */
public class AdapterCardsFolha extends RecyclerView.Adapter<AdapterCardsFolha.ViewHolder> {
    private Context mContext;
    private List<Folha> mDataset;
    private View layoutView;

    public AdapterCardsFolha(List<Folha> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
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
        holder.mNumPageView.setText(Integer.toString(position+1));
        for(int i=0; i < tags.size(); i++){
            tags_st += tags.get(i) + "; ";
        }
        holder.mTagView.setText(tags_st);
        File file = new File(folha.getLocal_folha());
        if(file.exists()){
            int screenWidth = DeviceDimensionsHelper.getDisplayWidth(mContext);
            Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()),
                    screenWidth, (int) DeviceDimensionsHelper.convertDpToPixel(150 ,mContext), true);

            holder.mThumbFolhaView.setImageBitmap(myBitmap);
        }
        //holder.mThumbFolhaView
        holder.mTitleView.setText(folha.getTitulo());

    }
    public static Bitmap scaleToFitWidth(Bitmap b, int width)

    {

        float factor = width / (float) b.getWidth();

        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);

    }
    private int dpToPx(int dp)
    {
        float density = mContext.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTitleView;
        public TextView mTagView;
        public TextView mNumPageView;
        public ImageView mThumbFolhaView;

        public ViewHolder(View v) {
            super(v);
            mTagView = (TextView) v.findViewById(R.id.txt_tags);
            mTitleView = (TextView) v.findViewById(R.id.txt_title);
            mNumPageView = (TextView) v.findViewById(R.id.txt_num_pagina);
            mThumbFolhaView = (ImageView) v.findViewById(R.id.img_thumb_folha);
        }
    }

}
