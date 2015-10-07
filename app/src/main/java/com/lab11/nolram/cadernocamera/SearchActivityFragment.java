package com.lab11.nolram.cadernocamera;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.lab11.nolram.components.AdapterCardsSearchCaderno;
import com.lab11.nolram.components.RecyclerItemClickListener;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.CadernoDataSource;
import com.lab11.nolram.database.model.Caderno;
import com.lab11.nolram.database.model.CadernoTagFolha;
import com.lab11.nolram.database.model.Folha;
import com.lab11.nolram.database.model.Tag;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {

    private CadernoDataSource cadernoDataSource;

    private List<CadernoTagFolha> myDataset;

    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsSearchCaderno mAdapter;
    private Toolbar toolbar;

    private static final int FOLHAS = 1;
    private static final int CADERNO = 2;
    private static final int TAGS = 3;

    public SearchActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        cadernoDataSource.open();
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
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view_main);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        cadernoDataSource = new CadernoDataSource(view.getContext());
        cadernoDataSource.open();

        String query = "";
        Intent intent = getActivity().getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            //Toast.makeText(getActivity().getApplicationContext(), query, Toast.LENGTH_SHORT).show();
        }

        AsyncPesquisa asyncPesquisa = new AsyncPesquisa();
        asyncPesquisa.execute(query);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(getTypeClass(position) == CADERNO) {
                            Intent intent = new Intent(view.getContext(), NotesActivity.class);
                            Bundle bundle = new Bundle();
                            Caderno caderno = myDataset.get(position).getCaderno();
                            bundle.putLong(Database.FOLHA_FK_CADERNO, caderno.getId());
                            bundle.putString(Database.CADERNO_COR_PRINCIPAL, caderno.getCorPrincipal());
                            bundle.putString(Database.CADERNO_COR_SECUNDARIA, caderno.getCorSecundaria());
                            bundle.putString(Database.CADERNO_TITULO, caderno.getTitulo());
                            bundle.putString(Database.CADERNO_BADGE, caderno.getBadge());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else if (getTypeClass(position) == FOLHAS){
                            Intent intent = new Intent(view.getContext(), FolhaActivity.class);
                            Bundle bundle = new Bundle();
                            Folha folha = myDataset.get(position).getFolha();
                            Caderno cad = cadernoDataSource.getCaderno(folha.getFk_caderno());
                            bundle.putInt(FolhaActivity.INDICE, folha.getContador()-1);
                            bundle.putLong(Database.FOLHA_FK_CADERNO, folha.getFk_caderno());
                            bundle.putString(Database.CADERNO_TITULO, cad.getTitulo());
                            bundle.putString(Database.CADERNO_BADGE, cad.getBadge());
                            //bundle.putStringArray(Database.TAG_TAG, folha.getTags().toArray(new
                            //        String[folha.getTags().size()]));
                            int cor_principal;
                            int cor_secundaria;
                            int id_cor_principal = getResources().getIdentifier(
                                    cad.getCorPrincipal(), "drawable",
                                    getActivity().getPackageName());
                            int id_cor_secundaria = view.getResources().getIdentifier(
                                    cad.getCorSecundaria(), "drawable",
                                    getActivity().getPackageName());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                cor_principal = getResources().getColor(id_cor_principal,
                                        getActivity().getTheme());
                                cor_secundaria = getResources().getColor(id_cor_secundaria,
                                        getActivity().getTheme());
                            }else{
                                cor_principal = getResources().getColor(id_cor_principal);
                                cor_secundaria = getResources().getColor(id_cor_secundaria);
                            }
                            bundle.putInt(Database.CADERNO_COR_SECUNDARIA, cor_secundaria);
                            bundle.putInt(Database.CADERNO_ID_COR_SECUNDARIA, id_cor_secundaria);
                            bundle.putInt(Database.CADERNO_COR_PRINCIPAL, cor_principal);
                            bundle.putInt(Database.CADERNO_ID_COR_PRINCIPAL, id_cor_principal);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else {
                            Tag tag = myDataset.get(position).getTag();
                            Intent intent = new Intent(view.getContext(), SearchTagActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(Database.TAG_TAG, tag.getTag());
                            bundle.putLong(Database.TAG_ID, tag.getId());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                })
        );

        return view;
    }

    class AsyncPesquisa extends AsyncTask<String, Void, Void> {

        public AsyncPesquisa(){
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {
            String query = params[0];
            List<Caderno> cadernos = cadernoDataSource.searchCadernos(query);
            List<Folha> folhas = cadernoDataSource.searchFolhas(query);
            List<Tag> tags = cadernoDataSource.searchTags(query);
            carregarDataset(cadernos, folhas, tags);
            return null;
        }

        @Override
        protected void onPostExecute(Void voidd) {
            progressBar.setVisibility(View.GONE);
            mAdapter = new AdapterCardsSearchCaderno(myDataset, getContext());
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setHasFixedSize(true);
        }
    }

    public int getTypeClass (int position) {
        CadernoTagFolha ctf = myDataset.get(position);
        if(ctf.getFolha() == null && ctf.getCaderno() == null){
            return TAGS;
        }else if(ctf.getTag() == null && ctf.getCaderno() == null){
            return FOLHAS;
        }else {
            return CADERNO;
        }
    }

    public void carregarDataset(List<Caderno> mCadernos, List<Folha> mFolhas, List<Tag> mTags){
        myDataset = new ArrayList<CadernoTagFolha>();
        for(Caderno c: mCadernos){
            myDataset.add(new CadernoTagFolha(c));
        }
        for(Folha f: mFolhas){
            myDataset.add(new CadernoTagFolha(f));
        }
        for(Tag t: mTags){
            myDataset.add(new CadernoTagFolha(t));
        }
    }
}
