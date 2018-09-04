package com.example.guilherme.mobe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.fragments.AdicionarCarroFragment;


public class AdicionarVeiculoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_veiculo);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Adicionar Veículo");

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_container_adicionar_veiculo, new AdicionarCarroFragment())
                .commit();
    }

    //Botao back da action bar
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){

            mostraAlertDialogDeCancelarCriacaoVeiculo();
        }

        return super.onOptionsItemSelected(item);
    }

    public void mostraAlertDialogDeCancelarCriacaoVeiculo () {

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Criação do veículo");
        alerta.setMessage("Deseja realmente cancelar a criação desse veículo?");
        alerta.setCancelable(false);
        alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Caso clique em não o app não faz nada
            }
        });

        alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog alertDialog = alerta.create();
        alertDialog.show();

    }

}
