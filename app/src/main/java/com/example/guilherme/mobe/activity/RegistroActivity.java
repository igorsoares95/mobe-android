package com.example.guilherme.mobe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.helper.MaskEditUtil;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

public class RegistroActivity extends Activity {

    private static final String TAG = RegistroActivity.class.getSimpleName();
    private Button btnRegistro;
    private Button btnLinkLogin;
    private EditText insereNomeCompleto;
    private EditText insereEmail;
    private EditText insereTelefone;
    private EditText insereSenha;
    private EditText insereConfirmaSenha;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        insereNomeCompleto = (EditText) findViewById(R.id.nome_registro);
        insereEmail = (EditText) findViewById(R.id.email_registro);
        insereTelefone = (EditText) findViewById(R.id.telefone_registro);
        insereSenha = (EditText) findViewById(R.id.senha_registro);
        insereConfirmaSenha = (EditText) findViewById(R.id.confirma_senha_registro);
        btnRegistro = (Button) findViewById(R.id.btn_registro);
        btnLinkLogin = (Button) findViewById(R.id.btnLinkLogin);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        //mascara do github para mascarar as edittext - link no trello de como usar
        SimpleMaskFormatter smf = new SimpleMaskFormatter("(NN)NNNN - NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(insereTelefone, smf);
        insereTelefone.addTextChangedListener(mtw);

        //insereTelefone.addTextChangedListener(MaskEditUtil.mask(insereTelefone, MaskEditUtil.FORMAT_FONE));

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = insereNomeCompleto.getText().toString().trim();
                String email = insereEmail.getText().toString().trim();
                String telefone = insereTelefone.getText().toString().trim();
                String senha = insereSenha.getText().toString().trim();
                String confirma_senha = insereConfirmaSenha.getText().toString().trim();

                if(!nome.isEmpty() && !email.isEmpty() && !telefone.isEmpty() && !senha.isEmpty() && !confirma_senha.isEmpty()) {
                    if(insereSenha.getText().toString().trim().length() >= 4) {
                        if(insereSenha.getText().toString().trim().equals(insereConfirmaSenha.getText().toString().trim())) {

                            registrarUsuario(nome, email, telefone, senha);

                        } else {
                            Toast.makeText(getApplicationContext(), "Senhas não coincidem. Tente novamente", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Senha deve conter no mínimo 4 caracteres", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }

            }
        });


        btnLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    /*Método para utilizar o botão retornar do sistema*/
    public void onBackPressed() {

        startActivity(new Intent(this, LoginActivity.class));
        finish();
        return;

    }

    private void registrarUsuario(final String nome, final String email, final String telefone, final String senha) {

        pDialog.setMessage("Registrando...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTRO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Registro Response: " + response);
                hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        Toast.makeText(getApplicationContext(), "Usuário registrado com sucesso! Foi enviado um link de confirmação para o seu email", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(RegistroActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "Erro Registro: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {

            protected Map<String, String> getParams(){

                Map<String, String> params = new HashMap<>();
                params.put("nome", nome);
                params.put("email", email);
                params.put("telefone", telefone);
                params.put("senha", senha);
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

