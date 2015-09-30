package com.lab11.nolram.cadernocamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lab11.nolram.components.AdapterCardsFolha;
import com.lab11.nolram.components.RecyclerItemClickListener;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.FolhaDataSource;
import com.lab11.nolram.database.model.Caderno;
import com.lab11.nolram.database.model.Folha;

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
    private long id;

    private FolhaDataSource folhaDataSource;

    private List<Folha> folhas;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("#"+query);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //toolbar.setTitle(titulo);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
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
        id = bundle.getLong(Database.TAG_ID);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view_folhas);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        folhaDataSource = new FolhaDataSource(view.getContext());
        folhaDataSource.open();

        folhas = folhaDataSource.getAllFolhasByTag(id);

        mAdapter = new AdapterCardsFolha(folhas, getActivity().getApplicationContext(),
                folhaDataSource);
        mRecyclerView.swapAdapter(mAdapter, true);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(view.getContext(), FolhaActivity.class);
                        Bundle bundle = new Bundle();
                        Folha folha = folhas.get(position);
                        Caderno caderno = folhaDataSource.getCaderno(folha.getFk_caderno());

                        bundle.putString(Database.FOLHA_LOCAL_IMAGEM, folha.getLocal_folha());
                        bundle.putString(Database.FOLHA_TITULO, folha.getTitulo());
                        bundle.putLong(Database.FOLHA_ID, folha.getId());
                        bundle.putLong(Database.FOLHA_FK_CADERNO, folha.getFk_caderno());
                        bundle.putString(Database.CADERNO_TITULO, caderno.getTitulo());
                        bundle.putString(Database.CADERNO_BADGE, caderno.getBadge());
                        bundle.putString(Database.FOLHA_DATA, folha.getData_adicionado());
                        bundle.putString(Database.TAG_TAG, folha.getTags().toString());
                        //bundle.putStringArray(Database.TAG_TAG, folha.getTags().toArray(new
                        //        String[folha.getTags().size()]));
                        int id_cor_principal = getResources().getIdentifier(
                                caderno.getCorPrincipal(), "drawable",
                                getActivity().getPackageName());
                        int id_cor_secundaria = getResources().getIdentifier(
                                caderno.getCorSecundaria(), "drawable",
                                getActivity().getPackageName());

                        int cor_principal = getResources().getColor(id_cor_principal);
                        int cor_secundaria = getResources().getColor(id_cor_secundaria);

                        bundle.putInt(Database.CADERNO_COR_SECUNDARIA, cor_secundaria);
                        bundle.putInt(Database.CADERNO_ID_COR_SECUNDARIA, id_cor_secundaria);
                        bundle.putInt(Database.CADERNO_COR_PRINCIPAL, cor_principal);
                        bundle.putInt(Database.CADERNO_ID_COR_PRINCIPAL, id_cor_principal);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
        );

        return view;
    }
}
