package com.example.nutrimatev1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AuthActivity : AppCompatActivity() {
    //Variables inicializadas para asignarlas luego


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        //Asignacion de variables del xml con lo del xml

        //Funciones para ejecutar en el main
        setup()
    }

    fun setup(){

    }

}