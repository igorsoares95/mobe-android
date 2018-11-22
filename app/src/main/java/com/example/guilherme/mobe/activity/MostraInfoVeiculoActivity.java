package com.example.guilherme.mobe.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.fragments.MostraInfoVeiculoFragment;

public class MostraInfoVeiculoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_info_veiculo);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setTitle("Meu Veículo");

        // pegar o bundle da fragment ListaVeiculosFragment e passa para a MostraInfoVeiculoFragment
        Intent intent = getIntent();
        Bundle dados_do_veiculo = intent.getExtras();

        MostraInfoVeiculoFragment mostra_info_veiculo_fragment = new MostraInfoVeiculoFragment();
        mostra_info_veiculo_fragment.setArguments(dados_do_veiculo);

        getSupportFragmentManager().beginTransaction().add(R.id.frame_container_mostra_info_veiculo, mostra_info_veiculo_fragment).commit();
    }


    //Botao back da action bar, se estiver na primeira fragment fecha a activity se nao volta para a fragment anterior
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == android.R.id.home){

            Fragment fragment_atual = getSupportFragmentManager().findFragmentById(R.id.frame_container_mostra_info_veiculo);
            String nome_fragment_atual = fragment_atual.getClass().getSimpleName();
            if(nome_fragment_atual.equals("MostraInfoVeiculoFragment")) {
                this.finish();
            } else if (nome_fragment_atual.equals("MostraManutencoesRecomendadasDoVeiculo")) {
                getSupportFragmentManager().popBackStack();
            } else if (nome_fragment_atual.equals("DetalhesManutencaoRecomendadaFragment")) {
                //voltar para a fragment anterior
                getSupportFragmentManager().popBackStack();
            } else if (nome_fragment_atual.equals("AdicionarManutencaoPersonalizadaFragment")) {
                getSupportFragmentManager().popBackStack();
            } else if (nome_fragment_atual.equals("MostraManutencoesDoVeiculo")) {
                getSupportFragmentManager().popBackStack();
            } else if (nome_fragment_atual.equals("DetalhesManutencaoDoVeiculoFragment")) {
                getSupportFragmentManager().popBackStack();
            }

        }

        return super.onOptionsItemSelected(item);
    }


}
