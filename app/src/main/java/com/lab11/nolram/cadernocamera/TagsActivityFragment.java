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

import com.lab11.nolram.components.AdapterCardsTag;
import com.lab11.nolram.components.RecyclerItemClickListener;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.CadernoDataSource;
import com.lab11.nolram.database.controller.FolhaDataSource;
import com.lab11.nolram.database.model.Tag;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TagsActivityFragment extends Fragment {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private CadernoDataSource cadernoDataSource;
    private AdapterCardsTag mAdapter;

    private List<Tag> tags;
    private LinearLayoutManager linearLayoutManager;

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

    public TagsActivityFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rec_view_tags);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        cadernoDataSource = new CadernoDataSource(view.getContext());
        cadernoDataSource.open();

        tags = cadernoDataSource.getAllTagsGroupBy();
        mAdapter = new AdapterCardsTag(tags, view.getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Tag tag = tags.get(position);
                        Intent intent = new Intent(view.getContext(), SearchTagActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(Database.TAG_TAG, tag.getTag());
                        bundle.putLong(Database.TAG_ID, tag.getId());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
        );

        return view;
    }
}
