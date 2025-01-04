package com.example.nutrimatev1.paciente;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutrimatev1.R;

public class GameMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(new SpaceShooter(this));
    }
}
