package com.example.guilherme.mobe.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.helper.SQLiteHandler;
import com.example.guilherme.mobe.listview.Veiculo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdicionarCarroFragment extends Fragment {
    /*teste*/
    private Spinner spinnerMarcas;
    private Spinner spinnerModelos;
    private Spinner spinnerAnos;
    private EditText txt_km;
    private EditText txt_dispositivo;
    private EditText txt_placa;
    private static final String TAG = AdicionarCarroFragment.class.getSimpleName();
    private int id_modelo_selecionado;
    private String nome_modelo_selecionado;
    private String ano_selecionado;
    private Button btn_proxima_etapa;
    private ProgressDialog pDialog;
    private SQLiteHandler bd;
    private String id_usuario;
    private String nome_activity_atual;

    private List lista_id_modelos = new ArrayList<>();
    private List<String> lista_nome_modelos = new ArrayList<String>();


    public AdicionarCarroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Adicionar Veículo");

        nome_activity_atual = getActivity().getClass().getSimpleName();


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adicionar_carro,container,false);

        obterMarcas();

        txt_dispositivo = (EditText) view.findViewById(R.id.txt_dispositivo_adicionar_veiculo);
        txt_km = (EditText) view.findViewById(R.id.txt_km_adicionar_veiculo);
        txt_placa = (EditText) view.findViewById(R.id.txt_placa_adicionar_carro);

        pDialog = new ProgressDialog(getActivity());
        bd = new SQLiteHandler(getActivity());


        HashMap<String, String> usuario = bd.getUserDetails();
        id_usuario = usuario.get("ID_USUARIO");

        spinnerMarcas = (Spinner) view.findViewById(R.id.Marcaspinner);
        spinnerMarcas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {

                //marca_selecionada = parent.getItemAtPosition(posicao).toString();

                int id_marca_selecionada = posicao + 1;
                Log.d(TAG,String.valueOf(id_marca_selecionada));

                obterModelos(id_marca_selecionada);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerModelos = (Spinner) view.findViewById(R.id.Modelospinner);
        spinnerModelos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {

                //id_modelo_selecionado = parent.getItemAtPosition(posicao).toString();
                id_modelo_selecionado = (int) lista_id_modelos.get(posicao);
                nome_modelo_selecionado = lista_nome_modelos.get(posicao);
                obterAnos();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerAnos = (Spinner) view.findViewById(R.id.anos_spinner);
        spinnerAnos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {

                ano_selecionado = parent.getItemAtPosition(posicao).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_proxima_etapa = (Button) view.findViewById(R.id.btn_proximo_adicionar_carro);
        btn_proxima_etapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionaVeiculo(ano_selecionado,txt_placa.getText().toString().trim(),txt_km.getText().toString().trim(),id_usuario,txt_dispositivo.getText().toString().trim(),String.valueOf(id_modelo_selecionado));

            }
        });

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
                        mostraAlertDialogDeCancelarCriacaoVeiculo();
                        return true;
                    }
                    return false;
                }
            });
        }


    }

    public void mostraAlertDialogDeCancelarCriacaoVeiculo () {

        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
        alerta.setTitle("Criação do veículo");
        alerta.setMessage("Deseja realmente cancelar a criação desse veículo?");
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


    private void obterAnos() {
        ArrayList lista_anos = new ArrayList<>();

        Calendar hoje = Calendar.getInstance();
        int ano = hoje.get(Calendar.YEAR);

        for(int i = 2000;i <=ano;i++) {
            lista_anos.add(i);
        }

        spinnerAnos.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, lista_anos));

    }

    private void obterMarcas () {

        final List<String> lista_nome_marcas = new ArrayList<String>();
        final List lista_id_marcas = new ArrayList<>();

        //lista_id_marcas.removeAll(lista_id_marcas);
        //lista_nome_marcas.removeAll(lista_nome_marcas);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_OBTER_MARCAS_VEICULOS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Obter marcas Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    JSONArray marcas = object.getJSONArray("marcas");

                    for(int i = 0; i < marcas.length() ; i++){

                        JSONObject marca = (JSONObject) marcas.get(i);
                        int id_marca = (int) marca.get("ID");
                        String nome_marca = (String) marca.get("MARCA");

                        lista_id_marcas.add(id_marca);
                        lista_nome_marcas.add(nome_marca);


                        Log.d(TAG, nome_marca);

                        //popular o array no spinner logo apos obter do server
                        spinnerMarcas.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, lista_nome_marcas));

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
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void obterModelos (final int id_marca) {

        lista_id_modelos.removeAll(lista_id_modelos);
        lista_nome_modelos.removeAll(lista_nome_modelos);



        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_OBTER_MODELOS_VEICULOS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update user Re: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    JSONArray modelos = object.getJSONArray("modelos");

                    for(int i = 0; i < modelos.length() ; i++){

                        JSONObject modelo = (JSONObject) modelos.get(i);
                        int id_modelo = (int) modelo.get("ID");
                        String nome_modelo = (String) modelo.get("MODELO");

                        lista_id_modelos.add(id_modelo);
                        lista_nome_modelos.add(nome_modelo);

                        Log.d(TAG, nome_modelo);

                        //popular o array no spinner logo apos obter do server
                        spinnerModelos.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, lista_nome_modelos));

                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Não foi encontrado modelos para a marca selecionada", Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_marca",String.valueOf(id_marca));
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void adicionaVeiculo(final String ano, final String placa, final String km, final String id_usuario, final String codigo_dispositivo, final String id_modelo_veiculo) {
        pDialog.setMessage("Registrando...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTRAR_VEICULO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Registro Veiculo Response: " + response);
                hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        Toast.makeText(getActivity(), "Veículo registrado com sucesso!", Toast.LENGTH_LONG).show();

                        //enviar veiculo adicionado para a proxima fragment
                        Bundle dados_veiculo_adicionado = new Bundle();
                        dados_veiculo_adicionado.putString("modelo_veiculo",String.valueOf(nome_modelo_selecionado));
                        dados_veiculo_adicionado.putString("placa_veiculo",placa);
                        dados_veiculo_adicionado.putString("km_veiculo",km);

                        MostraManutencoesRecomendadasDoVeiculo mostra_manutencoes_recomendadas_do_veiculo = new MostraManutencoesRecomendadasDoVeiculo();
                        mostra_manutencoes_recomendadas_do_veiculo.setArguments(dados_veiculo_adicionado);
                        getFragmentManager().beginTransaction().replace(R.id.frame_container_adicionar_veiculo, mostra_manutencoes_recomendadas_do_veiculo).addToBackStack(null).commit();


                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "Erro Registro: " + error.getMessage());
                Toast.makeText(getActivity(), "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {

            protected Map<String, String> getParams(){

                Map<String, String> params = new HashMap<>();
                params.put("ano", ano);
                params.put("placa", placa);
                params.put("km", km);
                params.put("id_usuario", id_usuario);
                params.put("codigo_dispositivo", codigo_dispositivo);
                params.put("id_modelo_veiculo", id_modelo_veiculo);
                return params;

            }
        };

        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void showDialog(){

        if(!pDialog.isShowing())
            pDialog.show();

    }

    private void hideDialog(){

        if(pDialog.isShowing())
            pDialog.dismiss();

    }



}
