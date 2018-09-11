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
import android.widget.Spinner;
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
import com.example.guilherme.mobe.listview.ManutencaoAtrasadaAdapter;
import com.example.guilherme.mobe.listview.Veiculo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostraManutencoesAtrasadasDoUsuarioFragment extends Fragment {

    private static final String TAG = MostraManutencoesAtrasadasDoUsuarioFragment.class.getSimpleName();
    ListView lista;
    Spinner spinner_filtro;
    private SQLiteHandler bd;
    private String id_usuario;
    private String id_manutencao_do_veiculo_selecionada;

    final List lista_id_veiculos_do_usuario = new ArrayList<>();
    final List<String> lista_itens_do_spinner = new ArrayList<String>();

    public MostraManutencoesAtrasadasDoUsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Manutenções Atrasadas");


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mostra_manutencoes_atrasadas_do_usuario,container,false);

        bd = new SQLiteHandler(this.getActivity());

        HashMap<String, String> usuario = bd.getUserDetails();
        id_usuario = usuario.get("ID_USUARIO");

        adicionaVeiculosDoUsuarioNoSpinner(id_usuario);

        lista = (ListView) view.findViewById(R.id.list_view_manutencoes_atrasadas_do_usuario);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ManutencaoAtrasada manutencao_atrasada = (ManutencaoAtrasada) parent.getItemAtPosition(position);
                id_manutencao_do_veiculo_selecionada = manutencao_atrasada.getId();

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

        spinner_filtro = (Spinner) view.findViewById(R.id.spinner_mostra_manutencoes_atrasadas_do_usuario);
        spinner_filtro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {

                if(posicao == 0) {

                    adicionaTodasManutencoesAtrasadasDoUsuarioNoListView(id_usuario);

                } else {

                    //obtem o id do veiculo selecionado e envia pro metodo
                    adicionaTodasManutencoesAtrasadasDoVeiculoDoUsuarioNoListView(lista_id_veiculos_do_usuario.get(posicao).toString());

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        adicionaTodasManutencoesAtrasadasDoUsuarioNoListView(id_usuario);
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

        /*
        txtInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        */

        /*
        //setOnFocusChangeListener é o evento usado para ao clicar na caixa abrir o datapicker
        txtInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                    new DatePickerDialog(getContext(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        */

        txtInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
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
                        onResume();



                    } else {

                        Toast.makeText(getActivity(), "Não foi possível realizar manutenção", Toast.LENGTH_SHORT).show();
                        onResume();

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
                Toast.makeText(getActivity().getApplicationContext(),"Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
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

    private void adicionaTodasManutencoesAtrasadasDoUsuarioNoListView(final String id_usuario) {

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

                    } else {

                        Toast.makeText(getActivity(), "Não foi encontrado manutenções atrasadas", Toast.LENGTH_SHORT).show();

                    }

                    ArrayAdapter adapter = new ManutencaoAtrasadaAdapter(getActivity(),manutencoes_atrasadas);
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

    private void adicionaTodasManutencoesAtrasadasDoVeiculoDoUsuarioNoListView(final String id_veiculo_do_usuario) {

        final ArrayList<ManutencaoAtrasada> manutencoes_atrasadas = new ArrayList<ManutencaoAtrasada>();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_OBTER_MANUTENCOES_ATRASADAS_DO_VEICULO_DO_USUARIO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Obter manutencoes atrasadas do veiculo Response: " + response.toString());

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

                    } else {

                        Toast.makeText(getActivity(), "Não foi encontrado manutenções atrasadas para esse veículo", Toast.LENGTH_SHORT).show();

                    }

                    ArrayAdapter adapter = new ManutencaoAtrasadaAdapter(getActivity(),manutencoes_atrasadas);
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
                Log.e(TAG, "Obter manutencoes atrasadas do veiculo Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_veiculo_do_usuario",id_veiculo_do_usuario);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private void adicionaVeiculosDoUsuarioNoSpinner(final String id_usuario) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_OBTER_VEICULOS_POR_USUARIO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Obter veiculos do usuario Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        JSONArray veiculos_JSON = object.getJSONArray("veiculos");

                        lista_itens_do_spinner.add("Todos os veiculos");
                        lista_id_veiculos_do_usuario.add(0);

                        for (int i = 0; i < veiculos_JSON.length(); i++) {

                            JSONObject veiculo_JSON = (JSONObject) veiculos_JSON.get(i);

                            int id_veiculo_do_usuario =  veiculo_JSON.getInt("id_veiculo_do_usuario");
                            String modelo_veiculo = (String) veiculo_JSON.get("modelo");
                            String placa = (String) veiculo_JSON.get("placa");

                            lista_id_veiculos_do_usuario.add(id_veiculo_do_usuario);
                            lista_itens_do_spinner.add(modelo_veiculo + " - " + placa);

                        }

                        spinner_filtro.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, lista_itens_do_spinner));


                    } else {

                        Toast.makeText(getActivity(), "Não foi encontrado veiculos para esse usuario", Toast.LENGTH_SHORT).show();

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
                Log.e(TAG, "Obter veiculos do usuario Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),"Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
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
