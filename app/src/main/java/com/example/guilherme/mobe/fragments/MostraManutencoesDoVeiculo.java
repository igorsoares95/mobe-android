package com.example.guilherme.mobe.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.listview.Manutencao;
import com.example.guilherme.mobe.listview.ManutencaoAdapter;
import com.google.android.gms.games.GamesMetadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostraManutencoesDoVeiculo extends Fragment {

    private static final String TAG = MostraManutencoesDoVeiculo.class.getSimpleName();
    private ListView lista;

    public MostraManutencoesDoVeiculo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_mostra_manutencoes_do_veiculo, container, false);

        getActivity().setTitle("Minhas manutenções");

        Bundle dados_do_veiculo = getArguments();

        String placa_veiculo = dados_do_veiculo.getString("placa_veiculo");

        lista = (ListView) view.findViewById(R.id.list_view_mostra_manutencoes_do_veiculo);
        adicionaManutencoesNoListView(placa_veiculo);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //obter manutencao da listview
                Manutencao manutencao_selecionada = (Manutencao) parent.getItemAtPosition(position);

                //Enviar dados para proxima fragment
                Bundle dados_da_manutencao = new Bundle();
                dados_da_manutencao.putString("id_manutencao_do_veiculo", manutencao_selecionada.getId());
                dados_da_manutencao.putString("descricao",manutencao_selecionada.getDescricao());
                dados_da_manutencao.putString("limite_km",manutencao_selecionada.getLimite_km());
                dados_da_manutencao.putString("limite_tempo_meses",manutencao_selecionada.getLimite_tempo_meses());
                dados_da_manutencao.putString("km_antecipacao",manutencao_selecionada.getKm_antecipacao());
                dados_da_manutencao.putString("tempo_antecipacao_meses",manutencao_selecionada.getTempo_antecipacao_meses());
                dados_da_manutencao.putString("data_ultima_manutencao",manutencao_selecionada.getData_ultima_manutencao());
                dados_da_manutencao.putString("km_ultima_manutencao",manutencao_selecionada.getKm_ultima_manutencao());



                DetalhesManutencaoDoVeiculoFragment detalhes_manutencao_do_veiculo = new DetalhesManutencaoDoVeiculoFragment();
                detalhes_manutencao_do_veiculo.setArguments(dados_da_manutencao);
                //------------------------------------------------------------------------------------

                getFragmentManager().beginTransaction().replace(R.id.frame_container, detalhes_manutencao_do_veiculo).addToBackStack(null).commit();

            }
        });



        return view;
    }

    private void adicionaManutencoesNoListView(final String placa_veiculo_do_usuario) {

        final ArrayList<Manutencao> manutencoes = new ArrayList<Manutencao>();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_OBTER_MANUTENCOES_DO_VEICULO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "obter manutencoes Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        JSONArray manutencoesJSON = object.getJSONArray("manutencoes");


                        for (int i = 0; i < manutencoesJSON.length(); i++) {

                            JSONObject manutencaoJSON = (JSONObject) manutencoesJSON.get(i);

                            int id = manutencaoJSON.getInt("id");
                            String descricao =  manutencaoJSON.getString("descricao");
                            int limite_km = manutencaoJSON.getInt("limite_km");
                            int limite_tempo_meses =  manutencaoJSON.getInt("limite_tempo_meses");
                            int km_antecipacao = manutencaoJSON.getInt("km_antecipacao");
                            int tempo_antecipacao_meses = manutencaoJSON.getInt("tempo_antecipacao_meses");
                            String data_ultima_manutencao = (String) manutencaoJSON.get("data_ultima_manutencao");
                            int km_ultima_manutencao = manutencaoJSON.getInt("km_ultima_manutencao");


                            manutencoes.add(new Manutencao(String.valueOf(id), descricao, String.valueOf(limite_km), String.valueOf(limite_tempo_meses), String.valueOf(km_antecipacao), String.valueOf(tempo_antecipacao_meses), data_ultima_manutencao, String.valueOf(km_ultima_manutencao)));

                        }

                        ArrayAdapter adapter = new ManutencaoAdapter(getActivity(),manutencoes);
                        lista.setAdapter(adapter);

                    } else {

                        String errorMsg = object.getString("error_msg");
                        Log.i(TAG, errorMsg);
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();

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
                Log.e(TAG, "obter manutencoes Error: " + error.getMessage());

                //Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity().getApplicationContext(), "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("placa_veiculo_do_usuario",placa_veiculo_do_usuario);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }


}
