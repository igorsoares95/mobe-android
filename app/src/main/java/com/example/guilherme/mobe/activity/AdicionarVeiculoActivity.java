package com.example.guilherme.mobe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
            Fragment fragment_atual = getSupportFragmentManager().findFragmentById(R.id.frame_container_adicionar_veiculo);
            String nome_fragment_atual = fragment_atual.getClass().getSimpleName();
            if(nome_fragment_atual.equals("AdicionarCarroFragment")) {
                mostraAlertDialogDeCancelarCriacaoVeiculo();
            } else if (nome_fragment_atual.equals("MostraManutencoesRecomendadasDoVeiculo")) {
                mostraAlertDialogDeCancelarCriacaoDaManutencaoRecomendada();
            } else if (nome_fragment_atual.equals("DetalhesManutencaoRecomendadaFragment")) {
                //voltar para a fragment anterior
                getSupportFragmentManager().popBackStack();
            } else if (nome_fragment_atual.equals("AdicionarManutencaoPersonalizadaFragment")) {
                mostraAlertDialogDeCancelarCriacaoDaManutencaoPersonalizada();
            }

        }

        return super.onOptionsItemSelected(item);
    }

/*
    //Botao back da action bar
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){

            if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
                Log.i("teste", "getBackStackEntryCount = 1");
            } else if(getSupportFragmentManager().getBackStackEntryCount() == 2){
                Log.i("teste", "getBackStackEntryCount = 2");
            } else if(getSupportFragmentManager().getBackStackEntryCount() == 3) {
                Log.i("teste", "getBackStackEntryCount = 3");
            } else if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
                Log.i("teste", "getBackStackEntryCount = 0");
            }

        }

        return super.onOptionsItemSelected(item);
    }
    */



    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            if (this != null) {
                this.onBackPressed();
            }
            return true;
        };
        return super.onOptionsItemSelected(item);
    }
    */


/*
    //Botao back da action bar, se estiver na primeira fragment fecha a activity se nao volta para a fragment anterior
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){

            if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
                //getSupportFragmentManager().popBackStack();
                mostraAlertDialogDeCancelarCriacaoDaManutencao();
            } else {
                //this.finish();
                mostraAlertDialogDeCancelarCriacaoVeiculo();
            }

        }

        return super.onOptionsItemSelected(item);
    }
    */

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

    public void mostraAlertDialogDeCancelarCriacaoDaManutencaoRecomendada () {

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Criação de manutenções");
        alerta.setMessage("Deseja realmente criar o veículo sem cadastro de manutenções recomendadas?");
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

    public void mostraAlertDialogDeCancelarCriacaoDaManutencaoPersonalizada () {

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Criação de manutenções");
        alerta.setMessage("Deseja realmente criar o veículo sem cadastro de manutenções personalizadas?");
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
