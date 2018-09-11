package com.example.guilherme.mobe.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.activity.AdicionarVeiculoActivity;
import com.example.guilherme.mobe.activity.MainActivity;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.listview.ManutencaoRecomendada;
import com.example.guilherme.mobe.listview.ManutencaoRecomendadaAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostraManutencoesRecomendadasDoVeiculo extends Fragment {

    private static final String TAG = MostraManutencoesRecomendadasDoVeiculo.class.getSimpleName();
    ListView lista;
    ManutencaoRecomendadaAdapter manutencao_recomendada_adapter = null;
    String modelo_veiculo, placa_veiculo, km_veiculo;
    TextView lbl_modelo_veiculo, lbl_placa_veiculo, lbl_km_veiculo;
    Button btn_proximo;
    ArrayList<ManutencaoRecomendada> primeiro_estado_das_manutencoes_recomendadas = new ArrayList<>();
    String nome_activity_atual;

    public MostraManutencoesRecomendadasDoVeiculo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mostra_manutencoes_recomendadas_do_veiculo,container,false);

        getActivity().setTitle("Manutenções recomendadas");

        nome_activity_atual = getActivity().getClass().getSimpleName();

        lbl_modelo_veiculo = (TextView) view.findViewById(R.id.lbl_modelo_mostra_manutencoes_recomendadas_do_veiculo);
        lbl_placa_veiculo = (TextView) view.findViewById(R.id.lbl_placa_mostra_manutencoes_recomendadas_do_veiculo);
        lbl_km_veiculo = (TextView) view.findViewById(R.id.lbl_km_mostra_manutencoes_recomendadas_do_veiculo);
        btn_proximo = (Button) view.findViewById(R.id.btn_proximo_mostra_manutencoes_recomendadas_do_veiculo);

        lista = (ListView) view.findViewById(R.id.list_view_manutencoes_recomendadas_do_veiculo);

        //Obter dados da fragment anterior
        Bundle dados_veiculo_adicionado = getArguments();
        modelo_veiculo = dados_veiculo_adicionado.getString("modelo_veiculo");
        placa_veiculo = dados_veiculo_adicionado.getString("placa_veiculo");
        km_veiculo = dados_veiculo_adicionado.getString("km_veiculo");

        lbl_modelo_veiculo.setText(modelo_veiculo);
        lbl_placa_veiculo.setText(placa_veiculo);
        lbl_km_veiculo.setText(km_veiculo);

        adicionaManutencoesRecomendadasNoListView(placa_veiculo);

        checkBtnProximoClick();

        return view;
    }


    //onBackPressed na fragment
    @Override
    public void onResume() {
        super.onResume();

        //Verifica qual é a acitivity atual parar assim tratar o botao onBackPressed
        if(nome_activity_atual.equals("AdicionarVeiculoActivity")) {

            if(getView() == null){
                return;
            }

            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                        // handle back button's click listener
                        Log.i("teste", "onBackPressed na fragment funcionou");
                        mostraAlertDialogDeCancelarCriacaoDaManutencaoRecomendada();
                        return true;
                    }
                    return false;
                }
            });
        }


    }

    private void checkBtnProximoClick() {

        final ArrayList<ManutencaoRecomendada> lista_manutencoes_selecionadas = new ArrayList<>();
        btn_proximo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<ManutencaoRecomendada> lista_manutencoes_recomendadas = manutencao_recomendada_adapter.lista_manutencoes_recomendadas;
                for(int i=0;i<lista_manutencoes_recomendadas.size();i++){

                    ManutencaoRecomendada manutencao_recomendada = lista_manutencoes_recomendadas.get(i);
                    ManutencaoRecomendada manutencao_recomendada_na_abertura_da_fragment = primeiro_estado_das_manutencoes_recomendadas.get(i);

                    if(manutencao_recomendada.isSelecionado() && !manutencao_recomendada_na_abertura_da_fragment.isSelecionado()){
                        responseText.append("\n" + manutencao_recomendada.getDescricao());
                        lista_manutencoes_selecionadas.add(manutencao_recomendada);

                    } else if(manutencao_recomendada_na_abertura_da_fragment.isSelecionado() && !manutencao_recomendada.isSelecionado()) {
                        //chamar o exclui manutencao
                        //Toast.makeText(getContext(), "chamar o exclui veiculo", Toast.LENGTH_LONG).show();
                        excluiManutencaoRecomendada(placa_veiculo, manutencao_recomendada.getId_manutencao_padrao());

                    }
                }

                Toast.makeText(getContext(), responseText, Toast.LENGTH_LONG).show();

                if(lista_manutencoes_selecionadas.size() > 0) {

                    //envia arraylist de manutencoes_selecionadas para a proxima fragment
                    Bundle manutencoes_selecionadas = new Bundle();
                    manutencoes_selecionadas.putSerializable("manutencoes_selecionadas", lista_manutencoes_selecionadas);
                    manutencoes_selecionadas.putString("modelo_veiculo",modelo_veiculo);
                    manutencoes_selecionadas.putString("km_veiculo",km_veiculo);
                    manutencoes_selecionadas.putString("placa_veiculo",placa_veiculo);

                    DetalhesManutencaoRecomendadaFragment detalhes_manutencao_recomendada_fragment = new DetalhesManutencaoRecomendadaFragment();
                    detalhes_manutencao_recomendada_fragment.setArguments(manutencoes_selecionadas);

                    //Verifica qual é a acitivity atual, para assim, abrir a fragment com o frame container correto
                    if(nome_activity_atual.equals("AdicionarVeiculoActivity")) {
                        getFragmentManager().beginTransaction().replace(R.id.frame_container_adicionar_veiculo, detalhes_manutencao_recomendada_fragment).addToBackStack(null).commit();
                    }
                    else if (nome_activity_atual.equals("MostraInfoVeiculoActivity")) {
                        getFragmentManager().beginTransaction().replace(R.id.frame_container_mostra_info_veiculo, detalhes_manutencao_recomendada_fragment).commit();
                    }

                } else {

                    getActivity().finish();

                }

            }
        });

    }

    private void adicionaManutencoesRecomendadasNoListView(final String placa_veiculo_do_usuario) {

        final ArrayList<ManutencaoRecomendada> lista_manutencoes_recomendadas = new ArrayList<ManutencaoRecomendada>();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_OBTER_MANUTENCOES_RECOMENDADAS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Obter manutencoes recomendadas Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        JSONArray manutencoesJSON = object.getJSONArray("manutencoes");

                        for (int i = 0; i < manutencoesJSON.length(); i++) {

                            JSONObject manutencaoJSON = (JSONObject) manutencoesJSON.get(i);

                            int id_manutencao_padrao = manutencaoJSON.getInt("id_manutencao_padrao");
                            String nome = (String) manutencaoJSON.get("nome");
                            int limite_km = manutencaoJSON.getInt("limite_km");
                            int limite_tempo_meses = manutencaoJSON.getInt("limite_tempo_meses");
                            boolean ja_cadastrado = manutencaoJSON.getBoolean("ja_cadastrado");
                            lista_manutencoes_recomendadas.add(new ManutencaoRecomendada(String.valueOf(id_manutencao_padrao),nome, String.valueOf(limite_km), String.valueOf(limite_tempo_meses),ja_cadastrado));
                            primeiro_estado_das_manutencoes_recomendadas.add(new ManutencaoRecomendada(String.valueOf(id_manutencao_padrao),nome, String.valueOf(limite_km), String.valueOf(limite_tempo_meses),ja_cadastrado));
                        }

                        manutencao_recomendada_adapter = new ManutencaoRecomendadaAdapter(getContext(), lista_manutencoes_recomendadas);
                        lista.setAdapter(manutencao_recomendada_adapter);


                    } else {

                        Toast.makeText(getActivity(), "Não foi encontrado manutenções recomendadas", Toast.LENGTH_SHORT).show();
                        //esconder o botao pois nao tem nenhuma manutencao recomendada
                        btn_proximo.setVisibility(View.GONE);
                        perguntaSeDesejaManutencaoPersonalizada();

                    }



                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("teste", "Obter manutencoes recomendadas Error: " + error.getMessage());
                Toast.makeText(getContext().getApplicationContext(),
                        "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
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

    private void excluiManutencaoRecomendada(final String placa_veiculo_do_usuario, final String id_manutencao_padrao) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EXCLUIR_MANUTENCAO_RECOMENDADA_VEICULO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("teste", "Excluir manutencao recomendada Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("teste", "Excluir manutencao recomendada Error: " + error.getMessage());
                Toast.makeText(getContext(),"Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("placa_veiculo_do_usuario",placa_veiculo_do_usuario);
                params.put("id_manutencao_padrao",id_manutencao_padrao);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    public void perguntaSeDesejaManutencaoPersonalizada() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(getContext());
        alerta.setTitle("Manutencão personalizada");
        alerta.setMessage("Deseja criar uma manutenção personalizada?");
        alerta.setCancelable(false);
        alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                getActivity().finish();

                //getFragmentManager().beginTransaction().replace(R.id.frame_container, new ListaVeiculosFragment()).commit();

            }
        });

        alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                //envia dados do veiculo para a proxima fragment
                Bundle dados_do_veiculo = new Bundle();
                dados_do_veiculo.putString("modelo_veiculo",modelo_veiculo);
                dados_do_veiculo.putString("km_veiculo",km_veiculo);
                dados_do_veiculo.putString("placa_veiculo",placa_veiculo);

                AdicionarManutencaoPersonalizadaFragment adicionar_manutencao_personalizada = new AdicionarManutencaoPersonalizadaFragment();
                adicionar_manutencao_personalizada.setArguments(dados_do_veiculo);

                getFragmentManager().beginTransaction().replace(R.id.frame_container_adicionar_veiculo, adicionar_manutencao_personalizada).addToBackStack(null).commit();

            }
        });

        AlertDialog alertDialog = alerta.create();
        alertDialog.show();
    }

    public void mostraAlertDialogDeCancelarCriacaoDaManutencaoRecomendada () {

        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
        alerta.setTitle("Criação de manutenções");
        alerta.setMessage("Deseja realmente criar o veículo sem cadastro de manutenções recomendadas?");
        alerta.setCancelable(false);
        alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Caso clique em não o app não faz nada
            }
        });

        alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }
        });

        AlertDialog alertDialog = alerta.create();
        alertDialog.show();

    }


}
