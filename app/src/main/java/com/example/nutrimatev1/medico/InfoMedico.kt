package com.example.nutrimatev1.medico

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nutrimatev1.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class InfoMedico : AppCompatActivity() {

    private lateinit var telefono: EditText
    private lateinit var textoSexo: TextView
    private lateinit var emailMed: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info_medico)

        telefono = findViewById(R.id.editTextPhoneMed)
        textoSexo = findViewById(R.id.textSexMed)
        emailMed = findViewById(R.id.editTextTextEmailMed)

        val user = Firebase.auth.currentUser
        user?.let {
            emailMed.setText(user.email)
            emailMed.isEnabled = false
        }


    }

    fun onClickUpdatePhone(v: View?) {
        val e1 = Regex("^(\\+34|0034|34)?[67]\\d{8}\$")
        if (!e1.matches(telefono.text.toString())) {
            Log.e("ERROR", "El teléfono introducido no es correcto")
            telefono.error = "Teléfono Incorrecto"
        }
    }

    fun onRadioButtonClicked(v: View?) {
        var texto = ""
        when (v?.id) {
            R.id.radioHombre -> texto = "Hombre"
            R.id.radioMujer -> texto = "Mujer"
            R.id.radioNC -> texto = "N/C"
        }

        textoSexo.text = "Sexo: $texto"
    }
}