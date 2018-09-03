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
public class DetalhesManutencaoRecomendadaFragment extends Fragment {


    TextView lbl_modelo_veiculo, lbl_km_veiculo, lbl_placa_veiculo, lbl_descricao_manutencao, lbl_limite_km_manutencao, lbl_limite_tempo_meses_manutencao;
    EditText txt_km_antecipacao_manutencao, txt_tempo_antecipacao_manutencao, txt_data_ultima_manutencao, txt_km_ultima_manutencao;
    Button btn_salvar;
    String modelo_veiculo, km_veiculo, placa_veiculo;
    int contador_manutencoes = 0;
    String nome_activity_atual;

    public DetalhesManutencaoRecomendadaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detalhes_manutencao_recomendada,container,false);

        nome_activity_atual = getActivity().getClass().getSimpleName();

        lbl_modelo_veiculo = view.findViewById(R.id.lbl_modelo_veiculo_detalhes_manutencao_recomendada);
        lbl_km_veiculo = view.findViewById(R.id.lbl_km_veiculo_detalhes_manutencao_recomendada);
        lbl_placa_veiculo = view.findViewById(R.id.lbl_placa_veiculo_detalhes_manutencao_recomendada);
        lbl_descricao_manutencao = view.findViewById(R.id.lbl_descricao_manutencao_detalhes_manutencao_recomendada);
        lbl_limite_km_manutencao = view.findViewById(R.id.lbl_limite_km_manutencao_detalhes_manutencao_recomendada);
        lbl_limite_tempo_meses_manutencao = view.findViewById(R.id.lbl_limite_tempo_meses_manutencao_detalhes_manutencao_recomendada);
        txt_km_antecipacao_manutencao = view.findViewById(R.id.txt_km_antecipacao_manutencao_detalhes_manutencao_recomendada);
        txt_tempo_antecipacao_manutencao = view.findViewById(R.id.txt_tempo_antecipacao_manutencao_detalhes_manutencao_recomendada);
        txt_data_ultima_manutencao = view.findViewById(R.id.txt_data_ultima_manutencao_detalhes_manutencao_recomendada);
        txt_km_ultima_manutencao = view.findViewById(R.id.txt_km_ultima_manutencao_detalhes_manutencao_recomendada);
        btn_salvar = view.findViewById(R.id.btn_salvar_detalhes_manutencao_recomendada);

        //Obter dados da fragment anterior
        Bundle dados_manutencao_recomendadas = getArguments();
        final ArrayList<ManutencaoRecomendada> lista_manutencoes_selecionadas = (ArrayList<ManutencaoRecomendada>) dados_manutencao_recomendadas.getSerializable("manutencoes_selecionadas");

        modelo_veiculo = dados_manutencao_recomendadas.getString("modelo_veiculo");
        placa_veiculo = dados_manutencao_recomendadas.getString("placa_veiculo");
        km_veiculo = dados_manutencao_recomendadas.getString("km_veiculo");

        lbl_modelo_veiculo.setText(modelo_veiculo);
        lbl_km_veiculo.setText(km_veiculo);
        lbl_placa_veiculo.setText(placa_veiculo);

        if(contador_manutencoes < lista_manutencoes_selecionadas.size()) {
            mostraDadosPrimeiraManutencaoRecomendada(lista_manutencoes_selecionadas);
        }

        if(lista_manutencoes_selecionadas.size() == 1) {
            btn_salvar.setText("Finalizar");
        }

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(contador_manutencoes < lista_manutencoes_selecionadas.size()) {

                    ManutencaoRecomendada manutencao_recomendada = lista_manutencoes_selecionadas.get(contador_manutencoes);
                    criaManutencaoRecomendada(placa_veiculo,manutencao_recomendada.getId_manutencao_padrao(),txt_km_antecipacao_manutencao.getText().toString(),txt_tempo_antecipacao_manutencao.getText().toString(), MaskEditUtil.formatarData(txt_data_ultima_manutencao.getText().toString(),"dd/MM/yyyy", "yyyy-MM-dd"),txt_km_ultima_manutencao.getText().toString());
                    limpaCaixasDeTexto();
                    contador_manutencoes++;

                    if(contador_manutencoes == lista_manutencoes_selecionadas.size() -1 ) {

                        //Verifica se é a ultima manutencao para mudar o texto do botao
                        btn_salvar.setText("Finalizar");

                    }

                    if(contador_manutencoes == lista_manutencoes_selecionadas.size()) {
                        // é o ultimo
                        //Toast.makeText(getContext(), "ultima manutencao", Toast.LENGTH_SHORT).show();
                        perguntaSeDesejaManutencaoPersonalizada();

                    } else {

                        lbl_descricao_manutencao.setText(lista_manutencoes_selecionadas.get(contador_manutencoes).getDescricao());
                        lbl_limite_km_manutencao.setText(lista_manutencoes_selecionadas.get(contador_manutencoes).getLimite_km());
                        lbl_limite_tempo_meses_manutencao.setText(lista_manutencoes_selecionadas.get(contador_manutencoes).getLimite_tempo_meses());

                    }

                }

            }
        });


        clickTxtDataUltimaManutencao();

        // Inflate the layout for this fragment
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

    private void atualizarTxtDataAposEscolherDataNoDataPicker() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

        txt_data_ultima_manutencao.setText(sdf.format(myCalendar.getTime()));
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

                //Verifica qual é a acitivity atual, para assim, abrir a fragment com o frame container correto
                if(nome_activity_atual.equals("AdicionarVeiculoActivity")) {
                    getFragmentManager().beginTransaction().replace(R.id.frame_container_adicionar_veiculo, adicionar_manutencao_personalizada).commit();
                }
                else if (nome_activity_atual.equals("MostraInfoVeiculoActivity")) {
                    getFragmentManager().beginTransaction().replace(R.id.frame_container_mostra_info_veiculo, adicionar_manutencao_personalizada).commit();
                }


            }
        });

        AlertDialog alertDialog = alerta.create();
        alertDialog.show();
    }

    public void mostraDadosPrimeiraManutencaoRecomendada(ArrayList<ManutencaoRecomendada> lista_manutencoes_recomendadas) {

        lbl_descricao_manutencao.setText(lista_manutencoes_recomendadas.get(contador_manutencoes).getDescricao());
        lbl_limite_km_manutencao.setText(lista_manutencoes_recomendadas.get(contador_manutencoes).getLimite_km());
        lbl_limite_tempo_meses_manutencao.setText(lista_manutencoes_recomendadas.get(contador_manutencoes).getLimite_tempo_meses());

    }

    private void limpaCaixasDeTexto() {
        txt_km_ultima_manutencao.setText("");
        txt_data_ultima_manutencao.setText("");
        txt_tempo_antecipacao_manutencao.setText("");
        txt_km_antecipacao_manutencao.setText("");

    }

    private void criaManutencaoRecomendada(final String placa, final String id_manutencao_padrao, final String km_antecipacao, final String tempo_antecipacao, final String data_ultima_manutencao, final String km_ultima_manutencao) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CRIAR_MANUTENCAO_RECOMENDADA_VEICULO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("teste", "Criar manutencao recomendada Response: " + response.toString());

                try {
                    JSONObject object = new JSONObject(response);

                    boolean error = object.getBoolean("error");

                    if(!error) {

                        Toast.makeText(getContext(), "Manutenção criada com sucesso", Toast.LENGTH_SHORT).show();


                    } else {

                        Toast.makeText(getContext(), "Não foi possível criar manutenção", Toast.LENGTH_SHORT).show();

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
                Log.e("teste", "Criar manutencao recomendada Error: " + error.getMessage());
                Toast.makeText(getContext(),"Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("placa_veiculo_usuario",placa);
                params.put("id_manutencao_padrao",id_manutencao_padrao);
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
