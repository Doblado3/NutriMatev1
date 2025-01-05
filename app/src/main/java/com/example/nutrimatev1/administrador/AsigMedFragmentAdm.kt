package com.example.nutrimatev1.administrador

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.nutrimatev1.R
import com.example.nutrimatev1.modelo.Medico
import com.example.nutrimatev1.modelo.Paciente
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AsigMedFragmentAdm : Fragment() {


    private lateinit var medicosSpinner: Spinner
    private lateinit var pacientesSpinner: Spinner
    private lateinit var asignarButton: Button

    private lateinit var medicosMap: Map<String, String>
    private lateinit var pacientesMap: Map<String, String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_asig_med_adm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        medicosSpinner = view.findViewById(R.id.medicos_spinner)
        pacientesSpinner = view.findViewById(R.id.pacientes_spinner)
        asignarButton = view.findViewById(R.id.asignar_button)

        medicosSpinner.onItemSelectedListener = SpinnerActivity()

        obtenerMedicos {
            obtenerPacientes {
                asignarButton.setOnClickListener {
                    val medicoSeleccionado = medicosSpinner.selectedItem as? String
                    val pacienteSeleccionado = pacientesSpinner.selectedItem as? String

                    val emailMed = medicosMap[medicoSeleccionado]
                    val emailPac = pacientesMap[pacienteSeleccionado]

                    if (emailMed != null && emailPac != null) {
                        //Función que se encarga de asignar los pacientes considerando además
                        //asignaciones ya realizadas
                        evitaDuplicados(emailMed, emailPac)
                    } else {
                        Toast.makeText(requireContext(), "Error: Selección inválida.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun obtenerMedicos(callback: () -> Unit) {
        FirebaseFirestore.getInstance().collection("Medicos")
            .get()
            .addOnSuccessListener { result ->
                val medicos = result.associate { document ->
                    val nombreCompleto = "${document.getString("nombre") ?: ""} ${document.getString("apellidos") ?: ""}"
                    val email = document.id

                    //Asigna el nombre al email para mostrarlo en el Spinner
                    nombreCompleto to email
                }
                medicosMap = medicos

                val nombresMedicos = mutableListOf("Porfavor, selecciona a un médico")
                nombresMedicos.addAll(medicos.keys.toList())

                //Personalizo el Spinner para que resaltar el primer elemento que no es ni paciente ni médico
                val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, nombresMedicos) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getView(position, convertView, parent)
                        val textView = view.findViewById<TextView>(android.R.id.text1)


                        if (position == 0) {
                            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                            textView.setTypeface(textView.typeface, Typeface.BOLD)
                        } else {
                            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                            textView.setTypeface(Typeface.DEFAULT)
                        }
                        return view
                    }

                    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getDropDownView(position, convertView, parent)
                        val textView = view.findViewById<TextView>(android.R.id.text1)


                        if (position == 0) {
                            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                            textView.setTypeface(textView.typeface, Typeface.BOLD)
                        } else {
                            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                            textView.setTypeface(Typeface.DEFAULT)
                        }
                        return view
                    }
                }
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                medicosSpinner.adapter = adapter
                callback()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al obtener médicos", exception)
                Toast.makeText(requireContext(), "Error al cargar médicos.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerPacientes(callback: () -> Unit) {
        FirebaseFirestore.getInstance().collection("Pacientes")
            .get()
            .addOnSuccessListener { result ->
                val pacientes = result.associate { document ->
                    val nombreCompleto = "${document.getString("nombre") ?: ""} ${document.getString("apellidos") ?: ""}"
                    val email = document.id
                    nombreCompleto to email
                }
                pacientesMap = pacientes

                val nombresPacientes = mutableListOf("Ahora, asígnale un paciente")
                nombresPacientes.addAll(pacientes.keys.toList())

                //Personalizo el Spinner para que resaltar el primer elemento que no es ni paciente ni médico
                val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, nombresPacientes) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getView(position, convertView, parent)
                        val textView = view.findViewById<TextView>(android.R.id.text1)


                        if (position == 0) {
                            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                            textView.setTypeface(textView.typeface, Typeface.BOLD)
                        } else {
                            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                            textView.setTypeface(Typeface.DEFAULT)
                        }
                        return view
                    }

                    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getDropDownView(position, convertView, parent)
                        val textView = view.findViewById<TextView>(android.R.id.text1)


                        if (position == 0) {
                            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                            textView.setTypeface(textView.typeface, Typeface.BOLD)
                        } else {
                            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
                            textView.setTypeface(Typeface.DEFAULT)
                        }
                        return view
                    }
                }
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                pacientesSpinner.adapter = adapter
                callback()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al obtener pacientes", exception)
                Toast.makeText(requireContext(), "Error al cargar pacientes.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun evitaDuplicados(emailMed: String, emailPac: String){
        val medicosRef = FirebaseFirestore.getInstance()
            .collection("Medicos")
            .document(emailMed)
            .collection("pacientes")
            .document(emailPac)

        medicosRef.get().addOnSuccessListener { paciente ->
            if (paciente.exists()){
                Toast.makeText(requireContext(), "Paciente ya asignado", Toast.LENGTH_SHORT).show()
            } else {

                asignarMedicoAPaciente(emailMed, emailPac)

            }
        }

    }

    private fun asignarMedicoAPaciente(emailMed: String, emailPac: String) {
        //Repetimos el mismo paso de funciones anteriores para obtener otros campos
        FirebaseFirestore.getInstance().collection("Medicos")
            .document(emailMed)
            .get()
            .addOnSuccessListener { documentMed ->
                val nombreMed = documentMed.getString("nombre")
                val apellidosMed = documentMed.getString("apellidos")
                val sexoMed = documentMed.getString("sexo")
                val telefonoMed = documentMed.getString("telefono")
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val fechaFormateadaMed = dateFormat.format(documentMed.getTimestamp("fecha de nacimiento")?.toDate())

                FirebaseFirestore.getInstance().collection("Pacientes")
                    .document(emailPac)
                    .get()
                    .addOnSuccessListener { documentPac ->
                        val nombrePac = documentPac.getString("nombre")
                        val apellidosPac = documentPac.getString("apellidos")
                        val sexoPac = documentPac.getString("sexo")
                        val telefonoPac = documentPac.getString("telefono")
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val fechaFormateadaPac = dateFormat.format(documentPac.getTimestamp("fecha de nacimiento")?.toDate())

                        val medicoData = hashMapOf(
                            "nombre" to nombreMed,
                            "apellidos" to apellidosMed,
                            "sexo" to sexoMed,
                            "telefono" to telefonoMed,
                            "fecha de nacimiento" to fechaFormateadaMed,
                            )

                        // Asigna el médico al paciente en la subcolección "medicos" del paciente
                        val pacienteRef = FirebaseFirestore.getInstance()
                            .collection("Pacientes")
                            .document(emailPac)

                        pacienteRef.collection("medicos").document(emailMed)
                            .set(medicoData)
                            .addOnSuccessListener {

                                val pacienteData = hashMapOf(
                                    "nombre" to nombrePac,
                                    "apellidos" to apellidosPac,
                                    "sexo" to sexoPac,
                                    "telefono" to telefonoPac,
                                    "fecha de nacimiento" to fechaFormateadaPac
                                )

                                // Luego, asigna el paciente al médico en la subcolección "pacientes" del médico
                                val medicoRef = FirebaseFirestore.getInstance()
                                    .collection("Medicos")
                                    .document(emailMed)

                                medicoRef.collection("pacientes").document(emailPac)
                                    .set(pacienteData)
                                    .addOnSuccessListener {

                                        Toast.makeText(requireContext(), "Asignación satisfactoria.", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(requireContext(), "Error al realizar la asignación: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }

                            }
                    }

                }
    }

    inner class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {

            if(pos == 0){

                pacientesSpinner.visibility = INVISIBLE

            }else{

                pacientesSpinner.visibility = VISIBLE

            }

        }

        override fun onNothingSelected(parent: AdapterView<*>) {

        }
    }
}