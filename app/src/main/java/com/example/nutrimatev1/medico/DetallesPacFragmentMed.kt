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

    private lateinit var textoPaciente: TextView
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

        textoPaciente = view.findViewById(R.id.textDetallesPac)
        botonBorrarPaciente = view.findViewById(R.id.botonBorrarPac)

        // Recuperar el objeto Paciente de los argumentos
        paciente = arguments?.getSerializable("paciente") as? Paciente

        // Mostrar los detalles del paciente
        paciente?.let {
            textoPaciente.text = "${it.nombre}: ${it.fechanac.get(Calendar.DAY_OF_MONTH)}/" +
                    "${it.fechanac.get(Calendar.MONTH) + 1}/" + // Los meses empiezan desde 0
                    "${it.fechanac.get(Calendar.YEAR)}"
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
