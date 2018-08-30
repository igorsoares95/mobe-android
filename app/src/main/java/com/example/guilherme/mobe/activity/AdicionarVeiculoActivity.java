package com.example.guilherme.mobe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.guilherme.mobe.R;
import com.example.guilherme.mobe.fragments.AdicionarCarroFragment;


public class AdicionarVeiculoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_veiculo);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_container_adicionar_veiculo, new AdicionarCarroFragment())
                .commit();
    }
}
