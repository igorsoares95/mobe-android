package com.example.guilherme.mobe.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.guilherme.mobe.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class informacoesManutencoesFragment extends Fragment {

    private static final String TAG = informacoesManutencoesFragment.class.getSimpleName();
    private TextView txtDescricao;
    private TextView txtKmLimite;
    private TextView txtTempoLimite;
    private EditText txtKmNotificacao;
    private EditText txtTempoNotificacao;
    private EditText txtDataUltimaManutencao;
    private EditText txtKmUltimaManutencao;
    private Button btnFinalizar;
    private String id_manutencao;
    private String descricao;
    private String limiteKm;
    private String limiteTempo;


    public informacoesManutencoesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_informacoes_manutencoes, container, false);

        //Obter dados do fragment anterior
        Bundle dados_da_manutencao = getArguments();
        id_manutencao = dados_da_manutencao.getString("id_manutencao");
        descricao = dados_da_manutencao.getString("descricao");
        limiteKm = dados_da_manutencao.getString("limiteKm");
        limiteTempo = dados_da_manutencao.getString("limiteTempo");
        //----------------------------------------------------------------

        txtDescricao = (TextView) view.findViewById(R.id.txtDescricao_infoManutencoes);
        txtKmLimite = (TextView) view.findViewById(R.id.txtkmLimite_infoManutencoes);
        txtTempoLimite = (TextView) view.findViewById(R.id.txttempoLimite_infoManutencoes);
        txtKmNotificacao = (EditText) view.findViewById(R.id.txtkmAntecipacao_infoManutencoes);
        txtTempoNotificacao = (EditText) view.findViewById(R.id.txttempoAntecipacao_infoManutencoes);
        txtDataUltimaManutencao = (EditText) view.findViewById(R.id.txtdtUltimaManutencao_infoManutencoes);
        txtKmUltimaManutencao = (EditText) view.findViewById(R.id.txtkmUltimaManutencao_infoManutencoes);


        return view;
    }

}
