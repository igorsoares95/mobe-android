package com.example.guilherme.mobe.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.helper.MaskEditUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ManutencaoAtrasadaAdapter extends ArrayAdapter<ManutencaoAtrasada> {

    private final Context context;
    private final ArrayList<ManutencaoAtrasada> elementos;

    public ManutencaoAtrasadaAdapter(Context context, ArrayList<ManutencaoAtrasada> elementos) {

        super(context, R.layout.linha_list_view_mostra_manutencoes_atrasadas_do_usuario,elementos);
        this.context = context;
        this.elementos = elementos;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView= inflater.inflate(R.layout.linha_list_view_mostra_manutencoes_atrasadas_do_usuario, parent, false);

        TextView txt_descricao = (TextView) rowView.findViewById(R.id.txt_descricao_list_view_manutencoes_atrasadas_do_usuario);
        TextView txt_veiculo = (TextView) rowView.findViewById(R.id.txt_veiculo_list_view_manutencoes_atrasadas_do_usuario);
        TextView txt_placa = (TextView) rowView.findViewById(R.id.txt_placa_list_view_manutencoes_atrasadas_do_usuario);
        TextView txt_km_atual = (TextView) rowView.findViewById(R.id.txt_km_atual_list_view_manutencoes_atrasadas_do_usuario);
        TextView txt_km_proxima_manutencao = (TextView) rowView.findViewById(R.id.txt_km_proxima_manutencao_list_view_manutencoes_atrasadas_do_usuario);
        TextView txt_data_proxima_manutencao = (TextView) rowView.findViewById(R.id.txt_data_proxima_manutencao_list_view_manutencoes_atrasadas_do_usuario);


        txt_descricao.setText(elementos.get(position).getDescricao());
        txt_veiculo.setText(elementos.get(position).getVeiculo());
        txt_placa.setText(elementos.get(position).getPlaca());
        txt_km_atual.setText(elementos.get(position).getKm_atual());
        txt_km_proxima_manutencao.setText(elementos.get(position).getKm_proxima_manutencao());
        txt_data_proxima_manutencao.setText(MaskEditUtil.formatarData(elementos.get(position).getData_proxima_manutencao(),"yyyy-MM-dd","dd/MM/yyyy"));

        return rowView;

    }
}
