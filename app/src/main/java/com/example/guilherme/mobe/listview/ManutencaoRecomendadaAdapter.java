package com.example.guilherme.mobe.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.guilherme.mobe.R;

import java.util.ArrayList;

public class ManutencaoRecomendadaAdapter extends ArrayAdapter<ManutencaoRecomendada> {

    public final ArrayList<ManutencaoRecomendada> lista_manutencoes_recomendadas;
    private final Context context;

    public ManutencaoRecomendadaAdapter(Context context, ArrayList<ManutencaoRecomendada> lista_manutencoes_recomendadas) {

        super(context, R.layout.linha_list_view_mostra_manutencoes_recomendadas_do_veiculo, lista_manutencoes_recomendadas);
        this.context = context;
        this.lista_manutencoes_recomendadas = lista_manutencoes_recomendadas;
    }

    public View getView(int position, View convertView, final ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.linha_list_view_mostra_manutencoes_recomendadas_do_veiculo, parent, false);

        CheckBox cb_descricao_manutencao = (CheckBox) rowView.findViewById(R.id.cb_descricao_mostra_manutencoes_recomendadas_do_veiculo);

        cb_descricao_manutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view;
                ManutencaoRecomendada manutencao_selecionada = (ManutencaoRecomendada) cb.getTag();
                Toast.makeText(getContext(), manutencao_selecionada.getDescricao(), Toast.LENGTH_SHORT).show();
                manutencao_selecionada.setSelecionado(cb.isChecked());
            }
        });

        ManutencaoRecomendada manutencao_da_linha = lista_manutencoes_recomendadas.get(position);
        cb_descricao_manutencao.setText(manutencao_da_linha.getDescricao());
        cb_descricao_manutencao.setChecked(manutencao_da_linha.isSelecionado());
        cb_descricao_manutencao.setTag(manutencao_da_linha);

        return rowView;


    }
}
