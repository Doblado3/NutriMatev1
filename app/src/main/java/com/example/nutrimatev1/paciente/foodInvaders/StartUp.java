package com.example.nutrimatev1.paciente.foodInvaders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nutrimatev1.R;

public class StartUp extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup);
    }

    public void startGame(View v){
        startActivity(new Intent(this, GameMainActivity.class));
        finish();
    }
}
