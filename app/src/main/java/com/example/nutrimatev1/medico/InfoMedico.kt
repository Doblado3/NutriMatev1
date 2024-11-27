package com.example.nutrimatev1.medico

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import com.example.nutrimatev1.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class InfoMedico : BaseMedico() {

    private lateinit var telefono: EditText
    private lateinit var textoSexo: TextView
    private lateinit var nombre: EditText
    private lateinit var apellidos: EditText
    private lateinit var fechaNac: EditText
    private lateinit var radioGroupMed: RadioGroup


    private lateinit var emailMed: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info_medico)

        telefono = findViewById(R.id.editTextPhoneMed)
        textoSexo = findViewById(R.id.textSexMed)
        nombre = findViewById(R.id.editTextNombreMed)
        apellidos = findViewById(R.id.editTextApellidosMed)
        fechaNac = findViewById(R.id.editTextDateMed)
        radioGroupMed = findViewById(R.id.radioGroupMed)

        emailMed = findViewById(R.id.editTextEmailMed)

        val user = Firebase.auth.currentUser
        user?.let {
            emailMed.setText(user.email)
            emailMed.isEnabled = false
        }

        setSupportActionBar(findViewById(R.id.toolbar))

        mostrarInfoMedico()



    }

    fun onClickUpdate(v: View?) {
        val e1 = Regex("^(\\+34|0034|34)?[67]\\d{8}\$")
        val ok1 = e1.matches(telefono.text.toString())
        if (!ok1) {
            Log.e("ERROR", "El teléfono introducido no es correcto")
            telefono.error = "Teléfono Incorrecto"
        }

        //Comprobar la fecha
        val e2 = Regex("^([0-2][0-9]|3[0-1])(\\/|-)(0[1-9]|1[0-2])\\2(\\d{4})\$")
        val ok2 = e2.matches(fechaNac.text.toString())
        if (!ok2) {
            Log.e("ERROR", "La fecha introducida no es correcta")
            fechaNac.error = "Fecha Incorrecta"
        }

        if (ok1 && ok2){

            actualizarBD()

        }

    }

    private fun actualizarBD(){
        //acceder a la BD -> coleccion Pacientes
        val col = Firebase.firestore.collection("Medicos")

        val fecha = Timestamp(SimpleDateFormat("dd/MM/yyyy").parse(fechaNac.text.toString()))

        //crear variable nuevosDatos con MapOf
        val nuevosDatos = mapOf(
            "nombre" to nombre.text.toString(),
            "apellidos" to apellidos.text.toString(),
            "fecha de nacimiento" to fecha,
            "sexo" to textoSexo.text.toString().substring(6),
            "telefono" to telefono.text.toString(),
        )

        //acceder al documento del paciente actual(email) y
        //modificar sus datos en la BD con set
        //notificar al usuario
        col.document(emailMed.text.toString()).set(nuevosDatos)
            .addOnSuccessListener {

                //mirar si hay cambio de contraseña y actualizarla

                showAlert("Datos Actualizados")

            }

            .addOnFailureListener{

                showAlert("Error logueando al usuario")

            }


    }

    private fun showAlert(text: String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setMessage(text)
            .setTitle("Info")
            .setPositiveButton("ACEPTAR", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
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

    private fun mostrarInfoMedico() {
        val col = Firebase.firestore.collection("Pacientes")
        val user = Firebase.auth.currentUser

        if (user != null) {
            val emailUsuario = user.email
            if (!emailUsuario.isNullOrEmpty()) {
                col.document(emailUsuario).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            // Rellenar los campos con los datos recuperados
                            nombre.setText(document.getString("nombre"))
                            apellidos.setText(document.getString("apellidos"))
                            telefono.setText(document.getString("telefono"))

                            // Convertir la fecha (Timestamp) a formato legible
                            val fechaTimestamp = document.getTimestamp("fecha de nacimiento")
                            fechaTimestamp?.let {
                                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                val fechaFormateada = dateFormat.format(it.toDate())
                                fechaNac.setText(fechaFormateada)
                            }

                            // Mostrar sexo en el TextView
                            val sexo = document.getString("sexo")
                            textoSexo.text = "Sexo: $sexo"
                        } else {
                            showAlert("No se encontraron datos para este usuario")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ERROR", "Error al obtener los datos del paciente", e)
                        showAlert("Error al obtener los datos del paciente")
                    }
            } else {
                Log.e("ERROR", "Email del usuario es nulo o vacío")
                showAlert("Error: Email del usuario no disponible")
            }
        } else {
            Log.e("ERROR", "Usuario no autenticado")
            showAlert("Error: Usuario no autenticado")
        }
    }
}