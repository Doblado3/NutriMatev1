package com.example.nutrimatev1.medico

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.nutrimatev1.R
import com.example.nutrimatev1.modelo.Alert
import com.example.nutrimatev1.modelo.Paciente
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class DetallesPacFragmentMed : Fragment() {

    private lateinit var textoNombre: TextView
    private lateinit var textoFecha: TextView
    private lateinit var botonBorrarPaciente: Button
    private var paciente: Paciente? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate el diseño para este fragment
        return inflater.inflate(R.layout.fragment_detalles_pac_med, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textoNombre = view.findViewById(R.id.textNombreDetPac)
        textoFecha = view.findViewById(R.id.textFechaDetPac)
        botonBorrarPaciente = view.findViewById(R.id.botonBorrarPac)

        // Recuperar el objeto Paciente de los argumentos
        arguments?.let { bundle ->
            val nombre = bundle.getString("nombre")
            val fechaNacimiento = bundle.getSerializable("fechaNacimiento") as? Calendar

            textoNombre.text = nombre



            fechaNacimiento?.let {
                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                textoFecha.text = "Fecha de Nacimiento: ${dateFormat.format(it.time)}"
            }
        }

        // Configurar el botón de borrar
        botonBorrarPaciente.setOnClickListener {
            eliminarPaciente()
        }
    }

    private fun eliminarPaciente() {
        paciente?.let {
            Firebase.firestore.collection("Medicos")
                .document(Firebase.auth.currentUser!!.email.toString())
                .collection("pacientes")
                .document(it.id) // Asegúrate de que `Paciente` tenga un campo `id`
                .delete()
                .addOnSuccessListener {
                    Alert.showAlert(requireContext(), "Paciente eliminado correctamente")
                    // Regresar al fragment anterior
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    Alert.showAlert(requireContext(), "Error al eliminar el paciente")
                }
        }
    }
}
