package com.lab11.nolram.components;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lab11.nolram.cadernocamera.R;
import com.lab11.nolram.database.model.Folha;

import java.util.List;

/**
 * Created by nolram on 25/08/15.
 */
public class AdapterCardsFolha extends RecyclerView.Adapter<AdapterCardsFolha.ViewHolder> {
    private List<Folha> mDataset;
    private View layoutView;

    public AdapterCardsFolha(List<Folha> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_folha, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Folha folha = mDataset.get(position);
        holder.mNumPageView.setText(position+1);
        //holder.mTagView.setText();
        //holder.mThumbFolhaView
        holder.mTitleView.setText(folha.getTitulo());

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
