package com.example.guilherme.mobe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.app.AppConfig;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EsqueciSenhaActivity extends AppCompatActivity {

    private static final String TAG = RegistroActivity.class.getSimpleName();
    private TextView insereEmail;
    private Button btnEnviaNovaSenha;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);

        insereEmail = (TextView) findViewById(R.id.email_esqueci_senha);
        btnEnviaNovaSenha = (Button) findViewById(R.id.enviar);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Esqueci Senha");

        btnEnviaNovaSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!insereEmail.getText().toString().trim().isEmpty()) {
                    esqueciSenha(insereEmail.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Preencha o campo email", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void esqueciSenha(final String email){
        pDialog.setMessage("Enviando ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_ESQUECI_SENHA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Esqueci senha Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Email enviado com sucesso", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Erro ao atualizar senha: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
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

