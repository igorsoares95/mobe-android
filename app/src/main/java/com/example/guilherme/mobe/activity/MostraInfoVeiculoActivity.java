package com.example.guilherme.mobe.activity;

import android.content.Intent;
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

            if(getSupportFragmentManager().getBackStackEntryCount() > 0 ) {
                getSupportFragmentManager().popBackStack();
            } else {
                this.finish();
            }

        }

        return super.onOptionsItemSelected(item);
    }


}
