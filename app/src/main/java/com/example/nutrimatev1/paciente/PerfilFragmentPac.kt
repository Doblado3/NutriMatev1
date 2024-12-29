package com.example.nutrimatev1.paciente

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.example.nutrimatev1.R
import com.example.nutrimatev1.modelo.Alert
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale


class PerfilFragmentPac : Fragment() {

    private lateinit var telefono: EditText
    private lateinit var textoSexo: TextView
    private lateinit var nombre: EditText
    private lateinit var apellidos: EditText
    private lateinit var fechaNac: EditText
    private lateinit var radioGroupPac: RadioGroup
    private lateinit var emailPac: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil_pac, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)


        telefono = view.findViewById(R.id.editTextPhonePac)
        textoSexo = view.findViewById(R.id.textSexPac)
        nombre = view.findViewById(R.id.editTextNombrePac)
        apellidos = view.findViewById(R.id.editTextApellidosPac)
        fechaNac = view.findViewById(R.id.editTextDatePac)
        radioGroupPac = view.findViewById(R.id.radioGroupPac)
        emailPac = view.findViewById(R.id.editTextEmailPac)

        val user = Firebase.auth.currentUser
        user?.let {
            emailPac.setText(user.email)
            emailPac.isEnabled = false
        }

        mostrarInfoPaciente()

        view.findViewById<RadioButton>(R.id.radioHombre).setOnClickListener {
            onRadioButtonClicked(it)
        }
        view.findViewById<RadioButton>(R.id.radioMujer).setOnClickListener {
            onRadioButtonClicked(it)
        }
        view.findViewById<RadioButton>(R.id.radioNC).setOnClickListener {
            onRadioButtonClicked(it)
        }

        val updateButton: Button = view.findViewById(R.id.botonActualizarPac)
        updateButton.setOnClickListener {
            onClickUpdate(it)
        }





    }

    private fun onClickUpdate(v: View?) {

        val e1 = Regex("^(\\+34|0034|34)?[67]\\d{8}\$")
        val ok1 = e1.matches(telefono.text.toString())
        if (!ok1) {
            Log.e("ERROR", "El teléfono introducido no es correcto")
            telefono.error = "Teléfono Incorrecto"
        }

        // Comprobar la fecha
        val e2 = Regex("^([0-2][0-9]|3[0-1])(\\/|-)(0[1-9]|1[0-2])\\2(\\d{4})\$")
        val ok2 = e2.matches(fechaNac.text.toString())
        if (!ok2) {
            Log.e("ERROR", "La fecha introducida no es correcta")
            fechaNac.error = "Fecha Incorrecta"
        }

        if (ok1 && ok2) {
            actualizarBD()
        }

    }

    private fun actualizarBD() {
        val col = Firebase.firestore.collection("Pacientes")
        val fecha = Timestamp(SimpleDateFormat("dd/MM/yyyy").parse(fechaNac.text.toString()))

        // Create new data for the medical info
        val nuevosDatos = mapOf(
            "nombre" to nombre.text.toString(),
            "apellidos" to apellidos.text.toString(),
            "fecha de nacimiento" to fecha,
            "sexo" to textoSexo.text.toString().substring(6),
            "telefono" to telefono.text.toString(),
        )

        col.document(emailPac.text.toString()).set(nuevosDatos)
            .addOnSuccessListener {
                Alert.showAlert(requireContext(),"Datos Actualizados")
            }
            .addOnFailureListener {
                Alert.showAlert(requireContext(),"Error actualizando los datos")
            }
    }

    private fun mostrarInfoPaciente() {
        val col = Firebase.firestore.collection("Pacientes")
        val user = Firebase.auth.currentUser

        user?.let {
            val emailUsuario = user.email
            if (!emailUsuario.isNullOrEmpty()) {
                col.document(emailUsuario).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            nombre.setText(document.getString("nombre"))
                            apellidos.setText(document.getString("apellidos"))
                            telefono.setText(document.getString("telefono"))

                            val fechaTimestamp = document.getTimestamp("fecha de nacimiento")
                            fechaTimestamp?.let {
                                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                val fechaFormateada = dateFormat.format(it.toDate())
                                fechaNac.setText(fechaFormateada)
                            }

                            val sexo = document.getString("sexo")
                            textoSexo.text = "Sexo: $sexo"
                        } else {
                            Alert.showAlert(requireContext(),"No se encontraron datos para este usuario")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ERROR", "Error al obtener los datos del paciente", e)
                        Alert.showAlert(requireContext(),"Error al obtener los datos del paciente")
                    }
            } else {
                Log.e("ERROR", "Email del usuario es nulo o vacío")
                Alert.showAlert(requireContext(),"Error: Email del usuario no disponible")
            }
        } ?: run {
            Log.e("ERROR", "Usuario no autenticado")
            Alert.showAlert(requireContext(),"Error: Usuario no autenticado")
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