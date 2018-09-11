package com.example.guilherme.mobe.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.helper.MaskEditUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalhesManutencaoDoVeiculoFragment extends Fragment {

    private static final String TAG = AdicionarCarroFragment.class.getSimpleName();
    private TextView lbl_descricao;
    private Button btn_salvar_alteracao, btn_excluir_manutencao;
    private ProgressDialog pDialog;
    private EditText txt_limite_km, txt_limite_tempo_meses, txt_km_antecipacao, txt_tempo_antecipacao_meses, txt_data_ultima_manutencao, txt_km_ultima_manutencao;

    public DetalhesManutencaoDoVeiculoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detalhes_manutencao_do_veiculo, container, false);

        getActivity().setTitle("Minha Manutenção");


        pDialog = new ProgressDialog(getActivity());

        Bundle dados_do_veiculo = getArguments();
        final String id_manutencao_do_veiculo = dados_do_veiculo.getString("id_manutencao_do_veiculo");
        String descricao = dados_do_veiculo.getString("descricao");
        String limite_km = dados_do_veiculo.getString("limite_km");
        String limite_tempo_meses = dados_do_veiculo.getString("limite_tempo_meses");
        String km_antecipacao = dados_do_veiculo.getString("km_antecipacao");
        String tempo_antecipacao_meses = dados_do_veiculo.getString("tempo_antecipacao_meses");
        String data_ultima_manutencao = dados_do_veiculo.getString("data_ultima_manutencao");
        String km_ultima_manutencao = dados_do_veiculo.getString("km_ultima_manutencao");

        lbl_descricao = (TextView) view.findViewById(R.id.lbl_descricao_fragment_detalhes_manutencao_do_veiculo);
        txt_limite_km = (EditText) view.findViewById(R.id.txt_limite_km_fragment_detalhes_manutencao_do_veiculo);
        txt_limite_tempo_meses = (EditText) view.findViewById(R.id.txt_limite_tempo_meses_fragment_detalhes_manutencao_do_veiculo);
        txt_km_antecipacao = (EditText) view.findViewById(R.id.txt_km_antecipacao_fragment_detalhes_manutencao_do_veiculo);
        txt_tempo_antecipacao_meses = (EditText) view.findViewById(R.id.txt_tempo_antecipacao_meses_fragment_detalhes_manutencao_do_veiculo);
        txt_data_ultima_manutencao = (EditText) view.findViewById(R.id.txt_data_ultima_manutencao_fragment_detalhes_manutencao_do_veiculo);
        txt_km_ultima_manutencao = (EditText) view.findViewById(R.id.txt_km_ultima_manutencao_fragment_detalhes_manutencao_do_veiculo);
        btn_salvar_alteracao = (Button) view.findViewById(R.id.btn_salvar_alteracao_fragment_detalhes_manutencao_do_veiculo);
        btn_excluir_manutencao = (Button) view.findViewById(R.id.btn_excluir_manutencao_fragment_detalhes_manutencao_do_veiculo);



        lbl_descricao.setText(descricao);
        txt_limite_km.setText(limite_km);
        txt_limite_tempo_meses.setText(limite_tempo_meses);
        txt_km_antecipacao.setText(km_antecipacao);
        txt_tempo_antecipacao_meses.setText(tempo_antecipacao_meses);
        txt_data_ultima_manutencao.setText(MaskEditUtil.formatarData(data_ultima_manutencao,"yyyy-MM-dd", "dd/MM/yyyy"));
        txt_km_ultima_manutencao.setText(km_ultima_manutencao);

        btn_salvar_alteracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String limite_km = txt_limite_km.getText().toString().trim();
                String limite_tempo_meses = txt_limite_tempo_meses.getText().toString().trim();
                String km_antecipacao = txt_km_antecipacao.getText().toString().trim();
                String tempo_antecipacao_meses = txt_tempo_antecipacao_meses.getText().toString().trim();
                String data_ultima_manutencao = MaskEditUtil.formatarData(txt_data_ultima_manutencao.getText().toString().trim(), "dd/MM/yyyy", "yyyy-MM-dd");
                String km_ultima_manutencao = txt_km_ultima_manutencao.getText().toString().trim();

                alteraInformacoesDaManutencao(id_manutencao_do_veiculo, limite_km, limite_tempo_meses, km_antecipacao, tempo_antecipacao_meses, data_ultima_manutencao, km_ultima_manutencao);


            }
        });

        btn_excluir_manutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                alerta.setTitle("Excluir Manutenção");
                alerta.setMessage("Deseja realmente remover essa manutenção?");
                alerta.setCancelable(false);
                alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Caso clique em não o app não faz nada
                    }
                });

                alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        excluiManutencaoDoVeiculo(id_manutencao_do_veiculo);
                        // getFragmentManager().beginTransaction().replace(R.id.frame_container, new ListaVeiculosFragment()).commit();


                    }
                });

                AlertDialog alertDialog = alerta.create();
                alertDialog.show();

            }
        });


        return view;


    }

    private void alteraInformacoesDaManutencao(final String id_manutencao_do_veiculo, final String limite_km, final String limite_tempo_meses, final String km_antecipacao, final String tempo_antecipacao_meses, final String data_ultima_manutencao, final String km_ultima_manutencao) {
        pDialog.setMessage("Alterando...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ALTERAR_INFORMACOES_MANUTENCAO_DO_VEICULO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Alterar manutencao Response: " + response);
                hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        Toast.makeText(getActivity(), "Informações alteradas com sucesso!", Toast.LENGTH_LONG).show();


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

                Log.e(TAG, "Erro alterar manutencao: " + error.getMessage());
                Toast.makeText(getActivity(), "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {

            protected Map<String, String> getParams(){

                Map<String, String> params = new HashMap<>();
                params.put("id_manutencao_do_veiculo", id_manutencao_do_veiculo);
                params.put("limite_km", limite_km);
                params.put("limite_tempo_meses", limite_tempo_meses);
                params.put("km_antecipacao", km_antecipacao);
                params.put("tempo_antecipacao_meses", tempo_antecipacao_meses);
                params.put("data_ultima_manutencao", data_ultima_manutencao);
                params.put("km_ultima_manutencao", km_ultima_manutencao);

                return params;

            }
        };

        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void excluiManutencaoDoVeiculo(final String id_manutencao_do_veiculo) {
        pDialog.setMessage("Excluindo...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_EXCLUIR_MANUTENCAO_DO_VEICULO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Excluir manutencao Response: " + response);
                hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        Toast.makeText(getActivity(), "Manutenção excluida com sucesso!", Toast.LENGTH_LONG).show();
                        getFragmentManager().popBackStack();

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

                Log.e(TAG, "Erro exluir manutencao: " + error.getMessage());
                Toast.makeText(getActivity(), "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {

            protected Map<String, String> getParams(){

                Map<String, String> params = new HashMap<>();
                params.put("id_manutencao_do_veiculo", id_manutencao_do_veiculo);
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
