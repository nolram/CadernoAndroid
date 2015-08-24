package com.lab11.nolram.cadernocamera;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;


/**
 * A placeholder fragment containing a simple view.
 */
public class NotesActivityFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton btnAddFolha;

    public NotesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.rec_view_folhas);
        btnAddFolha = (FloatingActionButton) v.findViewById(R.id.fab_imagem);

        return v;
    }
}
