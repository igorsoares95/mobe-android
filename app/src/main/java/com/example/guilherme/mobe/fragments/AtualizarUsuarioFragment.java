package com.example.guilherme.mobe.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.activity.LoginActivity;
import com.example.guilherme.mobe.activity.MainActivity;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.helper.SessionManager;
import com.example.guilherme.mobe.helper.SQLiteHandler;
import com.example.guilherme.mobe.activity.RegistroActivity;
import com.example.guilherme.mobe.helper.MaskEditUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class AtualizarUsuarioFragment extends Fragment {


    public AtualizarUsuarioFragment() {
        // Required empty public constructor
    }

    private static final String TAG = AtualizarUsuarioFragment.class.getSimpleName();
    private SQLiteHandler bd;
    private SessionManager session;
    private EditText atualizaNome;
    private TextView atualizaEmail;
    private EditText atualizaTelefone;
    private Button btnSalvar;
    private Button btnAlterarSenha;
    private Button btnDesativarUsuario;
    private ProgressDialog pDialog;

    //private ProgressDialog pDialog = new ProgressDialog(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_atualizar_usuario, container, false);



        atualizaNome = view.findViewById(R.id.nome_atualizaUsuario);
        atualizaEmail = (TextView) view.findViewById(R.id.email_atualizaUsuario);
        atualizaTelefone = (EditText) view.findViewById(R.id.telefone_atualizaUsuario);
        btnSalvar = (Button) view.findViewById(R.id.btnSalvar_atualizaUsuario);
        btnAlterarSenha = (Button) view.findViewById(R.id.btnAlterarSenha_atualizaUsuario);
        btnDesativarUsuario = (Button) view.findViewById(R.id.btn_desativar__atualizar_usuario);
        pDialog = new ProgressDialog(getActivity());
        bd = new SQLiteHandler(getActivity().getApplicationContext());
        session = new SessionManager(getActivity().getApplicationContext());

        HashMap<String, String> usuario = bd.getUserDetails();
        String nome = usuario.get("S_NOME");
        final String email = usuario.get("S_EMAIL");
        String telefone = usuario.get("N_TELEFONE");

        atualizaNome.setText(nome);
        atualizaEmail.setText(email);
        atualizaTelefone.setText(telefone);

        atualizaTelefone.addTextChangedListener(MaskEditUtil.mask(atualizaTelefone, MaskEditUtil.FORMAT_FONE));

        btnDesativarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                alerta.setTitle("Desativar conta");
                alerta.setMessage("Deseja realmente desativar essa conta?");
                alerta.setCancelable(false);
                alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Caso clique em nao, o app nao faz nada e mantem na mesma tela;
                    }
                });

                alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Caso clique em sim, será feito o logout
                        desativaUsuario(email);

                    }
                });

                AlertDialog alertDialog = alerta.create();
                alertDialog.show();


            }
        });


        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                atualizaUsuario(atualizaNome.getText().toString(), atualizaEmail.getText().toString(), atualizaTelefone.getText().toString());

            }
        });

        btnAlterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, new AtualizarSenhaFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
    private void atualizaUsuario(final String nome, final String email, final String telefone) {

        //pDialog.setMessage("Atualizando...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ATUALIZAR_USUARIO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response Atualizar Usuario: " + response);
                //hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        JSONObject usuario = jObj.getJSONObject("usuario");
                        String nome = usuario.getString("nome");
                        String email = usuario.getString("email");
                        int id_usuario = usuario.getInt("id");
                        String telefone = usuario.getString("telefone");
                        bd.updateUser(nome, email, id_usuario, telefone);
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_container, new AtualizarUsuarioFragment())
                                .commit();

                        Toast.makeText(getActivity().getApplicationContext(), "Usuário atualizado com sucesso!", Toast.LENGTH_LONG).show();





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

                Log.e(TAG, "Erro de atualização: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nome", nome);
                params.put("email", email);
                params.put("telefone", telefone);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void desativaUsuario(final String email) {


        pDialog.setMessage("Desativando...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INATIVAR_USUARIO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update user Response: " + response.toString());
                hideDialog();


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Usuário desativado com sucesso!", Toast.LENGTH_LONG).show();
                        logoutUser();


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email",email);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private void logoutUser() {
        session.setLogin(false);
        bd.deleteUsers();

        // Launching the login activity
        //Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        getActivity().finish();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

    }


    private void showDialog() {

        if(!pDialog.isShowing())
            pDialog.show();

    }

    private void hideDialog() {

        if(pDialog.isShowing())
            pDialog.dismiss();

    }
}
