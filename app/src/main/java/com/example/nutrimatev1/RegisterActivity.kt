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
import com.example.nutrimatev1.medico.MainMedico
import com.example.nutrimatev1.modelo.Alert
import com.example.nutrimatev1.paciente.MainPaciente
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText
    private lateinit var textRegisterSex: TextView
    private lateinit var RradioGroupSexo: RadioGroup
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        email = findViewById(R.id.ReditTextEmail)
        password = findViewById(R.id.ReditTextPassword)
        name = findViewById(R.id.ReditTextName)
        textRegisterSex = findViewById(R.id.textRegisterSex)
        RradioGroupSexo = findViewById(R.id.RradioGroupSexo)
        btnRegister = findViewById(R.id.btnRegister)



        RradioGroupSexo.setOnCheckedChangeListener { group, checkedId ->
            val selectedSex = findViewById<RadioButton>(checkedId).text.toString()
            textRegisterSex.text = "Sexo: $selectedSex"
        }

        btnRegister.setOnClickListener {
            if (email.text.isNotBlank() && password.text.isNotBlank() && name.text.isNotBlank()) {
                    val selectedSexoId = RradioGroupSexo.checkedRadioButtonId
                    if (selectedSexoId != -1) {
                        val selectedSexo = findViewById<RadioButton>(selectedSexoId).text.toString()


                        registerUser(email.text.toString(), password.text.toString(), name.text.toString(),  selectedSexo)
                    } else {
                        Alert.showAlert(this,"Por favor, selecciona un sexo")
                    }
                } else {
                    Alert.showAlert(this,"Por favor, selecciona un rol válido")
                }
        }
    }


    private fun registerUser(email: String, password: String, name: String, sexo: String) {
        // Define the regex for doctors' emails (adjust the pattern as needed)
        val doctorEmailRegex = Regex("^[a-zA-Z0-9._%+-]+@hospital\\.com\$")

        // Determine role based on email
        val role = if (doctorEmailRegex.matches(email)) "Médico" else "Paciente"

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("INFO", "El usuario se ha registrado correctamente")
                    saveUserToFirestore(email, name, role, sexo)
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

    private fun saveUserToFirestore(email: String, name: String, role: String, sexo: String) {
        val user = hashMapOf(
            "email" to email,
            "name" to name,
            "role" to role,
            "sexo" to sexo,
        )

        val collection = if (role == "Médico") "Medicos" else "Pacientes"
        FirebaseFirestore.getInstance().collection(collection).document(email).set(user)
            .addOnSuccessListener {
                Log.i("INFO", "Usuario guardado en Firestore")
                if (role == "Médico"){
                    startActivity(Intent(this, MainMedico::class.java))
                } else{
                    startActivity(Intent(this, MainPaciente::class.java))
                }
                finish()
            }
            .addOnFailureListener {
                Alert.showAlert(this,"Error guardando el usuario en Firestore")
            }
    }



}