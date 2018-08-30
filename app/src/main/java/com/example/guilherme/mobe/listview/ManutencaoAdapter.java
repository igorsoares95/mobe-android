package com.example.guilherme.mobe.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.guilherme.mobe.R;

import java.util.ArrayList;

public class ManutencaoAdapter extends ArrayAdapter<Manutencao> {

    private final Context context;
    private final ArrayList<Manutencao> elementos;

    public ManutencaoAdapter(Context context, ArrayList<Manutencao> elementos) {

        super(context, R.layout.linha_list_view_mostra_manutencoes_do_veiculo,elementos);
        this.context = context;
        this.elementos = elementos;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView= inflater.inflate(R.layout.linha_list_view_mostra_manutencoes_do_veiculo, parent, false);


        TextView txt_descricao = (TextView) rowView.findViewById(R.id.lbl_descricao_manutencao_linha_list_view_mostra_manutencoes_do_veiculo);

        /*
        //Formatar data para dd/MM/yyyy
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String data_proxima_manutencao_com_formatacao = "";
        try {
            Date data_proxima_manutencao_sem_formatacao = sdf.parse(elementos.get(position).getData_proxima_manutencao());
            data_proxima_manutencao_com_formatacao = new SimpleDateFormat("dd/MM/yyyy").format(data_proxima_manutencao_sem_formatacao);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        */

        txt_descricao.setText(elementos.get(position).getDescricao());

        return rowView;

    }
}

