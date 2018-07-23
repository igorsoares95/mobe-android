package com.example.guilherme.mobe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v7.app.ActionBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AtivarUsuarioActivity extends AppCompatActivity {

    private static final String TAG = AtivarUsuarioActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private Button btnAtivarUsuario;
    String email;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ativar_usuario);

        btnAtivarUsuario = (Button) findViewById(R.id.ativar_usuario);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Ativar usuário");




        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        email = bundle.getString("email");

        btnAtivarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ativaUsuario(email);
            }
        });
    }

    private void ativaUsuario(final String email) {
        pDialog.setMessage("Ativando ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ATIVAR_USUARIO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Ativar usuario Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Usuario ativado com sucesso", Toast.LENGTH_LONG).show();
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
                Log.e(TAG, "Login error: " + error.getMessage());
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

