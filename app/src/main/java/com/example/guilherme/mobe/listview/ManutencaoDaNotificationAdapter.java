package com.example.guilherme.mobe.listview;

import android.content.Context;
import android.graphics.Color;
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

public class ManutencaoDaNotificationAdapter extends ArrayAdapter<ManutencaoDaNotification> {

    private final Context context;
    private final ArrayList<ManutencaoDaNotification> elementos;

    public ManutencaoDaNotificationAdapter(Context context, ArrayList<ManutencaoDaNotification> elementos) {

        super(context, R.layout.linha_list_view_manutencoes_atrasadas_proximas_clicknotification,elementos);
        this.context = context;
        this.elementos = elementos;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView= inflater.inflate(R.layout.linha_list_view_manutencoes_atrasadas_proximas_clicknotification, parent, false);

        TextView txtModelo = (TextView) rowView.findViewById(R.id.txt_modelo_clicknotification);
        TextView txtPlaca = (TextView) rowView.findViewById(R.id.txt_placa_clicknotification);
        TextView txtKmAtual = (TextView) rowView.findViewById(R.id.txt_km_atual_clicknotification);
        TextView txtDescricaoManutencao = (TextView) rowView.findViewById(R.id.txt_manutencao_clicknotification);
        TextView txtKmUltimaManutencao = (TextView) rowView.findViewById(R.id.txt_km_ultima_manutencao_clicknotification);
        TextView txtKmProximaManutencao = (TextView) rowView.findViewById(R.id.txt_km_proxima_manutencao_clicknotification);
        TextView txtDataUltimaManutencao = (TextView) rowView.findViewById(R.id.txt_data_ultima_manutencao_clicknotification);
        TextView txtDataProximaManutencao = (TextView) rowView.findViewById(R.id.txt_data_proxima_manutencao_clicknotification);

        TextView lblDescricaoManutencao = (TextView) rowView.findViewById(R.id.lbl_manutencao_clicknotification);


        /*
        //Formatar data para dd/MM/yyyy
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String data_ultima_manutencao_com_formatacao = "";
        String data_proxima_manutencao_com_formatacao = "";
        try {
            Date data_ultima_manutencao_sem_formatacao = sdf.parse(elementos.get(position).getData_ultima_manutencao());
            Date data_proxima_manutencao_sem_formatacao = sdf.parse(elementos.get(position).getData_proxima_manutencao());
            data_ultima_manutencao_com_formatacao = new SimpleDateFormat("dd/MM/yyyy").format(data_ultima_manutencao_sem_formatacao);
            data_proxima_manutencao_com_formatacao = new SimpleDateFormat("dd/MM/yyyy").format(data_proxima_manutencao_sem_formatacao);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        */

        txtModelo.setText(elementos.get(position).getModelo());
        txtPlaca.setText(elementos.get(position).getPlaca());
        txtKmAtual.setText(elementos.get(position).getKm_atual());
        txtDescricaoManutencao.setText(elementos.get(position).getDescricao_manutencao());
        txtKmUltimaManutencao.setText(elementos.get(position).getKm_ultima_manutencao());
        txtKmProximaManutencao.setText(elementos.get(position).getKm_proxima_manutencao());
        txtDataUltimaManutencao.setText(MaskEditUtil.formatarData(elementos.get(position).getData_ultima_manutencao(),"yyyy-MM-dd","dd/MM/yyyy"));
        txtDataProximaManutencao.setText(MaskEditUtil.formatarData(elementos.get(position).getData_proxima_manutencao(),"yyyy-MM-dd","dd/MM/yyyy"));

        if(elementos.get(position).getStatus().equals("atrasada")) {

            txtDescricaoManutencao.setTextColor(Color.RED);
            lblDescricaoManutencao.setTextColor(Color.RED);

        } else if(elementos.get(position).getStatus().equals("proxima")) {

            txtDescricaoManutencao.setTextColor(Color.YELLOW);
            lblDescricaoManutencao.setTextColor(Color.YELLOW);
        }


        return rowView;

    }
}
