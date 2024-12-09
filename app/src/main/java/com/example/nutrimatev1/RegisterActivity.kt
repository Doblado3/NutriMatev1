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
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText
    private lateinit var spinnerRole: Spinner
    private lateinit var textRegisterSex: TextView
    private lateinit var RradioGroupSexo: RadioGroup
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        email = findViewById(R.id.ReditTextEmail)
        password = findViewById(R.id.ReditTextPassword)
        name = findViewById(R.id.ReditTextName)
        spinnerRole = findViewById(R.id.spinnerRole)
        textRegisterSex = findViewById(R.id.textRegisterSex)
        RradioGroupSexo = findViewById(R.id.RradioGroupSexo)
        btnRegister = findViewById(R.id.btnRegister)

        // Configurar el adaptador del Spinner
        val roles = arrayOf("Seleccione su rol", "Médico", "Paciente")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter

        RradioGroupSexo.setOnCheckedChangeListener { group, checkedId ->
            val selectedSex = findViewById<RadioButton>(checkedId).text.toString()
            textRegisterSex.text = "Sexo: $selectedSex"
        }

        btnRegister.setOnClickListener {
            if (email.text.isNotBlank() && password.text.isNotBlank() && name.text.isNotBlank()) {
                val selectedRole = spinnerRole.selectedItem.toString()
                if (selectedRole != "Seleccione su rol") {
                    val selectedSexoId = RradioGroupSexo.checkedRadioButtonId
                    if (selectedSexoId != -1) {
                        val selectedSexo = findViewById<RadioButton>(selectedSexoId).text.toString()
                        registerUser(email.text.toString(), password.text.toString(), name.text.toString(), selectedRole, selectedSexo)
                    } else {
                        showAlert("Por favor, selecciona un sexo")
                    }
                } else {
                    showAlert("Por favor, selecciona un rol válido")
                }
            } else {
                showAlert("Por favor, completa todos los campos")
            }
        }
    }


    private fun registerUser(email: String, password: String, name: String, role: String, sexo: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("INFO", "El usuario se ha registrado correctamente")
                    saveUserToFirestore(email, name, role)
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        showAlert("Error en el registro: ${exception.message}")
                    } else {
                        showAlert("Fallo en el registro")
                    }
                }
            }
    }

    private fun saveUserToFirestore(email: String, name: String, role: String) {
        val user = hashMapOf(
            "email" to email,
            "name" to name,
            "role" to role
        )

        val collection = if (role == "Médico") "Medicos" else "Pacientes"
        FirebaseFirestore.getInstance().collection(collection).document(email).set(user)
            .addOnSuccessListener {
                Log.i("INFO", "Usuario guardado en Firestore")
                val intent = Intent (this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                showAlert("Error guardando el usuario en Firestore")
            }
    }

    private fun showAlert(text: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(text)
            .setTitle("Error")
            .setPositiveButton("ACEPTAR", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}