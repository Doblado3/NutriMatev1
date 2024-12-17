package com.example.nutrimatev1.medico

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
import androidx.appcompat.app.AlertDialog
import com.example.nutrimatev1.R
import com.example.nutrimatev1.modelo.Alert
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale


class PerfilFragmentMed : Fragment() {

    private lateinit var telefono: EditText
    private lateinit var textoSexo: TextView
    private lateinit var nombre: EditText
    private lateinit var apellidos: EditText
    private lateinit var fechaNac: EditText
    private lateinit var radioGroupMed: RadioGroup
    private lateinit var emailMed: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil_med, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Lo que antes era InfoMedico(Activity), ahora pasa a ser PerfilFragmentMed
        * el código es prácticamente el mismo*/

        // Initialize the views
        telefono = view.findViewById(R.id.editTextPhoneMed)
        textoSexo = view.findViewById(R.id.textSexMed)
        nombre = view.findViewById(R.id.editTextNombreMed)
        apellidos = view.findViewById(R.id.editTextApellidosMed)
        fechaNac = view.findViewById(R.id.editTextDateMed)
        radioGroupMed = view.findViewById(R.id.radioGroupMed)
        emailMed = view.findViewById(R.id.editTextEmailMed)

        // Set email as the current user's email and make it uneditable
        val user = Firebase.auth.currentUser
        user?.let {
            emailMed.setText(user.email)
            emailMed.isEnabled = false
        }

        mostrarInfoMedico()

        //Esta es la única parte que necesitamos cambiar. Las funciones públicas
        //ya no se implementan en el XML sino que se hacen aquí en el Fragment
        //a través de "setOnClickListener".
        view.findViewById<RadioButton>(R.id.radioHombre).setOnClickListener {
            onRadioButtonClicked(it)
        }
        view.findViewById<RadioButton>(R.id.radioMujer).setOnClickListener {
            onRadioButtonClicked(it)
        }
        view.findViewById<RadioButton>(R.id.radioNC).setOnClickListener {
            onRadioButtonClicked(it)
        }


        //Incluye tanto al botón de actualizar como a las "restricciones" de
        //las expresiones regulares
        val updateButton: Button = view.findViewById(R.id.botonActualizarMed)
        updateButton.setOnClickListener {
            onClickUpdate(it)
        }
    }

    fun onClickUpdate(v: View?) {
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
        val col = Firebase.firestore.collection("Medicos")
        val fecha = Timestamp(SimpleDateFormat("dd/MM/yyyy").parse(fechaNac.text.toString()))

        // Create new data for the medical info
        val nuevosDatos = mapOf(
            "nombre" to nombre.text.toString(),
            "apellidos" to apellidos.text.toString(),
            "fecha de nacimiento" to fecha,
            "sexo" to textoSexo.text.toString().substring(6),
            "telefono" to telefono.text.toString(),
        )

        col.document(emailMed.text.toString()).set(nuevosDatos)
            .addOnSuccessListener {
                Alert.showAlert(requireContext(),"Datos Actualizados")
            }
            .addOnFailureListener {
                Alert.showAlert(requireContext(),"Error actualizando los datos")
            }
    }


    private fun mostrarInfoMedico() {
        val col = Firebase.firestore.collection("Medicos")
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
