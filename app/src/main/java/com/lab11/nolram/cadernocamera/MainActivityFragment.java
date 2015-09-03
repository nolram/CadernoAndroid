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
import com.lab11.nolram.database.model.Caderno;
import com.lab11.nolram.database.model.Tag;
import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final static int QTD_MENUS = 3;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private AdapterCardsCaderno mAdapter;
    private FloatingActionButton btnCaderno;
    private Toolbar toolbar;
    private Drawer menu;

    private CadernoDataSource cadernoDataSource;

    private List<Tag> tags;

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

        tags = cadernoDataSource.getAllTagsGroupBy();
        menu = new DrawerBuilder(getActivity())
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new SectionDrawerItem().withName(R.string.app_name),
                        new PrimaryDrawerItem().withName(R.string.menu_configuracoes).withIcon(FontAwesome.Icon.faw_gear),
                        new SectionDrawerItem().withName(R.string.tags)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                        if (position > 2) {
                            Tag tag = tags.get(position-QTD_MENUS);
                            Intent intent = new Intent(view.getContext(), SearchTagActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(Database.TAG_TAG, tag.getTag());
                            bundle.putLong(Database.TAG_ID, tag.getId());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
        for(int i = 0; i < tags.size(); i++) {
            Tag tmp_tag = tags.get(i);
            menu.addItem(new SecondaryDrawerItem().withIcon(FontAwesome.Icon.faw_tag).withName(tmp_tag.getTag()).withBadge(
                    String.valueOf(tmp_tag.getContador())));
        }
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

        mAdapter = new AdapterCardsCaderno(cadernoDataSource.getAllCadernos());
        mRecyclerView.swapAdapter(mAdapter, true);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(view.getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(view.getContext(), NotesActivity.class);
                        Bundle bundle = new Bundle();
                        Caderno caderno = cadernoDataSource.getAllCadernos().get(position);
                        bundle.putLong(Database.FOLHA_FK_CADERNO, caderno.getId());
                        bundle.putString(Database.CADERNO_COR_PRINCIPAL, caderno.getCorPrincipal());
                        bundle.putString(Database.CADERNO_COR_SECUNDARIA, caderno.getCorSecundaria());
                        bundle.putString(Database.CADERNO_TITULO, caderno.getTitulo());
                        intent.putExtras(bundle);
                        startActivity(intent);
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
        return view;
    }
}
