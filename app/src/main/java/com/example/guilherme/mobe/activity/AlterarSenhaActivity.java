package com.example.guilherme.mobe.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.fragments.AtualizarUsuarioFragment;
import com.example.guilherme.mobe.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AlterarSenhaActivity extends AppCompatActivity {

    private static final String TAG = RegistroActivity.class.getSimpleName();
    private EditText txtSenhaAntiga;
    private EditText txtNovaSenha;
    private EditText txtConfitmarNovaSenha;
    private Button botaoAlterarSenha;
    private ProgressDialog pDialog;
    private SQLiteHandler bd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_senha);
        this.setTitle("Alterar Senha");


        txtSenhaAntiga = (EditText) findViewById(R.id.senhaAtual_atualizarSenha);
        txtNovaSenha = (EditText) findViewById(R.id.novaSenha_atualizarSenha);
        txtConfitmarNovaSenha = (EditText) findViewById(R.id.confirmarSenha_atualizarSenha);
        botaoAlterarSenha = (Button) findViewById(R.id.botaoAlterar_atualizarSenha);
        bd = new SQLiteHandler(getApplicationContext());
        pDialog = new ProgressDialog(getApplicationContext());

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_close_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        HashMap<String, String> usuario = bd.getUserDetails();
        final String email = usuario.get("S_EMAIL");

        botaoAlterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String senhaAntiga = txtSenhaAntiga.getText().toString().trim();
                String novaSenha = txtNovaSenha.getText().toString().trim();
                String confirmarNovaSenha = txtConfitmarNovaSenha.getText().toString().trim();

                if(!senhaAntiga.isEmpty() && !novaSenha.isEmpty() && !confirmarNovaSenha.isEmpty()) {
                    if (txtNovaSenha.getText().toString().trim().length() >= 4) {
                        if (txtConfitmarNovaSenha.getText().toString().trim().equals(txtNovaSenha.getText().toString().trim())) {
                            atualizaSenhaUsuario(email, txtSenhaAntiga.getText().toString(), txtNovaSenha.getText().toString());
                        } else {
                            Toast.makeText(getApplicationContext(), "Senhas não coincidem. Tente novamente.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Senha deve conter no mínimo 4 caracteres", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void atualizaSenhaUsuario(final String email, final String senhaAntiga, final String novaSenha) {
        String tag_string_req = "req_update_passwd_user";

        pDialog.setMessage("Atualizando...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ATUALIZAR_SENHA_USUARIO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Alteração de senha Response: " + response);
                //hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Senha atualizada com sucesso", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Atualização de senha Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("senha_antiga", senhaAntiga);
                params.put("senha_nova", novaSenha);
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void showDialog () {

        if (!pDialog.isShowing())
            pDialog.show();

    }

    private void hideDialog () {

        if (pDialog.isShowing())
            pDialog.dismiss();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
