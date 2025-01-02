package com.example.nutrimatev1.administrador

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import com.example.nutrimatev1.R
import com.example.nutrimatev1.modelo.Medico
import com.example.nutrimatev1.modelo.Paciente
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


class AsigMedFragmentAdm : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_asig_med_adm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val medicosSpinner: Spinner = view.findViewById(R.id.medicos_spinner)
        val pacientesSpinner: Spinner = view.findViewById(R.id.pacientes_spinner)
        val asignarButton: Button = view.findViewById(R.id.asignar_button)

        obtenerMedicos { medicos ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, medicos.map { it.nombre })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            medicosSpinner.adapter = adapter
        }

        obtenerPacientes { pacientes ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, pacientes.map { it.nombre })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            pacientesSpinner.adapter = adapter
        }

        asignarButton.setOnClickListener {
            val medicoSeleccionado = medicosSpinner.selectedItem as? String
            val pacienteSeleccionado = pacientesSpinner.selectedItem as? String
            if (medicoSeleccionado != null && pacienteSeleccionado != null) {
                asignarMedicoAPaciente(medicoSeleccionado, pacienteSeleccionado)
            } else {
                Toast.makeText(requireContext(), "Selecciona un médico y un paciente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerMedicos(callback: (List<Medico>) -> Unit) {
        FirebaseFirestore.getInstance().collection("Medicos")
            .get()
            .addOnSuccessListener { result ->
                val medicos = result.map { document ->
                    Medico(document.id, document.getString("nombre") ?: "")
                }
                callback(medicos)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al obtener médicos", exception)
            }
    }

    private fun obtenerPacientes(callback: (List<Paciente>) -> Unit) {
        FirebaseFirestore.getInstance().collection("Pacientes")
            .get()
            .addOnSuccessListener { result ->
                val pacientes = result.map { document ->
                    val fechaNacimiento = document.getDate("fechaNacimiento")
                    val calendar = Calendar.getInstance()
                    if (fechaNacimiento != null) {
                        calendar.time = fechaNacimiento
                    }
                    Paciente(document.id, document.getString("nombre") ?: "", calendar)
                }
                callback(pacientes)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al obtener pacientes", exception)
            }
    }

    private fun asignarMedicoAPaciente(medico: String, email: String) {
        val pacienteRef = FirebaseFirestore.getInstance().collection("Pacientes").document(email)
        val medicoData = hashMapOf("nombre" to medico)

        pacienteRef.collection("medicos").add(medicoData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Médico asignado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error al asignar médico: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}