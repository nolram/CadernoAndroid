package com.lab11.nolram.cadernocamera;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.lab11.nolram.components.AdapterCardsCaderno;
import com.lab11.nolram.components.RecyclerItemClickListener;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.CadernoDataSource;
import com.lab11.nolram.database.model.Caderno;
import com.lab11.nolram.database.model.Tag;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final static int QTD_MENUS = 2;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsCaderno mAdapter;
    private FloatingActionButton btnCaderno;

    private CadernoDataSource cadernoDataSource;

    private List<Caderno> cadernos;

    public MainActivityFragment() {
    }

    @Override
    public void onResume() {
        cadernoDataSource.open();
        cadernos = cadernoDataSource.getAllCadernos();
        mAdapter.updateAll(cadernos);
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onPause() {
        cadernoDataSource.close();
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view_main);
        btnCaderno = (FloatingActionButton) view.findViewById(R.id.fab);

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        cadernoDataSource = new CadernoDataSource(view.getContext());
        cadernoDataSource.open();

        cadernos = cadernoDataSource.getAllCadernos();

        mAdapter = new AdapterCardsCaderno(cadernos, view.getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

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

                        /*ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                // the context of the activity
                                getActivity(),
                                // For each shared element, add to this method a new Pair item,
                                // which contains the reference of the view we are transitioning *from*,
                                // and the value of the transitionName attribute
                                new Pair<View, String>(view.findViewById(R.id.img_cor),
                                        getString(R.string.transition_color))
                        );
                        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());*/

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
}
