package com.example.guilherme.mobe.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.activity.AdicionarVeiculoActivity;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.helper.SQLiteHandler;
import com.example.guilherme.mobe.listview.ManutencaoAtrasada;
import com.example.guilherme.mobe.listview.ManutencaoAtrasadaAdapter;
import com.example.guilherme.mobe.listview.ManutencaoProxima;
import com.example.guilherme.mobe.listview.ManutencaoProximaAdapter;
import com.example.guilherme.mobe.listview.Veiculo;
import com.example.guilherme.mobe.listview.VeiculoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class InicioFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Button btn_meus_veiculos, btn_registrar_veiculo, btn_manutencoes_atrasadas, btn_manutencoes_proximas;
    private TextView lbl_saudacao, lbl_nome_usuario, lbl_qtd_manutencoes_atrasadas, lbl_qtd_manutencoes_proximas, lbl_descricao_veiculos;
    private SQLiteHandler bd;
    private static final String TAG = InicioFragment.class.getSimpleName();
    SwipeRefreshLayout swipeLayout;



    public InicioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        //teste

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_fragment_inicio);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //fim teste

        getActivity().setTitle("Início");

        btn_meus_veiculos = (Button) view.findViewById(R.id.btn_meus_veiculos_fragment_inicio);
        btn_registrar_veiculo = (Button) view.findViewById(R.id.btn_registrar_veiculo_fragment_inicio);
        btn_manutencoes_atrasadas = (Button) view.findViewById(R.id.btn_ver_manutencoes_atrasadas_fragment_inicio);
        btn_manutencoes_proximas = (Button) view.findViewById(R.id.btn_ver_manutencoes_proximas_fragment_inicio);
        lbl_saudacao = (TextView) view.findViewById(R.id.lbl_saudacao_fragment_inicio);
        lbl_nome_usuario = (TextView) view.findViewById(R.id.lbl_nome_usuario_fragment_inicio);
        lbl_qtd_manutencoes_atrasadas = (TextView) view.findViewById(R.id.lbl_qtd_manutencoes_atrasadas_fragment_inicio);
        lbl_qtd_manutencoes_proximas = (TextView) view.findViewById(R.id.lbl_qtd_manutencoes_proximas_fragment_inicio);
        lbl_descricao_veiculos = (TextView) view.findViewById(R.id.lbl_descricao_veiculos_fragment_inicio);

        mostraSaudacao();
        mostraNomeUsuario();
        mostraQtdDeVeiculos(obtemIdUsuarioSqLite());
        mostraQtdManutencoesAtrasadas(obtemIdUsuarioSqLite());
        mostraQtdManutencoesProximas(obtemIdUsuarioSqLite());

        btn_meus_veiculos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, new ListaVeiculosFragment())
                        .addToBackStack(null).commit();
            }
        });

        btn_manutencoes_atrasadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, new MostraManutencoesAtrasadasDoUsuarioFragment())
                        .addToBackStack(null).commit();
            }
        });

        btn_manutencoes_proximas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, new MostraManutencoesProximasDoUsuarioFragment())
                        .addToBackStack(null).commit();
            }
        });

        btn_registrar_veiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AdicionarVeiculoActivity.class);
                startActivity(intent);
            }
        });

        return view;


    }

    //Esse método é responsável por atualizar a tela quando clicar no SwipeRefreshLayout
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(false);
                mostraSaudacao();
                mostraNomeUsuario();
                mostraQtdDeVeiculos(obtemIdUsuarioSqLite());
                mostraQtdManutencoesAtrasadas(obtemIdUsuarioSqLite());
                mostraQtdManutencoesProximas(obtemIdUsuarioSqLite());
            }
        }, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        mostraSaudacao();
        mostraNomeUsuario();
        mostraQtdDeVeiculos(obtemIdUsuarioSqLite());
        mostraQtdManutencoesAtrasadas(obtemIdUsuarioSqLite());
        mostraQtdManutencoesProximas(obtemIdUsuarioSqLite());

    }

    private void mostraNomeUsuario() {
        bd = new SQLiteHandler(this.getActivity());
        HashMap<String, String> usuario = bd.getUserDetails();
        String nome_usuario = usuario.get("S_NOME");
        lbl_nome_usuario.setText(nome_usuario);
    }

    private void mostraSaudacao() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);

        if(hour >= 6 && hour <= 12) {
            lbl_saudacao.setText("Bom dia,");
        }
        else if(hour < 18) {
            lbl_saudacao.setText("Boa tarde,");
        }
        else {
            lbl_saudacao.setText("Boa noite,");
        }
    }

    private String obtemIdUsuarioSqLite() {
        bd = new SQLiteHandler(this.getActivity());
        HashMap<String, String> usuario = bd.getUserDetails();
        String id_usuario = usuario.get("ID_USUARIO");
        return id_usuario;
    }

    private void mostraQtdDeVeiculos(final String id_usuario) {

        final ArrayList<Veiculo> veiculos = new ArrayList<Veiculo>();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_OBTER_VEICULOS_POR_USUARIO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "obter veiculos Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        JSONArray veiculosJSON = object.getJSONArray("veiculos");

                        for (int i = 0; i < veiculosJSON.length(); i++) {

                            JSONObject veiculoJSON = (JSONObject) veiculosJSON.get(i);

                            String modelo = (String) veiculoJSON.get("modelo");
                            String placa = (String) veiculoJSON.get("placa");
                            double km = veiculoJSON.getDouble("km");
                            String dispositivo = (String) veiculoJSON.get("codigo_dispositivo");
                            veiculos.add(new Veiculo(modelo,placa,String.valueOf(km),dispositivo));

                        }

                        if(veiculos.size() == 1) {
                            lbl_descricao_veiculos.setText("Você possui " + veiculos.size() + " veículo registrado");
                        } else {
                            lbl_descricao_veiculos.setText("Você possui " + veiculos.size() + " veículos registrados");
                        }


                    } else {
                        lbl_descricao_veiculos.setText("Você não possui veículos registrados");
                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "obter veiculos Error: " + error.getMessage());

                //Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity().getApplicationContext(), "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_usuario",id_usuario);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private void mostraQtdManutencoesAtrasadas(final String id_usuario) {

        final ArrayList<ManutencaoAtrasada> manutencoes_atrasadas = new ArrayList<ManutencaoAtrasada>();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_OBTER_MANUTENCOES_ATRASADAS_DO_USUARIO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Obter manutencoes atrasadas Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        JSONArray manutencoes_atrasadasJSON = object.getJSONArray("manutencoes_atrasadas");

                        for (int i = 0; i < manutencoes_atrasadasJSON.length(); i++) {

                            JSONObject manutencao_atrasadaJSON = (JSONObject) manutencoes_atrasadasJSON.get(i);

                            int id_manutencao_do_veiculo =  manutencao_atrasadaJSON.getInt("id_manutencao_do_veiculo");
                            String modelo_veiculo = (String) manutencao_atrasadaJSON.get("modelo_veiculo");
                            String placa = (String) manutencao_atrasadaJSON.get("placa");
                            double km_atual = manutencao_atrasadaJSON.getDouble("km_atual");
                            String descricao = (String) manutencao_atrasadaJSON.get("descricao");
                            double km_proxima_manutencao = manutencao_atrasadaJSON.getDouble("km_proxima_manutencao");
                            String data_proxima_manutencao = (String) manutencao_atrasadaJSON.get("data_proxima_manutencao");

                            manutencoes_atrasadas.add(new ManutencaoAtrasada(String.valueOf(id_manutencao_do_veiculo), descricao, modelo_veiculo, placa, String.valueOf(km_atual), String.valueOf(km_proxima_manutencao), data_proxima_manutencao));

                        }

                        if(manutencoes_atrasadas.size() == 1) {
                            lbl_qtd_manutencoes_atrasadas.setText("Você possui " + manutencoes_atrasadas.size() + " manutenção atrasada");
                        } else {
                            lbl_qtd_manutencoes_atrasadas.setText("Você possui " + manutencoes_atrasadas.size() + " manutenções atrasadas");
                        }


                    } else {
                        lbl_qtd_manutencoes_atrasadas.setText("Você não possui manutenções atrasadas");
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Obter manutencoes atrasadas Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_usuario",id_usuario);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private void mostraQtdManutencoesProximas(final String id_usuario) {

        final ArrayList<ManutencaoProxima> manutencoes_proximas = new ArrayList<ManutencaoProxima>();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_OBTER_MANUTENCOES_PROXIMAS_DO_USUARIO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Obter manutencoes proximas Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        JSONArray manutencoes_proximasJSON = object.getJSONArray("manutencoes_proximas");

                        for (int i = 0; i < manutencoes_proximasJSON.length(); i++) {

                            JSONObject manutencao_proximaJSON = (JSONObject) manutencoes_proximasJSON.get(i);

                            int id_manutencao_do_veiculo = manutencao_proximaJSON.getInt("id_manutencao_do_veiculo");
                            String modelo_veiculo = (String) manutencao_proximaJSON.get("modelo_veiculo");
                            String placa = (String) manutencao_proximaJSON.get("placa");
                            double km_atual = manutencao_proximaJSON.getDouble("km_atual");
                            String descricao = (String) manutencao_proximaJSON.get("descricao");
                            double km_proxima_manutencao = manutencao_proximaJSON.getDouble("km_proxima_manutencao");
                            String data_proxima_manutencao = (String) manutencao_proximaJSON.get("data_proxima_manutencao");

                            manutencoes_proximas.add(new ManutencaoProxima(String.valueOf(id_manutencao_do_veiculo),descricao, modelo_veiculo, placa, String.valueOf(km_atual), String.valueOf(km_proxima_manutencao), data_proxima_manutencao));

                        }

                        if(manutencoes_proximas.size() == 1) {
                            lbl_qtd_manutencoes_proximas.setText("Você possui " + manutencoes_proximas.size() + " manutenção atrasada");
                        } else {
                            lbl_qtd_manutencoes_proximas.setText("Você possui " + manutencoes_proximas.size() + " manutenções atrasadas");
                        }


                    } else {
                        lbl_qtd_manutencoes_proximas.setText("Você não possui manutenções próximas");
                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Obter manutencoes atrasadas Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_usuario",id_usuario);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }




}
