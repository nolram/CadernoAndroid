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
import android.widget.AdapterView;
import android.widget.Toast;

import com.lab11.nolram.components.AdapterCardsCaderno;
import com.lab11.nolram.components.RecyclerItemClickListener;
import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.CadernoDataSource;
import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsCaderno mAdapter;
    private FloatingActionButton btnCaderno;
    private Toolbar toolbar;
    private Drawer result;

    private CadernoDataSource cadernoDataSource;

    public MainActivityFragment() {
    }

    @Override
    public void onResume() {
        cadernoDataSource.open();

        mAdapter = new AdapterCardsCaderno(cadernoDataSource.getAllCadernos());
        mRecyclerView.swapAdapter(mAdapter, true);
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
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        result = new DrawerBuilder(getActivity())
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_home).withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName(R.string.menu_categorias).withIcon(FontAwesome.Icon.faw_align_justify),
                        new PrimaryDrawerItem().withName(R.string.menu_favoritos).withIcon(FontAwesome.Icon.faw_bookmark),
                        new SectionDrawerItem().withName(R.string.app_name),
                        new SecondaryDrawerItem().withName(R.string.menu_configuracoes).withIcon(FontAwesome.Icon.faw_gear)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();

                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rec_view_main);
        btnCaderno = (FloatingActionButton) v.findViewById(R.id.fab);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);


        linearLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        cadernoDataSource = new CadernoDataSource(v.getContext());
        cadernoDataSource.open();

        mAdapter = new AdapterCardsCaderno(cadernoDataSource.getAllCadernos());
        mRecyclerView.swapAdapter(mAdapter, true);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(v.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent a = new Intent(v.getContext(), NotesActivity.class);
                        Bundle b = new Bundle();
                        b.putLong(Database.FOLHA_FK_CADERNO, cadernoDataSource.getAllCadernos().get(position).getId());
                        a.putExtras(b);
                        startActivity(a);
                    }
                })
        );

        btnCaderno.attachToRecyclerView(mRecyclerView);

        btnCaderno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(v.getContext(), CriarCadernoActivity.class);
                startActivity(a);
            }
        });
        return v;
    }
}
