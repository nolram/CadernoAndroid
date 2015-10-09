package com.lab11.nolram.cadernocamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lab11.nolram.components.AdapterCardsIconPicker;
import com.lab11.nolram.components.MarginDecoration;
import com.lab11.nolram.components.RecyclerItemClickListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class IconPickerActivityFragment extends Fragment {

    public static final String ICONE_ESCOLHIDO = "icone_escolhido";
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    private AdapterCardsIconPicker mAdapter;

    public IconPickerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_icon_picker, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view_img_picker);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mAdapter = new AdapterCardsIconPicker();
        mRecyclerView.addItemDecoration(new MarginDecoration(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(view.getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(ICONE_ESCOLHIDO, mAdapter.getItem(position));
                        getActivity().setResult(Activity.RESULT_OK, resultIntent);
                        getActivity().finish();
                    }
                }));
        return view;
    }
}
