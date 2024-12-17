package com.example.nutrimatev1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrimatev1.medico.MainMedico
import com.example.nutrimatev1.modelo.Alert
import com.example.nutrimatev1.paciente.MainPaciente
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    //Variables inicializadas para asignarlas luego
    private lateinit var auth: FirebaseAuth

    private lateinit var email: EditText
    private lateinit var password: EditText


    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        //Asignacion de variables del xml con lo del xml
        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)


        btnLogin = findViewById(R.id.AUbtnLogin)

        auth =Firebase.auth

        //Funciones para ejecutar en el main
        setup()
    }

    fun setup(){
        //Lógica botones de autenticación


        btnLogin.setOnClickListener{
            if(email.text.isNotBlank() && password.text.isNotBlank()){

                auth.signInWithEmailAndPassword(

                    email.text.toString(),
                    password.text.toString()
                ).addOnCompleteListener{ task ->

                    if(task.isSuccessful){
                        Log.i("INFO","Usuario logueado correctamente")
                        showHome(email.text.toString())
                        //intent = Intent(this, InfoMedico::class.java)
                        //startActivity(intent)


                        email.text.clear()
                        password.text.clear()
                    }else{
                        Alert.showAlert(this,"Error logueando al usuario")
                    }

                }
            }

        }
    }

    //Mensaje de error en el registro


    private fun showHome(email: String) {
        val col = FirebaseFirestore.getInstance()

        // Verificar en la colección "medicos" usando el email como ID
        col.collection("Medicos").document(email).get()
            .addOnSuccessListener { medicoDoc ->
                if (medicoDoc.exists()) {
                    // Si el email está en "medicos", redirige a HomeMedicoActivity
                    startActivity(Intent(this, MainMedico::class.java))
                } else {
                    // Verificar en la colección "pacientes" usando el email como ID
                    col.collection("Pacientes").document(email).get()
                        .addOnSuccessListener { pacienteDoc ->
                            if (pacienteDoc.exists()) {
                                // Si el email está en "pacientes", redirige a HomePaciente
                                startActivity(Intent(this, MainPaciente::class.java))
                            } else {
                                // Manejar el caso en el que el email no esté en ninguna colección
                                Alert.showAlert(this,"El usuario no pertenece a ningún rol válido")
                            }
                        }
                        .addOnFailureListener {
                            Alert.showAlert(this,"Error al comprobar la colección de pacientes")
                        }
                }
            }
            .addOnFailureListener {
                Alert.showAlert(this,"Error al comprobar la colección de médicos")
            }
    }





}