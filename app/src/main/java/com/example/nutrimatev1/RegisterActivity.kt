package com.example.nutrimatev1

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText
    private lateinit var roleGroup: RadioGroup
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)
        name = findViewById(R.id.editTextName)
        roleGroup = findViewById(R.id.radioGroupRole)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            if (email.text.isNotBlank() && password.text.isNotBlank() && name.text.isNotBlank()) {
                val selectedRoleId = roleGroup.checkedRadioButtonId
                if (selectedRoleId != -1) {
                    val selectedRole = findViewById<RadioButton>(selectedRoleId).text.toString()
                    registerUser(email.text.toString(), password.text.toString(), name.text.toString(), selectedRole)
                } else {
                    showAlert("Por favor, selecciona un rol")
                }
            } else {
                showAlert("Por favor, completa todos los campos")
            }
        }
    }


    private fun registerUser(email: String, password: String, name: String, role: String) {
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