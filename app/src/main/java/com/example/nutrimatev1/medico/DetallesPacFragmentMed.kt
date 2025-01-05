package com.example.nutrimatev1.medico

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.nutrimatev1.R
import com.example.nutrimatev1.modelo.Alert
import com.example.nutrimatev1.modelo.Paciente
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class DetallesPacFragmentMed : Fragment() {

    private lateinit var textoNombre: TextView
    private lateinit var textoFecha: TextView
    private lateinit var botonBorrarPaciente: AppCompatButton
    private lateinit var paciente: Paciente

    private lateinit var botonCitas: AppCompatButton

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

        botonCitas = view.findViewById(R.id.botonAsignarCitaPac)

        val calendarBox = Calendar.getInstance()
        val dateBox = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    // Establece la hora seleccionada en el calendario
                    calendarBox.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendarBox.set(Calendar.MINUTE, minute)

                    // Llama a la función para guardar la cita con fecha y hora
                    guardarCita(year, month, day, hourOfDay, minute)
                },
                calendarBox.get(Calendar.HOUR_OF_DAY),
                calendarBox.get(Calendar.MINUTE),
                true
            ).show()
        }

        botonCitas.setOnClickListener{
            DatePickerDialog(requireContext(), dateBox, calendarBox.get(Calendar.YEAR),
                calendarBox.get(Calendar.MONTH), calendarBox.get(Calendar.DAY_OF_MONTH)).show()
        }



        // Recuperar el objeto Paciente de los argumentos
        arguments?.let { bundle ->
            val id = bundle.getString("id")
            val nombre = bundle.getString("nombre")
            val fechaNacimiento = bundle.getString("fecha de nacimiento")
            val telefono = bundle.getString("telefono")
            val sexo = bundle.getString("sexo")
            val apellidos = bundle.getString("apellidos")

            textoNombre.text = nombre

            //Guardar la fecha como un String facilita mucho esta parte
            textoFecha.text = "Fecha de nacimiento: ${fechaNacimiento}"

            if (id != null && nombre != null  && fechaNacimiento != null && telefono != null
                && sexo != null && apellidos != null) {
                paciente = Paciente(id, nombre, fechaNacimiento, telefono, sexo, apellidos)
            } else {

                Toast.makeText(requireContext(), "Faltan datos del paciente", Toast.LENGTH_SHORT).show()
            }
        }

        botonBorrarPaciente.setOnClickListener {
            eliminarPaciente()
        }
    }

    private fun guardarCita(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val emailMedico = Firebase.auth.currentUser!!.email!!
        val fecha = Calendar.getInstance()
        fecha.set(year, month, day, hour, minute)

        Firebase.firestore.collection("Medicos")
            .document(emailMedico)
            .collection("pacientes")
            .document(paciente.id)
            .collection("citas")
            .add(mapOf(
                "emailMedico" to emailMedico,
                "fechahora" to Timestamp(fecha.time)
            ))
            .addOnSuccessListener {
                Alert.showAlert(requireContext(), "Cita guardada de forma correcta")

            }
            .addOnFailureListener{

                Alert.showAlert(requireContext(), "Ha surgido un error al asignar la cita")

            }

        // Guardar la cita en la colección "Pacientes"
        Firebase.firestore.collection("Pacientes")
            .document(paciente.id)
            .collection("citas")
            .add(mapOf(
                "emailMedico" to emailMedico,
                "fechahora" to Timestamp(fecha.time)
            ))
            .addOnSuccessListener {
                Alert.showAlert(requireContext(), "Cita guardada en la colección de pacientes")
            }
            .addOnFailureListener {
                Alert.showAlert(requireContext(), "Ha surgido un error al asignar la cita en la colección de pacientes")
            }

    }


    private fun eliminarPaciente() {
            Firebase.firestore.collection("Medicos")
                .document(Firebase.auth.currentUser!!.email.toString())
                .collection("pacientes")
                .document(paciente.id)
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

