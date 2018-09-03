package com.example.guilherme.mobe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.app.Config;
import com.example.guilherme.mobe.helper.SQLiteHandler;
import com.example.guilherme.mobe.helper.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkRegistro;
    private EditText insereEmail;
    private EditText insereSenha;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        insereEmail = findViewById(R.id.email_login);
        insereSenha = findViewById(R.id.senha_login);
        btnLogin = findViewById(R.id.login);
        btnLinkRegistro = findViewById(R.id.registro);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        bd = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = insereEmail.getText().toString().trim();
                String senha = insereSenha.getText().toString().trim();

                if (!email.isEmpty() && !senha.isEmpty()) {
                    realizaLogin(email, senha);
                } else {
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            }

        });

        btnLinkRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void btnLinkEsqueciSenha(View view) {
        startActivity(new Intent(this, EsqueciSenhaActivity.class));
    }

    private void realizaLogin(final String email, final String senha) {
        pDialog.setMessage("Entrando ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        session.setLogin(true);
                        JSONObject usuario = jObj.getJSONObject("usuario");
                        String nome = usuario.getString("nome");
                        String email = usuario.getString("email");
                        int id_usuario = usuario.getInt("id");
                        String telefone = usuario.getString("telefone");
                        bd.addUser(nome, email, id_usuario, telefone);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();


                        if(errorMsg.equals("Usuario inativo")) {

                            JSONObject usuario = jObj.getJSONObject("usuario");

                            int inativo = usuario.getInt("inativo");

                            if (inativo == 1) {

                                String email = usuario.getString("email");

                                Intent intent = new Intent(LoginActivity.this, AtivarUsuarioActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("email",email);
                                intent.putExtras(bundle);
                                startActivity(intent);

                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("senha", senha);
                params.put("reg_id",obtemFirebaseRegId());


                return params;
            }

        };

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

    // Fetches reg id from shared preferences
    // and displays on the screen
    private String obtemFirebaseRegId() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.i(TAG, "Firebase reg id: " + regId);

        return regId;

    }
}





