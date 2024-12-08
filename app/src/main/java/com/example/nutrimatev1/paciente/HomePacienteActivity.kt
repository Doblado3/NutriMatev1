package com.example.nutrimatev1.paciente

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nutrimatev1.R

class HomePacienteActivity : BasePaciente() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_paciente)
        setSupportActionBar(findViewById(R.id.toolbar))


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Asignamos onClickListeners a los botones
        findViewById<View>(R.id.LLInfo).setOnClickListener { onClickHomePacientes(it) }
        findViewById<View>(R.id.LLHistorial).setOnClickListener { onClickHomePacientes(it) }
        findViewById<View>(R.id.LLTests).setOnClickListener { onClickHomePacientes(it) }
        findViewById<View>(R.id.LLDiviertete).setOnClickListener { onClickHomePacientes(it) }


    }

    //Función para clickear los botones
    //Solo implementado para el botón de tests
    fun onClickHomePacientes(v: View?) {
        when (v?.id) {
            R.id.LLTests -> {
                intent = Intent(this, TestsActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Toast.makeText(this, "Aún no hay activity", Toast.LENGTH_SHORT).show()
            }
        }
    }


}