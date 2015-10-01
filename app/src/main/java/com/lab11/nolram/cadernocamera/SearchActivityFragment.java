package com.lab11.nolram.cadernocamera;

import android.app.SearchManager;
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

import com.lab11.nolram.components.AdapterCardsSearchCaderno;
import com.lab11.nolram.components.RecyclerItemClickListener;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.CadernoDataSource;
import com.lab11.nolram.database.model.Caderno;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {

    private CadernoDataSource cadernoDataSource;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsSearchCaderno mAdapter;
    private Toolbar toolbar;
    private List<Caderno> cadernos;

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

        cadernos = cadernoDataSource.searchCadernos(query);

        mAdapter = new AdapterCardsSearchCaderno(cadernos, view.getContext());
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

        return view;
    }
}
