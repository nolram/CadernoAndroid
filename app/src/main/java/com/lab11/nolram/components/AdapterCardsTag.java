package com.lab11.nolram.components;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lab11.nolram.cadernocamera.R;
import com.lab11.nolram.database.model.Tag;

import java.util.List;

/**
 * Created by nolram on 29/09/15.
 */
public class AdapterCardsTag extends RecyclerView.Adapter<AdapterCardsTag.ViewHolder> {
    private Context mContext;
    private List<Tag> mDataset;
    private View layoutView;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTagView;
        public TextView mContadorView;

        public ViewHolder(View v) {
            super(v);
            mTagView = (TextView) v.findViewById(R.id.txt_label);
            mContadorView = (TextView) v.findViewById(R.id.txt_contador);
        }
    }

    public AdapterCardsTag(List<Tag> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_tag, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(layoutView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tag tag = mDataset.get(position);
        holder.mTagView.setText(tag.getTag());
        holder.mContadorView.setText(String.valueOf(tag.getContador()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
