package com.example.guilherme.mobe.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guilherme.mobe.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostraManutencoesProximasDoUsuario extends Fragment {


    public MostraManutencoesProximasDoUsuario() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mostra_manutencoes_proximas_do_usuario, container, false);
    }

}