package com.example.nutrimatev1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrimatev1.administrador.MainAdministrador
import com.example.nutrimatev1.medico.MainMedico
import com.example.nutrimatev1.modelo.Alert
import com.example.nutrimatev1.paciente.MainPaciente
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class RegisterActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText
    private lateinit var textRegisterSex: TextView
    private lateinit var RradioGroupSexo: RadioGroup
    private lateinit var btnRegister: Button

    private lateinit var telefono: EditText
    private lateinit var fechaNac: EditText
    private lateinit var apellidos: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        email = findViewById(R.id.ReditTextEmail)
        password = findViewById(R.id.ReditTextPassword)
        name = findViewById(R.id.ReditTextName)
        textRegisterSex = findViewById(R.id.textRegisterSex)
        RradioGroupSexo = findViewById(R.id.RradioGroupSexo)
        btnRegister = findViewById(R.id.btnRegister)

        telefono = findViewById(R.id.ReditTextPhone)
        fechaNac = findViewById(R.id.ReditTextDate)
        apellidos = findViewById(R.id.ReditTextApellidos)



        RradioGroupSexo.setOnCheckedChangeListener { group, checkedId ->
            val selectedSex = findViewById<RadioButton>(checkedId).text.toString()
            textRegisterSex.text = "Sexo: $selectedSex"
        }

        btnRegister.setOnClickListener {
            if (email.text.isNotBlank() && password.text.isNotBlank() && name.text.isNotBlank()) {
                val selectedSexoId = RradioGroupSexo.checkedRadioButtonId

                val e1 = Regex("^(\\+34|0034|34)?[67]\\d{8}\$")
                val ok1 = e1.matches(telefono.text.toString())
                if (!ok1) {
                    Log.e("ERROR", "El teléfono introducido no es correcto")
                    telefono.error = "Teléfono Incorrecto"
                }

                val e2 = Regex("^([0-2][0-9]|3[0-1])(\\/|-)(0[1-9]|1[0-2])\\2(\\d{4})\$")
                val ok2 = e2.matches(fechaNac.text.toString())
                if (!ok2) {
                    Log.e("ERROR", "La fecha introducida no es correcta")
                    fechaNac.error = "Fecha Incorrecta"
                }

                if (ok1 && ok2 && selectedSexoId != -1) {
                    val selectedSexo = findViewById<RadioButton>(selectedSexoId).text.toString()
                    val fecha = Timestamp(SimpleDateFormat("dd/MM/yyyy").parse(fechaNac.text.toString()))

                    registerUser(email.text.toString(), password.text.toString(), name.text.toString(), selectedSexo,
                        apellidos.text.toString(), fecha, telefono.text.toString())
                }
            } else {
                Alert.showAlert(this,"Por favor, rellena todos los campos")
            }
        }
    }


    private fun registerUser(email: String, password: String, name: String, sexo: String, apellidos: String,
                             fecha: Timestamp, telefono: String) {
        // Tampoco necesitamos patrones demasiado complejos, fáciles de recordar
        val doctorEmailRegex = Regex("^[a-zA-Z0-9._%+-]+@hospital\\.com\$")


        val adminEmailRegex = Regex("^[a-zA-Z0-9._%+-]+@adm\\.com\$")

        val role = when {
            doctorEmailRegex.matches(email) -> "Médico"
            adminEmailRegex.matches(email) -> "Administrador"
            else -> "Paciente"
        }

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("INFO", "El usuario se ha registrado correctamente")
                    saveUserToFirestore(email, name, role, sexo, apellidos, fecha, telefono)
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        Alert.showAlert(this, "Error en el registro: ${exception.message}")
                    } else {
                        Alert.showAlert(this, "Fallo en el registro")
                    }
                }
            }
    }

    private fun saveUserToFirestore(email: String, name: String, role: String, sexo: String,
                                    apellidos: String,
                                    fecha: Timestamp, telefono: String) {
        val user = hashMapOf(
            "email" to email,
            "nombre" to name,
            "role" to role,
            "sexo" to sexo,
            "apellidos" to apellidos,
            "telefono" to telefono,
            "fecha de nacimiento" to fecha,
        )

        val collection = when (role) {
            "Médico" -> "Medicos"
            "Administrador" -> "Administradores"
            else -> "Pacientes"
        }

        // Fijaos que es aquí donde se decide a donde redirigimos al usuario
        FirebaseFirestore.getInstance().collection(collection).document(email).set(user)
            .addOnSuccessListener {
                Log.i("INFO", "Usuario guardado en Firestore")
                val intent = when (role) {
                    "Médico" -> Intent(this, MainMedico::class.java)
                    "Administrador" -> Intent(this, MainAdministrador::class.java)
                    else -> Intent(this, MainPaciente::class.java)
                }
                startActivity(intent)

                // El finish() es para que no podamos volver hacia atrás de forma indebida
                finish()
            }
            .addOnFailureListener {
                Alert.showAlert(this,"Error guardando el usuario en Firestore")
            }
    }



}