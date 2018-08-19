package com.example.guilherme.mobe.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.activity.RegistroActivity;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AtualizarSenhaFragment extends Fragment {


    public AtualizarSenhaFragment() {
        // Required empty public constructor
    }
    private static final String TAG = RegistroActivity.class.getSimpleName();
    private EditText txtSenhaAntiga;
    private EditText txtNovaSenha;
    private EditText txtConfitmarNovaSenha;
    private Button botaoAlterarSenha;
    private SQLiteHandler bd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Alterar Senha");


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_atualizar_senha, container, false);
        txtSenhaAntiga = (EditText) view.findViewById(R.id.senhaAtual_atualizarSenha);
        txtNovaSenha = (EditText) view.findViewById(R.id.novaSenha_atualizarSenha);
        txtConfitmarNovaSenha = (EditText) view.findViewById(R.id.confirmarSenha_atualizarSenha);
        botaoAlterarSenha = (Button) view.findViewById(R.id.botaoAlterar_atualizarSenha);
        bd = new SQLiteHandler(getActivity().getApplicationContext());

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
                            Toast.makeText(getActivity().getApplicationContext(), "Senhas não coincidem. Tente novamente.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Senha deve conter no mínimo 4 caracteres", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    private void atualizaSenhaUsuario(final String email, final String senhaAntiga, final String novaSenha) {
        String tag_string_req = "req_update_passwd_user";

        //pDialog.setMessage("Atualizando...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ATUALIZAR_SENHA_USUARIO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update password user Response: " + response);
                //hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getActivity().getApplicationContext(), "Senha atualizada com sucesso", Toast.LENGTH_LONG).show();
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_container, new AtualizarUsuarioFragment()).commit();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Password Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
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

}
