package com.example.guilherme.mobe.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.activity.MainActivity;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.listview.Veiculo;
import com.example.guilherme.mobe.listview.VeiculoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostraInfoVeiculoFragment extends Fragment {

    private static final String TAG = MostraInfoVeiculoFragment.class.getSimpleName();
    private EditText txtMarca;
    private EditText txtModelo;
    private EditText txtKm;
    private EditText txtPlaca;
    private EditText txtDispositivo;
    private EditText txtAno;
    private String placa;
    private String id_usuario;
    private Button btn_alterar_dados_veiculo;
    private Button btn_excluir_veiculo;


    public MostraInfoVeiculoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mostra_info_veiculo,container,false);




        //Obter dados da fragment anterior
        Bundle dados_do_veiculo = getArguments();
        placa = dados_do_veiculo.getString("placa");
        id_usuario = dados_do_veiculo.getString("id_usuario");
        //--------------------------------------------------------------------------

        txtMarca = (EditText) view.findViewById(R.id.txt_marca_mostra_info_veiculo);
        txtModelo = (EditText) view.findViewById(R.id.txt_modelo_mostra_info_veiculo);
        txtKm = (EditText) view.findViewById(R.id.txt_km_mostra_info_veiculo);
        txtPlaca = (EditText) view.findViewById(R.id.txt_placa_mostra_info_veiculo);
        txtDispositivo = (EditText) view.findViewById(R.id.txt_dispositivo_mostra_info_veiculo);
        txtAno = (EditText) view.findViewById(R.id.txt_ano_mostra_info_veiculo);
        btn_alterar_dados_veiculo = (Button) view.findViewById(R.id.btn_alterar_dados_veiculo_mostra_info_veiculo);
        btn_excluir_veiculo = (Button) view.findViewById(R.id.btn_excluir_veiculo_mostra_info_veiculo);

        mostraInfoVeiculo();

        btn_alterar_dados_veiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                atualizaInfoVeiculo(txtMarca.getText().toString(),txtModelo.getText().toString(),txtAno.getText().toString(),txtDispositivo.getText().toString(),placa);

            }
        });

        btn_excluir_veiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                alerta.setTitle("Excluir Veículo");
                alerta.setMessage("Deseja realmente remover esse veículo?");
                alerta.setCancelable(false);
                alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Caso clique em não o app não faz nada
                    }
                });

                alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        excluiVeiculo(placa);
                        getFragmentManager().beginTransaction().replace(R.id.frame_container, new ListaVeiculosFragment()).commit();

                    }
                });

                AlertDialog alertDialog = alerta.create();
                alertDialog.show();

            }
        });

        return view;

    }

    private void mostraInfoVeiculo() {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_MOSTRAR_INFO_VEICULO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Obter dados do veiculo Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        JSONObject info_veiculo = object.getJSONObject("veiculo");

                        String marca = info_veiculo.getString("marca");
                        String modelo = info_veiculo.getString("modelo");
                        int ano = info_veiculo.getInt("ano");
                        String placa = info_veiculo.getString("placa");
                        int km = info_veiculo.getInt("km");
                        String dispositivo = info_veiculo.getString("dispositivo");

                        txtMarca.setText(marca);
                        txtModelo.setText(modelo);
                        txtPlaca.setText(placa);
                        txtAno.setText(String.valueOf(ano));
                        txtKm.setText(String.valueOf(km));
                        txtDispositivo.setText(dispositivo);

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
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("placa",placa);
                params.put("usuario",id_usuario);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private void atualizaInfoVeiculo(final String marca, final String modelo,final String ano, final String dispositivo, final String placa) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ATUALIZAR_INFO_VEICULO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Atualizar info veiculo Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        Toast.makeText(getActivity().getApplicationContext(), "As informações do veículo foram alteradas com sucesso!", Toast.LENGTH_LONG).show();

                    } else {

                        String errorMsg = object.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

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
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("marca",marca);
                params.put("modelo",modelo);
                params.put("ano",ano);
                params.put("dispositivo",dispositivo);
                params.put("placa",placa);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private void excluiVeiculo(final String placa) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EXCLUIR_VEICULO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Excluir veiculo Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        Toast.makeText(getActivity().getApplicationContext(), "Veículo removido com sucesso!", Toast.LENGTH_LONG).show();

                    } else {

                        String errorMsg = object.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

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
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("placa",placa);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

}