package com.lab11.nolram.cadernocamera;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lab11.nolram.components.AdapterCardsCaderno;
import com.lab11.nolram.components.AdapterCardsSearchCaderno;
import com.lab11.nolram.database.controller.CadernoDataSource;
import com.melnykov.fab.FloatingActionButton;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {

    private CadernoDataSource cadernoDataSource;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsSearchCaderno mAdapter;
    private FloatingActionButton btnCaderno;
    private Toolbar toolbar;

    public SearchActivityFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view_main);
        btnCaderno = (FloatingActionButton) view.findViewById(R.id.fab);
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

        mAdapter = new AdapterCardsSearchCaderno(cadernoDataSource.searchCadernos(query));
        mRecyclerView.swapAdapter(mAdapter, true);
        return view;
    }
}
