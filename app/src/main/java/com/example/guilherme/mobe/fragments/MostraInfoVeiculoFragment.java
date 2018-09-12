package com.example.guilherme.mobe.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class MostraInfoVeiculoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MostraInfoVeiculoFragment.class.getSimpleName();
    final MostraInfoVeiculoFragment context = this;
    private TextView txtMarca;
    private TextView txtModelo;
    private TextView txtKm;
    private TextView txtPlaca;
    private TextView txtDispositivo;
    private TextView txtAno;
    private String placa;
    private String id_usuario;
    private AppCompatImageButton btn_alterar_km_veiculo, btn_alterar_dispositivo_veiculo;
    private Button btn_excluir_veiculo;
    private Button btn_criar_manutencao;
    private Button btn_ver_manutencoes;
    private String km_no_momento_da_abertura_da_fragment, dispositivo_no_momento_da_abertura_da_fragment;
    private String nome_activity_atual;
    private ProgressDialog pDialog;
    SwipeRefreshLayout swipeLayout;




    public MostraInfoVeiculoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        nome_activity_atual = getActivity().getClass().getSimpleName();

        getActivity().setTitle("Meu veículo");

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        View view = inflater.inflate(R.layout.fragment_mostra_info_veiculo,container,false);

        //teste

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_fragment_mostra_info_veiculo);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //fim teste

        //Obter dados da fragment anterior
        Bundle dados_do_veiculo = getArguments();
        placa = dados_do_veiculo.getString("placa");
        id_usuario = dados_do_veiculo.getString("id_usuario");
        //--------------------------------------------------------------------------

        txtMarca = (TextView) view.findViewById(R.id.txt_marca_fragment_mostra_info_veiculo);
        txtModelo = (TextView) view.findViewById(R.id.txt_modelo_fragment_mostra_info_veiculo);
        txtKm = (TextView) view.findViewById(R.id.txt_km_fragment_mostra_info_veiculo);
        txtPlaca = (TextView) view.findViewById(R.id.txt_placa_fragment_mostra_info_veiculo);
        txtDispositivo = (TextView) view.findViewById(R.id.txt_dispositivo_fragment_mostra_info_veiculo);
        txtAno = (TextView) view.findViewById(R.id.txt_ano_fragment_mostra_info_veiculo);
        btn_alterar_km_veiculo = (AppCompatImageButton) view.findViewById(R.id.btn_alterar_km_fragment_mostra_info_veiculo);
        btn_alterar_dispositivo_veiculo = (AppCompatImageButton) view.findViewById(R.id.btn_alterar_dispositivo_fragment_mostra_info_veiculo);
        btn_excluir_veiculo = (Button) view.findViewById(R.id.btn_excluir_veiculo_fragment_mostra_info_veiculo);
        btn_criar_manutencao = (Button) view.findViewById(R.id.btn_criar_manutencao_fragment_mostra_info_veiculo);
        btn_ver_manutencoes = (Button) view.findViewById(R.id.btn_ver_manutencoes_fragment_mostra_info_veiculo);

        mostraInfoVeiculo();

        btn_alterar_km_veiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(1);
            }
        });

        btn_alterar_dispositivo_veiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(2);
            }
        });

        btn_ver_manutencoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //envia dados do veiculo para a proxima fragment
                Bundle dados_do_veiculo = new Bundle();
                dados_do_veiculo.putString("placa_veiculo",txtPlaca.getText().toString().trim());

                MostraManutencoesDoVeiculo mostra_manutencoes_do_veiculo = new MostraManutencoesDoVeiculo();
                mostra_manutencoes_do_veiculo.setArguments(dados_do_veiculo);
                getFragmentManager().beginTransaction().replace(R.id.frame_container_mostra_info_veiculo, mostra_manutencoes_do_veiculo).addToBackStack(null).commit();

            }
        });

        btn_criar_manutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                alerta.setTitle("Adicionar Manutenção");
                alerta.setMessage("Deseja criar manutenção para esse veículo?");
                alerta.setCancelable(true);
                alerta.setNegativeButton("Personalizada", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //envia dados do veiculo para a proxima fragment
                        Bundle dados_do_veiculo = new Bundle();
                        dados_do_veiculo.putString("modelo_veiculo",txtModelo.getText().toString().trim());
                        dados_do_veiculo.putString("km_veiculo",txtKm.getText().toString().trim());
                        dados_do_veiculo.putString("placa_veiculo",txtPlaca.getText().toString().trim());

                        AdicionarManutencaoPersonalizadaFragment adicionar_manutencao_personalizada = new AdicionarManutencaoPersonalizadaFragment();
                        adicionar_manutencao_personalizada.setArguments(dados_do_veiculo);

                        getFragmentManager().beginTransaction().replace(R.id.frame_container_mostra_info_veiculo, adicionar_manutencao_personalizada).addToBackStack(null).commit();

                    }
                }) ;

                alerta.setPositiveButton("Recomendada", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Bundle dados_do_veiculo = new Bundle();
                        dados_do_veiculo.putString("modelo_veiculo",txtModelo.getText().toString().trim());
                        dados_do_veiculo.putString("km_veiculo",txtKm.getText().toString().trim());
                        dados_do_veiculo.putString("placa_veiculo",txtPlaca.getText().toString().trim());

                        MostraManutencoesRecomendadasDoVeiculo mostra_manutencoes_recomendadas_do_veiculo = new MostraManutencoesRecomendadasDoVeiculo();
                        mostra_manutencoes_recomendadas_do_veiculo.setArguments(dados_do_veiculo);
                        getFragmentManager().beginTransaction().replace(R.id.frame_container_mostra_info_veiculo, mostra_manutencoes_recomendadas_do_veiculo).addToBackStack(null).commit();

                    }
                });

                AlertDialog alertDialog = alerta.create();
                alertDialog.show();

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
                       // getFragmentManager().beginTransaction().replace(R.id.frame_container, new ListaVeiculosFragment()).commit();


                    }
                });

                AlertDialog alertDialog = alerta.create();
                alertDialog.show();

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
                mostraInfoVeiculo();
            }
        }, 1000);
    }

    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume foi chamado");
        mostraInfoVeiculo();
    }

    private void showInputDialog(int i) {

        LayoutInflater layoutInflater = getLayoutInflater();
        View promptView = layoutInflater.inflate(R.layout.input_dialog,null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptView);

        final EditText txtInput = (EditText) promptView.findViewById(R.id.editTextInput);
        if (i == 1) {
            String titulo = "Insira a nova Quilometragem";
            alertDialogBuilder.setCancelable(false).setTitle(titulo)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(km_no_momento_da_abertura_da_fragment.equals(txtInput.getText().toString()) || txtInput.getText().toString().isEmpty()) {

                                Toast.makeText(getActivity().getApplicationContext(), "Não foi alterada a km", Toast.LENGTH_LONG).show();

                            } else {

                                alterarKmVeiculoManualmente(txtPlaca.getText().toString(),txtInput.getText().toString(),txtDispositivo.getText().toString());
                              //  txtDispositivo.setText(txtInput.getText().toString());

                            }

                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
        } else {
            String titulo = "Insira o novo Dispositivo";
            alertDialogBuilder.setCancelable(false).setTitle(titulo)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(dispositivo_no_momento_da_abertura_da_fragment.equals(txtInput.getText().toString()) || txtInput.getText().toString().isEmpty()) {

                                Toast.makeText(getActivity().getApplicationContext(), "Não foi alterado o código do dispositivo", Toast.LENGTH_SHORT).show();

                            } else {

                                alterarDispositivoDoVeiculo(txtInput.getText().toString(), txtPlaca.getText().toString());
                              //  txtDispositivo.setText(txtInput.getText().toString());


                            }
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

        }

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

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
                        String dispositivo = info_veiculo.getString("codigo_dispositivo");

                        txtMarca.setText(marca);
                        txtModelo.setText(modelo);
                        txtPlaca.setText(placa);
                        txtAno.setText(String.valueOf(ano));
                        txtKm.setText(String.valueOf(km));
                        txtDispositivo.setText(dispositivo);

                        km_no_momento_da_abertura_da_fragment = String.valueOf(km);
                        dispositivo_no_momento_da_abertura_da_fragment = dispositivo;

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
                Toast.makeText(getActivity().getApplicationContext(),"Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("placa",placa);
                params.put("id_usuario",id_usuario);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private void alterarDispositivoDoVeiculo(final String codigo_dispositivo, final String placa) {
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ALTERAR_DISPOSITIVO_DO_VEICULO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Alterar dispositivo Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        Toast.makeText(getActivity().getApplicationContext(), "A dispositivo foi alterado com sucesso!", Toast.LENGTH_LONG).show();
                        onResume(); // recarregar as informacoes do veiculo


                    } else {

                        String errorMsg = object.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        onResume(); // recarregar as informacoes do veiculo

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
                Log.e(TAG, "modificar dispositivo Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),"Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("codigo_dispositivo",codigo_dispositivo);
                params.put("placa",placa);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private void alterarKmVeiculoManualmente(final String placa, final String km, final String codigo_dispositivo) {

        pDialog.setMessage("Atualizando...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ALTERAR_KM_MANUALMENTE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Alterar km manualmente Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        Toast.makeText(getActivity().getApplicationContext(), "A quilometragem do veículo foi alterada com sucesso!", Toast.LENGTH_LONG).show();
                        onResume(); // recarregar as informacoes do veiculo


                    } else {

                        String errorMsg = object.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        onResume(); // recarregar as informacoes do veiculo

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
                Log.e(TAG, "modificar km Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),"Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("placa",placa);
                params.put("km",km);
                params.put("codigo_dispositivo",codigo_dispositivo);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private void excluiVeiculo(final String placa) {

        pDialog.setMessage("Excluindo ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_EXCLUIR_VEICULO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Excluir veiculo Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        Toast.makeText(getActivity(), "Veículo removido com sucesso!", Toast.LENGTH_LONG).show();
                        getActivity().finish();

                    } else {

                        String errorMsg = object.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getActivity(),"Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
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

    private void showDialog() {
        if(!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    private void hideDialog() {
        if(pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

}
