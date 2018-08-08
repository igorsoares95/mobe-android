package com.example.guilherme.mobe.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.guilherme.mobe.R;

import java.util.ArrayList;

/**
 * Created by igorsoares on 13/07/2018.
 */

public class VeiculoAdapter extends ArrayAdapter<Veiculo> {

    private final Context context;
    private final ArrayList<Veiculo> elementos;

    public VeiculoAdapter(Context context, ArrayList<Veiculo> elementos) {

        super(context, R.layout.linha_list_view_veiculo,elementos);
        this.context = context;
        this.elementos = elementos;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView= inflater.inflate(R.layout.linha_list_view_veiculo, parent, false);

        TextView txtModelo = (TextView) rowView.findViewById(R.id.txt_modelo_lista_veiculo);
        TextView txtPlaca = (TextView) rowView.findViewById(R.id.txt_placa_lista_veiculo);
        TextView txtKm = (TextView) rowView.findViewById(R.id.txt_km_atual_lista_veiculo);
        TextView txtDispositivo = (TextView) rowView.findViewById(R.id.txt_dispositivo_lista_veiculo);

        txtModelo.setText(elementos.get(position).getModelo());
        txtPlaca.setText(elementos.get(position).getPlaca());
        txtKm.setText(elementos.get(position).getKm());
        txtDispositivo.setText(elementos.get(position).getDispositivo());

        return rowView;

    }



}
