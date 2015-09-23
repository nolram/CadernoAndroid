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
import com.lab11.nolram.database.controller.CadernoDataSource;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {

    private CadernoDataSource cadernoDataSource;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsSearchCaderno mAdapter;
    private Toolbar toolbar;

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

        mAdapter = new AdapterCardsSearchCaderno(cadernoDataSource.searchCadernos(query),
                view.getContext());
        mRecyclerView.swapAdapter(mAdapter, true);
        return view;
    }
}
