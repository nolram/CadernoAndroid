package com.lab11.nolram.components;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lab11.nolram.cadernocamera.R;
import com.lab11.nolram.database.model.Caderno;

import java.util.List;

/**
 * Created by nolram on 24/08/15.
 */
public class AdapterCardsCaderno extends RecyclerView.Adapter<AdapterCardsCaderno.ViewHolder> {
    private List<Caderno> mDataset;
    private View layoutView;

    public AdapterCardsCaderno(List<Caderno> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_caderno, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Caderno caderno = mDataset.get(position);
        holder.mTitleView.setText(caderno.getTitulo());
        holder.mTextView.setText(caderno.getDescricao());
        holder.mDateView.setText(caderno.getUltimaModificacao());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTitleView;
        public TextView mTextView;
        public TextView mDateView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.txt_descricao);
            mTitleView = (TextView) v.findViewById(R.id.txt_title);
            mDateView = (TextView) v.findViewById(R.id.txt_modificacao);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
