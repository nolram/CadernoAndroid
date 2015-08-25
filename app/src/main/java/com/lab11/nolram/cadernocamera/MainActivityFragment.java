package com.lab11.nolram.cadernocamera;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lab11.nolram.components.AdapterCardsCaderno;
import com.lab11.nolram.components.RecyclerItemClickListener;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.CadernoDataSource;
import com.melnykov.fab.FloatingActionButton;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsCaderno mAdapter;
    private FloatingActionButton btnCaderno;

    private CadernoDataSource cadernoDataSource;

    public MainActivityFragment() {
    }

    @Override
    public void onResume() {
        cadernoDataSource.open();

        mAdapter = new AdapterCardsCaderno(cadernoDataSource.getAllCadernos());
        mRecyclerView.swapAdapter(mAdapter, true);
        mAdapter.notifyDataSetChanged();

        super.onResume();
    }

    @Override
    public void onPause() {
        cadernoDataSource.close();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rec_view_main);
        btnCaderno = (FloatingActionButton) v.findViewById(R.id.fab);
        linearLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        cadernoDataSource = new CadernoDataSource(v.getContext());
        cadernoDataSource.open();

        mAdapter = new AdapterCardsCaderno(cadernoDataSource.getAllCadernos());
        mRecyclerView.swapAdapter(mAdapter, true);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(v.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent a = new Intent(v.getContext(), NotesActivity.class);
                        Bundle b = new Bundle();
                        b.putLong(Database.FOLHA_FK_CADERNO, cadernoDataSource.getAllCadernos().get(position).getId());
                        a.putExtras(b);
                        startActivity(a);
                    }
                })
        );

        btnCaderno.attachToRecyclerView(mRecyclerView);

        btnCaderno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(v.getContext(), CriarCadernoActivity.class);
                startActivity(a);
            }
        });
        return v;
    }
}
