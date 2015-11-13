package com.lab11.nolram.cadernocamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsCaderno mAdapter;
    private FloatingActionButton btnCaderno;

    private int type_order_by;

    private CadernoDataSource cadernoDataSource;

    private List<Caderno> cadernos;

    public MainActivityFragment() {
    }

    @Override
    public void onResume() {
        cadernoDataSource.open();
        cadernos = cadernoDataSource.getAllCadernos(type_order_by);
        mAdapter.updateAll(cadernos);
        mAdapter.notifyDataSetChanged();
        btnCaderno.show(); // To fix bug
        super.onResume();
    }

    @Override
    public void onPause() {
        cadernoDataSource.close();
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        type_order_by = CadernoDataSource.RECENTES_MODIFICADOS;

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view_main);
        btnCaderno = (FloatingActionButton) view.findViewById(R.id.fab);

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        cadernoDataSource = new CadernoDataSource(view.getContext());
        cadernoDataSource.open();

        cadernos = cadernoDataSource.getAllCadernos(CadernoDataSource.RECENTES_MODIFICADOS);

        mAdapter = new AdapterCardsCaderno(cadernos, view.getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        if(cadernos.size() == 0) {
            new MaterialShowcaseView.Builder(getActivity())
                    .setTarget(btnCaderno)
                    .setDismissText(R.string.txt_got_it)
                    .setContentText(R.string.txt_no_notebook)
                    .singleUse(Constants.SHOW_HOW_TO_MAIN)
                    .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
                    .show();
        }

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(view.getContext(), NotesActivity.class);
                        Bundle bundle = new Bundle();
                        Caderno caderno = cadernos.get(position);
                        bundle.putLong(Database.FOLHA_FK_CADERNO, caderno.getId());
                        bundle.putString(Database.CADERNO_COR_PRINCIPAL, caderno.getCorPrincipal());
                        bundle.putString(Database.CADERNO_COR_SECUNDARIA, caderno.getCorSecundaria());
                        bundle.putString(Database.CADERNO_TITULO, caderno.getTitulo());
                        bundle.putString(Database.CADERNO_BADGE, caderno.getBadge());
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                })
        );

        btnCaderno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(v.getContext(), CriarCadernoActivity.class);
                startActivity(a);
            }
        });
        return view;
    }

    public void updateList(int tipo_order_by){
        cadernos.clear();
        type_order_by = tipo_order_by;
        cadernos = cadernoDataSource.getAllCadernos(tipo_order_by);
        mAdapter.updateAll(cadernos);
        mAdapter.notifyDataSetChanged();
    }
}
