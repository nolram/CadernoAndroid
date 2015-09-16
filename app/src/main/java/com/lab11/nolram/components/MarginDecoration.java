package com.lab11.nolram.components;

/**
 * Created by nolram on 16/09/15.
 * Get from: https://github.com/chiuki/android-recyclerview
 * http://blog.sqisland.com/2014/12/recyclerview-grid-with-header.html
 */
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lab11.nolram.cadernocamera.R;

public class MarginDecoration extends RecyclerView.ItemDecoration {
    private int margin;

    public MarginDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.item_margin);
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(margin, margin, margin, margin);
    }
}
