package com.example.nutrimatev1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {
    //Variables inicializadas para asignarlas luego
    private lateinit var auth: FirebaseAuth

    private lateinit var email: EditText
    private lateinit var password: EditText

    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        //Asignacion de variables del xml con lo del xml
        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)

        btnRegister = findViewById(R.id.AUbtnRegister)
        btnLogin = findViewById(R.id.AUbtnLogin)

        auth =Firebase.auth

        //Funciones para ejecutar en el main
        setup()
    }

    fun setup(){
        //Lógica botones de autenticación
        btnRegister.setOnClickListener {
            // Comprobar que el correo electrónico y la contraseña no estén vacíos
            if(email.text.isNotBlank() && password.text.isNotBlank()){
                // sí podemos registrar al usuario
                //Log.i("INFO", email.text.toString()) //Debug
                //Log.i("INFO", passwd.text.toString())
                auth.createUserWithEmailAndPassword( //metodo concreto para la autenticacion de email y contraseña que hemos elegido
                    email.text.toString(),
                    password.text.toString()
                ).addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        // El registro se ha hecho de forma correcta
                        Log.i("INFO", "El usuario se ha registrado correctamente")
                        //showHome(email.text.toString())
                        email.text.clear() //Para que desaparezca de la pantalla si se registra correctamente y que no se quede escrito
                        password.text.clear()
                    } else{
                        // Si ha habido algún fallo que aparezca un Toast
                        //Toast.makeText(this,"Fallo en el registro",Toast.LENGTH_SHORT).show() //Aviso de fallo en el registro
                        showAlert("Fallo en el registro") //Para que se muestre el mensaje de abajo al fallar el registro
                    }
                }
            }
        }
    }

    //Mensaje de error en el registro
    private fun showAlert(text: String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setMessage(text)
            .setTitle("Error")
            .setPositiveButton("ACEPTAR", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //Si es un medico registrado en la base de datos, va al activity del medico, si no al paciente
    //Descomentar cuando esten realizadas las pantallas del medico y del paciente
    /*
    private fun showHome(email: String){
        if (email in emailMedicos){
            startActivity(Intent(this, HomeMedicoActivity::class.java))
        }else{
            startActivity(Intent(this, HomePaciente::class.java))
        }
    }
    */


}