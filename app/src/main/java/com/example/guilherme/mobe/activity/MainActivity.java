package com.example.guilherme.mobe.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.content.LocalBroadcastManager;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.app.AppConfig;
import com.example.guilherme.mobe.app.AppController;
import com.example.guilherme.mobe.fragments.AtualizarUsuarioFragment;
import com.example.guilherme.mobe.fragments.ListaVeiculosFragment;
import com.example.guilherme.mobe.fragments.ManutencoesAtrasadasFragment;
import com.example.guilherme.mobe.helper.SQLiteHandler;
import com.example.guilherme.mobe.helper.SessionManager;
import com.example.guilherme.mobe.app.Config;


import com.example.guilherme.mobe.util.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SQLiteHandler bd;
    private SessionManager session;
    private TextView txtNome;
    private TextView txtEmail;
    private TextView txtTelefone;
    private Button btnLogout;
    private Button btnDesativarConta;
    private ProgressDialog pDialog;
    String nome_usuario_main;
    String email_usuario_main;
    private NavigationView navigationView;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        bd = new SQLiteHandler(this.getApplicationContext());
        session = new SessionManager(this.getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        HashMap<String, String> usuario = bd.getUserDetails();
        nome_usuario_main = usuario.get("S_NOME");
        email_usuario_main = usuario.get("S_EMAIL");

        // Adicionar nome e email do usuario no nav_header_main
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navNomeUsuario = (TextView) headerView.findViewById(R.id.txt_nome_usuario_main);
        navNomeUsuario.setText(nome_usuario_main);
        TextView navEmailUsuario = (TextView) headerView.findViewById(R.id.txt_email_usuario_main);
        navEmailUsuario.setText(email_usuario_main);
        //-----------------------------------------------------------

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frame_container, new ManutencoesAtrasadasFragment())
                    .addToBackStack(null).commit();
        }

        //--------teste------------

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();


                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    //---------------teste-------------------------------

    private void logoutUsuario() {

        pDialog.setMessage("Saindo ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_LOGOUT_USUARIO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Logout Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {

                        session.setLogin(false);
                        bd.deleteUsers();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
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
                Log.e(TAG, "Logout error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email_usuario_main);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_man_atras) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, new ManutencoesAtrasadasFragment())
                    .addToBackStack(null).commit();

        } else if (id == R.id.nav_man_fut) {

        } else if (id == R.id.nav_meus_veiculos) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, new ListaVeiculosFragment())
                    .addToBackStack(null).commit();

        } else if (id == R.id.nav_minha_conta) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, new AtualizarUsuarioFragment())
                    .addToBackStack(null).commit();

        } else if (id == R.id.nav_logout) {

            AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
            alerta.setTitle("Logout");
            alerta.setMessage("Deseja realmente fazer logout?");
            alerta.setCancelable(false);
            alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Caso clique em não o app não faz nada
                }
            });

            alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    logoutUsuario();
                }
            });

            AlertDialog alertDialog = alerta.create();
            alertDialog.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
