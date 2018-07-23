package com.example.guilherme.mobe.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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

    private Spinner spinnerMarcas;
    private Spinner spinnerModelos;
    private Spinner spinnerAnos;
    private EditText txt_km;
    private EditText txt_dispositivo;
    private EditText txt_placa;
    private static final String TAG = AdicionarCarroFragment.class.getSimpleName();
    private String marca_selecionada;
    private String modelo_selecionado;
    private String ano_selecionado;
    private Button btn_proxima_etapa;
    private ProgressDialog pDialog;
    private SQLiteHandler bd;
    private String id_usuario;




    public AdicionarCarroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

                marca_selecionada = parent.getItemAtPosition(posicao).toString();
                obterModelos(marca_selecionada);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerModelos = (Spinner) view.findViewById(R.id.Modelospinner);
        spinnerModelos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {

                modelo_selecionado = parent.getItemAtPosition(posicao).toString();
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
                adicionaVeiculo(marca_selecionada,modelo_selecionado,ano_selecionado,txt_placa.getText().toString(),id_usuario,txt_dispositivo.getText().toString(),txt_km.getText().toString());
            }
        });

        return view;
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


        final List<String> lista_marcas = new ArrayList<String>();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_OBTER_MARCAS_VEICULOS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Obter marcas Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    JSONArray marcas = object.getJSONArray("marcas");

                    for(int i = 0; i < marcas.length() ; i++){

                        String marca = (String) marcas.get(i);
                        lista_marcas.add(marca);
                        Log.d(TAG, marca);

                        //popular o array no spinner logo apos obter do server
                        spinnerMarcas.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, lista_marcas));

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
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }


    private void obterModelos (final String marca) {

        final List<String> lista_modelos = new ArrayList<String>();


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_OBTER_MODELOS_VEICULOS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update user Re: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    JSONArray veiculos = object.getJSONArray("veiculos");

                    for(int i = 0; i < veiculos.length() ; i++){

                        String marca = (String) veiculos.get(i);
                        lista_modelos.add(marca);

                        Log.d(TAG, marca);

                        //popular o array no spinner logo apos obter do server
                        spinnerModelos.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, lista_modelos));

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
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }


    private void adicionaVeiculo(final String marca, final String modelo, final String ano, final String placa, final String usuario, final String dispositivo, final String km) {
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
                        getFragmentManager().beginTransaction().replace(R.id.frame_container, new ListaVeiculosFragment()).addToBackStack(null).commit();


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
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {

            protected Map<String, String> getParams(){

                Map<String, String> params = new HashMap<>();
                params.put("marca", marca);
                params.put("modelo", modelo);
                params.put("ano", ano);
                params.put("placa", placa);
                params.put("usuario", usuario);
                params.put("dispositivo", dispositivo);
                params.put("km", km);

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
