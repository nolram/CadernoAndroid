package com.lab11.nolram.cadernocamera;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.lab11.nolram.components.AdapterCardsFolha;
import com.lab11.nolram.components.RecyclerItemClickListener;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.FolhaDataSource;
import com.lab11.nolram.database.model.Folha;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchTagActivityFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsFolha mAdapter;
    private Toolbar toolbar;

    private String query;

    private FolhaDataSource folhaDataSource;

    private List<Folha> folhas;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle(query);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //toolbar.setTitle(titulo);
    }

    @Override
    public void onResume() {
        folhaDataSource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        folhaDataSource.close();
        super.onPause();
    }

    public SearchTagActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search_tag, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        query = bundle.getString(Database.TAG_TAG);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view_folhas);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        folhaDataSource = new FolhaDataSource(view.getContext());
        folhaDataSource.open();

        folhas = folhaDataSource.getAllFolhasByTag(query);

        mAdapter = new AdapterCardsFolha(folhas);
        mRecyclerView.swapAdapter(mAdapter, true);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(view.getContext(), FolhaActivity.class);
                        Bundle bundle = new Bundle();
                        String[] cores;
                        Folha folha = folhas.get(position);
                        bundle.putString(Database.FOLHA_LOCAL_IMAGEM, folha.getLocal_folha());
                        bundle.putString(Database.FOLHA_TITULO, folha.getTitulo());
                        bundle.putString(Database.FOLHA_DATA, folha.getData_adicionado());
                        bundle.putString(Database.TAG_TAG, folha.getTags().toString());
                        cores = folhaDataSource.getColor(folha.getFk_caderno());
                        bundle.putString(Database.CADERNO_COR_SECUNDARIA, cores[0]);
                        bundle.putString(Database.CADERNO_COR_PRINCIPAL, cores[1]);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
        );

        return view;
    }
}
