package com.example.guilherme.mobe.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.guilherme.mobe.helper.MaskEditUtil;
import com.example.guilherme.mobe.listview.ManutencaoRecomendada;
import com.example.guilherme.mobe.listview.ManutencaoRecomendadaAdapter;

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
public class AdicionarManutencaoPersonalizadaFragment extends Fragment {

    private static final String TAG = MostraManutencoesRecomendadasDoVeiculo.class.getSimpleName();
    EditText txt_descricao_manutencao, txt_limite_km, txt_limite_tempo_meses, txt_km_antecipacao, txt_tempo_antecipacao, txt_data_ultima_manutencao, txt_km_ultima_manutencao;
    Button btn_criar_manutencao;
    TextView lbl_modelo_veiculo, lbl_km_veiculo, lbl_placa_veiculo;
    String modelo_veiculo, placa_veiculo, km_veiculo;
    private Context mContext;
    String nome_activity_atual;



    public AdicionarManutencaoPersonalizadaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adicionar_manutencao_personalizada, container, false);

        getActivity().setTitle("Manutenção personalizada");

        nome_activity_atual = getActivity().getClass().getSimpleName();

        mContext = getContext();

        txt_descricao_manutencao = (EditText) view.findViewById(R.id.txt_descricao_manutencao_fragment_adicionar_manutencao_personalizada);
        txt_limite_km = (EditText) view.findViewById(R.id.txt_limite_km_fragment_adicionar_manutencao_personalizada);
        txt_limite_tempo_meses = (EditText) view.findViewById(R.id.txt_limite_tempo_meses_fragment_adicionar_manutencao_personalizada);
        txt_km_antecipacao = (EditText) view.findViewById(R.id.txt_km_antecipacao_fragment_adicionar_manutencao_personalizada);
        txt_tempo_antecipacao = (EditText) view.findViewById(R.id.txt_tempo_antecipacao_fragment_adicionar_manutencao_personalizada);
        txt_data_ultima_manutencao = (EditText) view.findViewById(R.id.txt_data_ultima_manutencao_fragment_adicionar_manutencao_personalizada);
        txt_km_ultima_manutencao = (EditText) view.findViewById(R.id.txt_km_ultima_manutencao_fragment_adicionar_manutencao_personalizada);
        btn_criar_manutencao = (Button) view.findViewById(R.id.btn_criar_manutencao_fragment_adicionar_manutencao_personalizada);
        lbl_modelo_veiculo = (TextView) view.findViewById(R.id.lbl_modelo_veiculo_fragment_adicionar_manutencao_personalizada);
        lbl_km_veiculo = (TextView) view.findViewById(R.id.lbl_km_veiculo_fragment_adicionar_manutencao_personalizada);
        lbl_placa_veiculo = (TextView) view.findViewById(R.id.lbl_placa_veiculo_fragment_adicionar_manutencao_personalizada);
        txt_km_antecipacao.setInputType(InputType.TYPE_CLASS_NUMBER);
        txt_km_ultima_manutencao.setInputType(InputType.TYPE_CLASS_NUMBER);
        txt_limite_km.setInputType(InputType.TYPE_CLASS_NUMBER);
        txt_tempo_antecipacao.setInputType(InputType.TYPE_CLASS_NUMBER);
        txt_limite_tempo_meses.setInputType(InputType.TYPE_CLASS_NUMBER);

        Bundle dados_do_veiculo = getArguments();

        modelo_veiculo = dados_do_veiculo.getString("modelo_veiculo");
        placa_veiculo = dados_do_veiculo.getString("placa_veiculo");
        km_veiculo = dados_do_veiculo.getString("km_veiculo");

        lbl_modelo_veiculo.setText(modelo_veiculo);
        lbl_km_veiculo.setText(km_veiculo + " Km");
        lbl_placa_veiculo.setText(placa_veiculo);

        btn_criar_manutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String descricao_manutencao = txt_descricao_manutencao.getText().toString().trim();
                String limite_km = txt_limite_km.getText().toString().trim();
                String limite_tempo_meses = txt_limite_tempo_meses.getText().toString().trim();
                String km_antecipacao = txt_km_antecipacao.getText().toString().trim();
                String tempo_antecipacao = txt_tempo_antecipacao.getText().toString().trim();
                String data_ultima_manutencao = txt_data_ultima_manutencao.getText().toString().trim();
                String km_ultima_manutencao = txt_km_ultima_manutencao.getText().toString().trim();

                if(!descricao_manutencao.isEmpty() && !limite_km.isEmpty() && !limite_tempo_meses.isEmpty() && !km_antecipacao.isEmpty() &&
                        !tempo_antecipacao.isEmpty() && !data_ultima_manutencao.isEmpty() && !km_ultima_manutencao.isEmpty()) {

                    criaManutencaoPersonalizada(placa_veiculo,txt_descricao_manutencao.getText().toString(), txt_limite_km.getText().toString(),
                            txt_limite_tempo_meses.getText().toString(), txt_km_antecipacao.getText().toString(), txt_tempo_antecipacao.getText().toString(),
                            MaskEditUtil.formatarData(txt_data_ultima_manutencao.getText().toString(), "dd/MM/yyyy", "yyyy-MM-dd"), txt_km_ultima_manutencao.getText().toString());

                } else {

                    Toast.makeText(getContext(), "Preencha os campos em branco!", Toast.LENGTH_SHORT).show();

                }

            }
        });

        clickTxtDataUltimaManutencao();

        return view;
    }

    final Calendar myCalendar = Calendar.getInstance();
    private void clickTxtDataUltimaManutencao() {
        //Abrir calendario para pedir da data da manutencao
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                atualizarTxtDataAposEscolherDataNoDataPicker();
            }

        };


        //setOnFocusChangeListener é o evento usado para ao clicar na caixa abrir o datapicker
        txt_data_ultima_manutencao.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus) {

                    new DatePickerDialog(getContext(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                }
            }
        });

    }

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
                        mostraAlertDialogDeCancelarCriacaoDaManutencaoPersonalizada();
                        return true;
                    }
                    return false;
                }
            });
        }


    }

    public void mostraAlertDialogDeCancelarCriacaoDaManutencaoPersonalizada () {

        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
        alerta.setTitle("Criação de manutenções");
        alerta.setMessage("Deseja realmente criar o veículo sem cadastro de manutenções personalizadas?");
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

    private void atualizarTxtDataAposEscolherDataNoDataPicker() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

        txt_data_ultima_manutencao.setText(sdf.format(myCalendar.getTime()));
    }

    private void criaManutencaoPersonalizada(final String placa, final String descricao, final String limite_km, final String limite_tempo_meses, final String km_antecipacao, final String tempo_antecipacao, final String data_ultima_manutencao, final String km_ultima_manutencao) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CRIAR_MANUTENCAO_PERSONALIZADA_VEICULO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i(TAG, "Criar manutencao personalizada Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        Toast.makeText(mContext, "Manutenção criada com sucesso", Toast.LENGTH_LONG).show();
                        getActivity().finish();

                    } else {

                        Toast.makeText(mContext, "Não foi possível criar manutenção", Toast.LENGTH_LONG).show();

                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Criar manutencao personalizada Error: " + error.getMessage());
                Toast.makeText(getContext(),"Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("placa_veiculo_usuario",placa);
                params.put("descricao",descricao);
                params.put("limite_km",limite_km);
                params.put("limite_tempo_meses",limite_tempo_meses);
                params.put("km_antecipacao",km_antecipacao);
                params.put("tempo_antecipacao",tempo_antecipacao);
                params.put("data_ultima_manutencao",data_ultima_manutencao);
                params.put("km_ultima_manutencao",km_ultima_manutencao);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }


}
