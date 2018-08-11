package com.example.guilherme.mobe.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.helper.MaskEditUtil;
import com.example.guilherme.mobe.helper.SQLiteHandler;
import com.example.guilherme.mobe.listview.ManutencaoAtrasada;
import com.example.guilherme.mobe.listview.ManutencaoProxima;
import com.example.guilherme.mobe.listview.ManutencaoProximaAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostraManutencoesProximasDoUsuarioFragment extends Fragment {

    private static final String TAG = MostraManutencoesProximasDoUsuarioFragment.class.getSimpleName();
    ListView lista;
    private SQLiteHandler bd;
    private String id_usuario;
    private String id_manutencao_do_veiculo_selecionada;

    public MostraManutencoesProximasDoUsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Manutenções Próximas");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mostra_manutencoes_proximas_do_usuario,container,false);

        bd = new SQLiteHandler(this.getActivity());

        HashMap<String, String> usuario = bd.getUserDetails();
        id_usuario = usuario.get("ID_USUARIO");

        lista = (ListView) view.findViewById(R.id.list_view_manutencoes_proximas_do_usuario);

        adicionaManutencoesProximasDoUsuarioNoListView();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ManutencaoProxima manutencao_proxima = (ManutencaoProxima) parent.getItemAtPosition(position);
                id_manutencao_do_veiculo_selecionada = manutencao_proxima.getId();

                AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                alerta.setTitle("Realizar manutenção");
                alerta.setMessage("Deseja realizar essa manutenção?");
                alerta.setCancelable(false);
                alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Caso clique em nao, o app nao faz nada e mantem na mesma tela;
                    }
                });

                alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        abreAlertDialog();
                    }
                });

                AlertDialog alertDialog = alerta.create();
                alertDialog.show();

            }
        });

        return view;
    }

    private void abreAlertDialog() {
        abreAlertDialogDataManutencao();

    }

    EditText txtInput;
    final Calendar myCalendar = Calendar.getInstance();

    private void abreAlertDialogDataManutencao() {

        LayoutInflater layoutInflater = getLayoutInflater();
        View promptView = layoutInflater.inflate(R.layout.input_dialog,null);
        AlertDialog.Builder alertDialogBuilderDataManutencao = new AlertDialog.Builder(getContext());
        alertDialogBuilderDataManutencao.setView(promptView);

        txtInput = (EditText) promptView.findViewById(R.id.editTextInput);
        String titulo = "Insira a Data da manutenção";
        alertDialogBuilderDataManutencao.setCancelable(false).setTitle(titulo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        abreAlertDialogKmManutencao(MaskEditUtil.formatarData(txtInput.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"));

                        Log.e(TAG,MaskEditUtil.formatarData(txtInput.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"));

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        //Abrir calendario para pedir da data da manutencao
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        txtInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilderDataManutencao.create();
        alertDialog.show();

    }

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

        txtInput.setText(sdf.format(myCalendar.getTime()));
    }

    private void abreAlertDialogKmManutencao(final String data) {

        LayoutInflater layoutInflater = getLayoutInflater();
        View promptView = layoutInflater.inflate(R.layout.input_dialog,null);
        AlertDialog.Builder alertDialogBuilderKmManutencao = new AlertDialog.Builder(getContext());
        alertDialogBuilderKmManutencao.setView(promptView);

        final EditText txtInput = (EditText) promptView.findViewById(R.id.editTextInput);
        String titulo = "Insira a Km da manutenção";
        alertDialogBuilderKmManutencao.setCancelable(false).setTitle(titulo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        realizaManutencao(data, txtInput.getText().toString(), id_manutencao_do_veiculo_selecionada);

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilderKmManutencao.create();
        alertDialog.show();

    }


    private void realizaManutencao(final String data_ultima_manutencao, final String km_ultima_manutencao, final String id_manutencao_do_veiculo) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REALIZAR_MANUTENCAO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Realizar manutencao Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        Toast.makeText(getActivity(), "Manutenção realizada com sucesso", Toast.LENGTH_SHORT).show();
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_container, new ListaVeiculosFragment())
                                .commit();



                    } else {

                        Toast.makeText(getActivity(), "Não foi possível realizar manutenção", Toast.LENGTH_SHORT).show();
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_container, new ListaVeiculosFragment())
                                .commit();

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
                Log.e(TAG, "Realizar manutencao Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("data_ultima_manutencao",data_ultima_manutencao);
                params.put("km_ultima_manutencao",km_ultima_manutencao);
                params.put("id_manutencao_do_veiculo",id_manutencao_do_veiculo);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);



    }

    private void adicionaManutencoesProximasDoUsuarioNoListView() {

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

                    } else {

                        Toast.makeText(getActivity(), "Não foi encontrado manutenções próximas", Toast.LENGTH_SHORT).show();

                    }

                    ArrayAdapter adapter = new ManutencaoProximaAdapter(getActivity(),manutencoes_proximas);
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
                Log.e(TAG, "Obter manutencoes atrasadas Error: " + error.getMessage());
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
