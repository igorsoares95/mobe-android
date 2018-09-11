package com.example.guilherme.mobe.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.telephony.PhoneNumberUtils;
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
import com.example.guilherme.mobe.activity.AlterarSenhaActivity;
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
    private TextView atualizaNome;
    private TextView atualizaEmail;
    private EditText atualizaTelefone;
    private AppCompatImageButton btnAlterarTelefone;
    private Button btnAlterarSenha;
    private Button btnDesativarUsuario;
    private ProgressDialog pDialog;

    //private ProgressDialog pDialog = new ProgressDialog(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Atualizar dados pessoais");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_atualizar_usuario, container, false);



        atualizaNome = (TextView) view.findViewById(R.id.txtNome_atualiza_usuario);
        atualizaEmail = (TextView) view.findViewById(R.id.txtEmail_atualiza_usuario);
        atualizaTelefone = (EditText) view.findViewById(R.id.txtTelefone_atualiza_usuario);
        btnAlterarSenha = (Button) view.findViewById(R.id.btn_alterar_senha_mostra_info_usuario);
        btnAlterarTelefone = (AppCompatImageButton) view.findViewById(R.id.btnAlterarTelefone_mostra_info_usuario);
        btnDesativarUsuario = (Button) view.findViewById(R.id.btn_desativar__atualizar_usuario);
        pDialog = new ProgressDialog(getContext());
        bd = new SQLiteHandler(getActivity().getApplicationContext());
        session = new SessionManager(getActivity().getApplicationContext());

        mostraInfoUsuario();
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
                        desativaUsuario(getEmailUsuario());

                    }
                });

                AlertDialog alertDialog = alerta.create();
                alertDialog.show();


            }
        });


        btnAlterarTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showInputDialog();

            }
        });

        btnAlterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlterarSenhaActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mostraInfoUsuario();
    }

    private void mostraInfoUsuario() {

        HashMap<String, String> usuario = bd.getUserDetails();
        String nome = usuario.get("S_NOME");
        final String email = usuario.get("S_EMAIL");
        String telefone = usuario.get("N_TELEFONE");

        atualizaNome.setText(nome);
        atualizaEmail.setText(email);
        atualizaTelefone.setEnabled(false);
        atualizaTelefone.setText(telefone);

    }

    private String getEmailUsuario() {
        HashMap<String, String> usuario = bd.getUserDetails();
        final String email = usuario.get("S_EMAIL");
        return email;
    }

    private void atualizaUsuario(final String nome, final String email, final String telefone) {

        pDialog.setMessage("Atualizando...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ATUALIZAR_USUARIO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response Atualizar Usuario: " + response);
                hideDialog();

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

                        Toast.makeText(getActivity().getApplicationContext(), "Usuário atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                        onResume();
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        onResume();

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
                Toast.makeText(getActivity().getApplicationContext(), "Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity().getApplicationContext(),"Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
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

    //exibe caixa de dialogo para inserçao do novo telefone, igual alterar km do fragment MostraInfoVeiculoFragment
    private void showInputDialog() {

        LayoutInflater layoutInflater = getLayoutInflater();
        View promptView = layoutInflater.inflate(R.layout.input_dialog,null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptView);

        final EditText txtInput = (EditText) promptView.findViewById(R.id.editTextInput);
            String titulo = "Insira o novo telefone";
            alertDialogBuilder.setCancelable(false).setTitle(titulo)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(atualizaTelefone.equals(txtInput.getText().toString()) || txtInput.getText().toString().isEmpty()) {

                                Toast.makeText(getActivity().getApplicationContext(), "Não foi alterado o telefone", Toast.LENGTH_SHORT).show();

                            } else {

                                //Log.i("teste",atualizaNome.getText().toString().trim() + atualizaEmail.getText().toString().trim() + txtInput.getText().toString().trim());
                                atualizaUsuario(atualizaNome.getText().toString().trim(), atualizaEmail.getText().toString().trim(), txtInput.getText().toString().trim());
                            }

                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });



        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

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
