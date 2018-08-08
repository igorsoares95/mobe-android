package com.example.guilherme.mobe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.listview.ManutencaoAtrasadaOuProxima;
import com.example.guilherme.mobe.listview.ManutencaoAtrasadaOuProximaAdapter;
import com.example.guilherme.mobe.listview.VeiculoAdapter;
import com.example.guilherme.mobe.service.MyFirebaseMessagingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClickNotificationMostrarManutencoes extends AppCompatActivity {

    private static final String TAG = ClickNotificationMostrarManutencoes.class.getSimpleName();
    String payload_string;
    ListView list_view_click_notification;
    Button btn_abrir_pagina_inicial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_notification_mostrar_manutencoes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        list_view_click_notification = (ListView) findViewById(R.id.list_view_click_notification);
      //  btn_abrir_pagina_inicial = findViewById(R.id.btn_abrir_pagina_inicial);

        setSupportActionBar(toolbar);



        Intent intent = getIntent();
        payload_string = intent.getStringExtra("payload");
        adicionaManutencoesAtrasadasEProximasNoListView(payload_string);


        /*
        btn_abrir_pagina_inicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        */


    }


    private void adicionaManutencoesAtrasadasEProximasNoListView(String payload_string) {


        final ArrayList<ManutencaoAtrasadaOuProxima> manutencoes_atrasadas_ou_proximas = new ArrayList<ManutencaoAtrasadaOuProxima>();

        try {

            JSONArray manutencoes = new JSONArray(payload_string);

            for (int i = 0; i < manutencoes.length(); i++) {

                JSONObject manutencao = (JSONObject) manutencoes.get(i);

                String modelo_veiculo = (String) manutencao.get("modelo_veiculo");
                String placa = (String) manutencao.get("placa");
                String km_atual = manutencao.getString("km_atual");
                String descricao = manutencao.getString("descricao");
                String km_ultima_manutencao = manutencao.getString("km_ultima_manutencao");
                String km_proxima_manutencao = manutencao.getString("km_proxima_manutencao");
                String data_ultima_manutencao = manutencao.getString("data_ultima_manutencao");
                String data_proxima_manutencao = manutencao.getString("data_proxima_manutencao");
                String status = manutencao.getString("status");

                manutencoes_atrasadas_ou_proximas.add(new ManutencaoAtrasadaOuProxima(modelo_veiculo,placa,km_atual, descricao, km_ultima_manutencao, km_proxima_manutencao, data_ultima_manutencao, data_proxima_manutencao, status));

            }

            ArrayAdapter adapter = new ManutencaoAtrasadaOuProximaAdapter(ClickNotificationMostrarManutencoes.this,manutencoes_atrasadas_ou_proximas);
            list_view_click_notification.setAdapter(adapter);



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
