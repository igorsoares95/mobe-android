package com.example.guilherme.mobe.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guilherme.mobe.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class informacoesManutencoesFragment extends Fragment {


    public informacoesManutencoesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_informacoes_manutencoes, container, false);
    }

}
