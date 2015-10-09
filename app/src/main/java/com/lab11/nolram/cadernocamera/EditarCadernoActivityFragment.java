package com.lab11.nolram.cadernocamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lab11.nolram.database.Database;
import com.lab11.nolram.database.controller.CadernoDataSource;
import com.lab11.nolram.database.model.Caderno;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditarCadernoActivityFragment extends Fragment {

    public static final int IDENTIFY_INTEGER = 11;
    private int cor_principal;
    private int cor_secundaria;
    private Button btnEditar;
    private ImageButton btnImgPicker;
    private EditText edtDescricao;
    private EditText edtTitulo;
    private RadioGroup radioGroupCor;
    private RadioButton radioCor;
    private Toolbar toolbar;
    private Caderno caderno;
    private CadernoDataSource cadernoDataSource;
    private int icon;
    private long id_caderno;

    public EditarCadernoActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(cor_principal);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(cor_secundaria);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (IDENTIFY_INTEGER): {
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

    public String[] escolher_cor(int id) {
        String[] escolhas = new String[2];
        switch (id) {
            case R.id.rd_alizarin:
                escolhas[0] = getResources().getResourceName(R.color.flatui_alizarin);
                escolhas[1] = getResources().getResourceName(R.color.flatui_pomegranate);
                break;
            case R.id.rd_amethyst:
                escolhas[0] = getResources().getResourceName(R.color.flatui_amethyst);
                escolhas[1] = getResources().getResourceName(R.color.flatui_wisteria);
                break;
            case R.id.rd_carrot:
                escolhas[0] = getResources().getResourceName(R.color.flatui_carrot);
                escolhas[1] = getResources().getResourceName(R.color.flatui_pumpkin);
                break;
            case R.id.rd_pink:
                escolhas[0] = getResources().getResourceName(R.color.flatui_pink_weak);
                escolhas[1] = getResources().getResourceName(R.color.flatui_pink_strong);
                break;
            case R.id.rd_concrete:
                escolhas[0] = getResources().getResourceName(R.color.flatui_concrete);
                escolhas[1] = getResources().getResourceName(R.color.flatui_asbestos);
                break;
            case R.id.rd_emerald:
                escolhas[0] = getResources().getResourceName(R.color.flatui_emerald);
                escolhas[1] = getResources().getResourceName(R.color.flatui_nephritis);
                break;
            case R.id.rd_peter_river:
                escolhas[0] = getResources().getResourceName(R.color.flatui_peter_river);
                escolhas[1] = getResources().getResourceName(R.color.flatui_belize_hole);
                break;
            case R.id.rd_sun_flower:
                escolhas[0] = getResources().getResourceName(R.color.flatui_sun_flower);
                escolhas[1] = getResources().getResourceName(R.color.flatui_orange);
                break;
            case R.id.rd_turquoise:
                escolhas[0] = getResources().getResourceName(R.color.flatui_turquoise);
                escolhas[1] = getResources().getResourceName(R.color.flatui_green_sea);
                break;
            case R.id.rd_black:
                escolhas[0] = getResources().getResourceName(R.color.cinza);
                escolhas[1] = getResources().getResourceName(R.color.cinza_escuro);
                break;
            default:
                escolhas[0] = getResources().getResourceName(R.color.flatui_wet_asphalt);
                escolhas[1] = getResources().getResourceName(R.color.flatui_midnight_blue);
                break;

        }
        return escolhas;
    }

    public int getOptionButton(int id) {
        int idElement;
        switch (id) {
            case R.color.flatui_alizarin:
                idElement = R.id.rd_alizarin;
                break;
            case R.color.flatui_amethyst:
                idElement = R.id.rd_amethyst;
                break;
            case R.color.flatui_carrot:
                idElement = R.id.rd_carrot;
                break;
            case R.color.flatui_pink_weak:
                idElement = R.id.rd_pink;
                break;
            case R.color.flatui_concrete:
                idElement = R.id.rd_concrete;
                break;
            case R.color.flatui_emerald:
                idElement = R.id.rd_emerald;
                break;
            case R.color.flatui_peter_river:
                idElement = R.id.rd_peter_river;
                break;
            case R.color.flatui_sun_flower:
                idElement = R.id.rd_sun_flower;
                break;
            case R.color.flatui_turquoise:
                idElement = R.id.rd_turquoise;
                break;
            case R.color.cinza:
                idElement = R.id.rd_black;
                break;
            default:
                idElement = R.id.rd_wet_asphalt;
                break;

        }
        return idElement;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_editar_caderno, container, false);
        icon = R.drawable.book_2;
        radioGroupCor = (RadioGroup) v.findViewById(R.id.rd_cor);

        btnEditar = (Button) v.findViewById(R.id.btn_editar_caderno);
        edtDescricao = (EditText) v.findViewById(R.id.edtxt_descricao);
        edtTitulo = (EditText) v.findViewById(R.id.edtxt_titulo);
        btnImgPicker = (ImageButton) v.findViewById(R.id.btn_img_picker);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);

        cadernoDataSource = new CadernoDataSource(v.getContext());
        cadernoDataSource.open();

        Bundle bundle = getActivity().getIntent().getExtras();

        id_caderno = bundle.getLong(Database.CADERNO_ID);
        cor_principal = bundle.getInt(Database.CADERNO_COR_PRINCIPAL);
        cor_secundaria = bundle.getInt(Database.CADERNO_COR_SECUNDARIA);

        Caderno caderno = cadernoDataSource.getCaderno(id_caderno);

        edtDescricao.setText(caderno.getDescricao());
        edtTitulo.setText(caderno.getTitulo());

        int id_badge = getResources().getIdentifier(caderno.getBadge(),
                "drawable", getActivity().getPackageName());

        btnImgPicker.setImageResource(id_badge);

        int id_opcao_cor = getResources().getIdentifier(caderno.getCorPrincipal(),
                "drawable", getActivity().getPackageName());

        RadioButton b = (RadioButton) v.findViewById(getOptionButton(id_opcao_cor));
        b.setChecked(true);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = edtTitulo.getText().toString();
                String descricao = edtDescricao.getText().toString();
                int selectedId = radioGroupCor.getCheckedRadioButtonId();
                String[] cor = escolher_cor(selectedId);
                if (!titulo.isEmpty() && selectedId != -1) {
                    String badge = getResources().getResourceName(icon);
                    cadernoDataSource.updateCaderno(titulo, descricao, cor, badge, id_caderno);
                    Bundle b = new Bundle();
                    b.putString(Database.CADERNO_TITULO, titulo);
                    b.putString(Database.CADERNO_COR_PRINCIPAL, cor[0]);
                    b.putString(Database.CADERNO_COR_SECUNDARIA, cor[1]);

                    Intent i = getActivity().getIntent();
                    i.putExtras(b);
                    getActivity().setResult(Activity.RESULT_OK, i);
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
