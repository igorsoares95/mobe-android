package com.example.guilherme.mobe.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
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
import com.example.guilherme.mobe.helper.SQLiteHandler;
import com.example.guilherme.mobe.helper.SessionManager;
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
public class ListaVeiculosFragment extends Fragment {

    ListView lista;
    private static final String TAG = ListaVeiculosFragment.class.getSimpleName();
    private SQLiteHandler bd;
    private String id_usuario;
    FloatingActionButton fab;



    public ListaVeiculosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Meus veículos");


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_veiculos,container,false);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        bd = new SQLiteHandler(this.getActivity());

        HashMap<String, String> usuario = bd.getUserDetails();
        id_usuario = usuario.get("ID_USUARIO");

        lista = (ListView) view.findViewById(R.id.lvVeiculos);
        adicionaVeiculosNoListView();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFragmentManager().beginTransaction().replace(R.id.frame_container, new AdicionarCarroFragment()).addToBackStack(null).commit();


            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //obter veiculo da listview
                Veiculo veiculo_selecionado = (Veiculo) parent.getItemAtPosition(position);

                //Enviar dados para proxima fragment
                Bundle dados_do_veiculo = new Bundle();
                dados_do_veiculo.putString("placa", veiculo_selecionado.getPlaca());
                dados_do_veiculo.putString("id_usuario",id_usuario);

                MostraInfoVeiculoFragment mostra_info_veiculo_fragment = new MostraInfoVeiculoFragment();
                mostra_info_veiculo_fragment.setArguments(dados_do_veiculo);
                //------------------------------------------------------------------------------------

                getFragmentManager().beginTransaction().replace(R.id.frame_container, mostra_info_veiculo_fragment).addToBackStack(null).commit();


            }
        });

        return view;

    }

    private void adicionaVeiculosNoListView() {

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

                            //Log.d(TAG, "Update user Response: " + modelo + placa + km + dispositivo);

                        }

                    }

                    ArrayAdapter adapter = new VeiculoAdapter(getActivity(),veiculos);
                    lista.setAdapter(adapter);


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
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
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
