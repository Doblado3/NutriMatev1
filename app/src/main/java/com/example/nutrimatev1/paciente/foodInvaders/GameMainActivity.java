package com.example.nutrimatev1.paciente.foodInvaders;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class GameMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(new SpaceShooter(this));
    }
}
