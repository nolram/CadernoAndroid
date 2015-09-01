package com.lab11.nolram.cadernocamera;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lab11.nolram.components.AdapterCardsCaderno;
import com.lab11.nolram.components.AdapterCardsFolha;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.FolhaDataSource;
import com.melnykov.fab.FloatingActionButton;


/**
 * A placeholder fragment containing a simple view.
 */
public class NotesActivityFragment extends Fragment {

    private long fk_caderno;
    private String cor_principal;
    private String cor_secundaria;

    private RecyclerView mRecyclerView;
    private FloatingActionButton btnAddFolha;
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsFolha mAdapter;
    private Toolbar toolbar;

    private FolhaDataSource folhaDataSource;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Integer.valueOf(cor_principal));
    }

    @Override
    public void onResume() {
        folhaDataSource.open();

        /*mAdapter = new AdapterCardsFolha(folhaDataSource.getAllFolhas(fk_caderno));
        mRecyclerView.swapAdapter(mAdapter, true);
        mAdapter.notifyDataSetChanged();*/

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

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        Bundle bundle = getActivity().getIntent().getExtras();
        fk_caderno = bundle.getLong(Database.FOLHA_FK_CADERNO);
        cor_principal = bundle.getString(Database.CADERNO_COR_PRINCIPAL);
        cor_principal = bundle.getString(Database.CADERNO_COR_SECUNDARIA);

        folhaDataSource = new FolhaDataSource(view.getContext());
        folhaDataSource.open();

        mAdapter = new AdapterCardsFolha(folhaDataSource.getAllFolhas(fk_caderno));
        mRecyclerView.swapAdapter(mAdapter, true);

        btnAddFolha.attachToRecyclerView(mRecyclerView);

        btnAddFolha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(view.getContext(), CriarFolhaActivity.class);
                Bundle b = new Bundle();
                b.putLong(Database.FOLHA_FK_CADERNO, fk_caderno);
                a.putExtras(b);
                startActivity(a);
            }
        });
        return view;
    }
}
