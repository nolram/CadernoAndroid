package com.lab11.nolram.cadernocamera;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lab11.nolram.database.controller.CadernoDataSource;


/**
 * A placeholder fragment containing a simple view.
 */
public class CriarCadernoActivityFragment extends Fragment{

    private Button btnCriar;
    private ImageButton btnImgPicker;
    private EditText edtDescricao;
    private EditText edtTitulo;
    private RadioGroup radioGroupCor;
    private RadioButton radioCor;
    private Toolbar toolbar;

    public static final int IDENTIFY_INTEGER = 11;

    private CadernoDataSource cadernoDataSource;
    private int icon;

    public CriarCadernoActivityFragment() {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (IDENTIFY_INTEGER) : {
                if (resultCode == Activity.RESULT_OK) {
                    icon = data.getIntExtra(IconPickerActivityFragment.ICONE_ESCOLHIDO,
                            R.drawable.book_2);
                    btnImgPicker.setImageResource(icon);
                }
                break;
            }
        }
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

    public String[] escolher_cor(int id){
        String[] escolhas = new String[2];
        switch (id){
            case R.id.rd_alizarin:
                escolhas[0] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_alizarin));
                escolhas[1] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_pomegranate));
                break;
            case R.id.rd_amethyst:
                escolhas[0] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_amethyst));
                escolhas[1] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_wisteria));
                break;
            case R.id.rd_carrot:
                escolhas[0] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_carrot));
                escolhas[1] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_pumpkin));
                break;
            case R.id.rd_pink:
                escolhas[0] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_pink_weak));
                escolhas[1] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_pink_strong));
                break;
            case R.id.rd_concrete:
                escolhas[0] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_concrete));
                escolhas[1] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_asbestos));
                break;
            case R.id.rd_emerald:
                escolhas[0] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_emerald));
                escolhas[1] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_nephritis));
                break;
            case R.id.rd_peter_river:
                escolhas[0] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_peter_river));
                escolhas[1] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_belize_hole));
                break;
            case R.id.rd_sun_flower:
                escolhas[0] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_sun_flower));
                escolhas[1] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_orange));
                break;
            case R.id.rd_black:
                escolhas[0] = String.valueOf(getActivity().getResources().getColor(R.color.cinza));
                escolhas[1] = String.valueOf(getActivity().getResources().getColor(R.color.cinza_escuro));
                break;
            default:
                escolhas[0] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_wet_asphalt));
                escolhas[1] = String.valueOf(getActivity().getResources().getColor(R.color.flatui_midnight_blue));
                break;

        }
        return escolhas;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_criar_caderno, container, false);
        icon = R.drawable.book_2;
        radioGroupCor = (RadioGroup) v.findViewById(R.id.rd_cor);

        btnCriar = (Button) v.findViewById(R.id.btn_criar_caderno);
        edtDescricao = (EditText) v.findViewById(R.id.edtxt_descricao);
        edtTitulo = (EditText) v.findViewById(R.id.edtxt_titulo);
        btnImgPicker = (ImageButton) v.findViewById(R.id.btn_img_picker);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);

        cadernoDataSource = new CadernoDataSource(v.getContext());
        cadernoDataSource.open();

        btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = edtTitulo.getText().toString();
                String descricao = edtDescricao.getText().toString();
                int selectedId = radioGroupCor.getCheckedRadioButtonId();
                String[] cor = escolher_cor(selectedId);
                if (!titulo.isEmpty() && selectedId != -1) {
                    String nome = getResources().getResourceName(icon);
                    cadernoDataSource.criarCaderno(titulo, descricao, cor, nome);
                    getActivity().finish();
                } else if (selectedId == -1) {
                    Toast.makeText(v.getContext(), "Escolha uma cor",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "O Titulo não pode estar em branco",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnImgPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), IconPickerActivity.class);
                startActivityForResult(intent, IDENTIFY_INTEGER);
            }
        });
        return v;
    }
}
