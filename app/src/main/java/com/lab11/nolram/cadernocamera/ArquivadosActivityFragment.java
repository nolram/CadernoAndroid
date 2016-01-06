package com.lab11.nolram.cadernocamera;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lab11.nolram.Constants;
import com.lab11.nolram.components.AdapterCardsCaderno;
import com.lab11.nolram.components.RecyclerItemClickListener;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.CadernoDataSource;
import com.lab11.nolram.database.model.Caderno;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArquivadosActivityFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LoadNotebooks loadNotebooks;
    private CadernoDataSource cadernoDataSource;
    private AdapterCardsCaderno mAdapter;
    private List<Caderno> cadernos;
    private Toolbar toolbar;
    private LinearLayoutManager linearLayoutManager;

    public ArquivadosActivityFragment() {
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

    public enum Estados {
        ON_CREATE, ON_RESUME;
    }

    @Override
    public void onResume() {
        cadernoDataSource.open();
        loadNotebooks = new LoadNotebooks(getActivity().getApplicationContext(), Estados.ON_RESUME);
        loadNotebooks.execute();
        super.onResume();
    }

    @Override
    public void onPause() {
        cadernoDataSource.close();
        loadNotebooks.cancel(true);
        super.onPause();
    }

    class LoadNotebooks extends AsyncTask<Void, Void, Void> {
        private Context myContext;
        Estados estados;

        public LoadNotebooks(Context context, Estados estado) {
            myContext = context;
            estados = estado;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(!isCancelled()) {
                cadernos = cadernoDataSource.getAllCadernosArquivados(1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voidd) {
            if (!isCancelled()) {
                switch (estados) {
                    case ON_CREATE:
                        mAdapter = new AdapterCardsCaderno(cadernos, myContext);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setHasFixedSize(true);
                        break;

                    case ON_RESUME:
                        mAdapter.updateAll(cadernos);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_arquivados, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view_arquivados);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);


        cadernoDataSource = new CadernoDataSource(view.getContext());
        cadernoDataSource.open();

        loadNotebooks = new LoadNotebooks(getActivity().getApplicationContext(), Estados.ON_CREATE);
        loadNotebooks.execute();

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(view.getContext(), NotesActivity.class);
                        Bundle bundle = new Bundle();
                        Caderno caderno = cadernos.get(position);
                        bundle.putLong(Database.FOLHA_FK_CADERNO, caderno.getId());
                        bundle.putBoolean(Database.CADERNO_ARQUIVADO, caderno.isArquivado());
                        bundle.putString(Database.CADERNO_COR_PRINCIPAL, caderno.getCorPrincipal());
                        bundle.putString(Database.CADERNO_COR_SECUNDARIA, caderno.getCorSecundaria());
                        bundle.putString(Database.CADERNO_TITULO, caderno.getTitulo());
                        bundle.putString(Database.CADERNO_BADGE, caderno.getBadge());
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                })
        );

        return view;
    }
}
