package com.lab11.nolram.cadernocamera;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lab11.nolram.database.controller.CadernoDataSource;


/**
 * A placeholder fragment containing a simple view.
 */
public class CriarCadernoActivityFragment extends Fragment{

    private Button btnCriar;
    private EditText edtDescricao;
    private EditText edtTitulo;

    private CadernoDataSource cadernoDataSource;

    public CriarCadernoActivityFragment() {
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
        View v = inflater.inflate(R.layout.fragment_criar_caderno, container, false);
        btnCriar = (Button) v.findViewById(R.id.btn_criar_caderno);
        edtDescricao = (EditText) v.findViewById(R.id.edtxt_descricao);
        edtTitulo = (EditText) v.findViewById(R.id.edtxt_titulo);

        cadernoDataSource = new CadernoDataSource(v.getContext());
        cadernoDataSource.open();

        btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = edtTitulo.getText().toString();
                String descricao = edtDescricao.getText().toString();
                if (!titulo.isEmpty()) {
                    cadernoDataSource.criarCaderno(titulo, descricao);
                    getActivity().finish();
                } else {
                    Toast.makeText(v.getContext(), "O Titulo não pode estar em branco",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }
}
