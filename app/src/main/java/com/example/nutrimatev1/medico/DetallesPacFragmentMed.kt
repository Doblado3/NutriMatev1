package com.example.nutrimatev1.medico

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrimatev1.R
import com.example.nutrimatev1.modelo.Alert
import com.example.nutrimatev1.modelo.Cita
import com.example.nutrimatev1.modelo.Paciente
import com.example.nutrimatev1.modelo.ResultadosTest
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class DetallesPacFragmentMed : Fragment() {

    private lateinit var textoNombre: TextView
    private lateinit var textoFecha: TextView
    private lateinit var botonBorrarPaciente: AppCompatButton
    private lateinit var paciente: Paciente

    private lateinit var botonCitas: AppCompatButton
    private lateinit var citasMed: RecyclerView
    private lateinit var textoCitas: TextView
    private lateinit var resultadosPac: RecyclerView


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
        citasMed = view.findViewById(R.id.citasMedRec)
        textoCitas = view.findViewById(R.id.textViewDescCitasMed)
        resultadosPac = view.findViewById(R.id.resultadosPacMed)


        val calendarBox = Calendar.getInstance()
        val dateBox = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, day)

            if (selectedDate.before(Calendar.getInstance())) {
                Toast.makeText(requireContext(), "No se pueden coger citas en fechas ya pasadas", Toast.LENGTH_SHORT).show()
            } else {
                TimePickerDialog(
                    requireContext(),
                    R.style.CustomDatePickerStyle,
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
                ).apply {
                    setOnShowListener {
                        getButton(TimePickerDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
                        getButton(TimePickerDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
                    }
                }.show()
            }
        }



        botonCitas.setOnClickListener{
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.CustomDatePickerStyle,
                dateBox,
                calendarBox.get(Calendar.YEAR),
                calendarBox.get(Calendar.MONTH),
                calendarBox.get(Calendar.DAY_OF_MONTH))

            // Establecer la fecha mínima permitida a la fecha actual
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000


            datePickerDialog.setOnShowListener {
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
            }
                datePickerDialog.show()
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

                //Si no inicializamos el paciente antes de mostrar las citas, la aplicación "crashea"
                muestraCitas()
                muestraResultadosPac()

            } else {

                Toast.makeText(requireContext(), "Faltan datos del paciente", Toast.LENGTH_SHORT).show()
            }
        }

        botonBorrarPaciente.setOnClickListener {
            eliminarPaciente()
        }
    }

    private fun muestraResultadosPac() {
        var resultados = mutableListOf<ResultadosTest>()
        FirebaseFirestore.getInstance().collection("Pacientes")
            .document(paciente.id)
            .collection("ResultadosTests")
            .get()
            .addOnSuccessListener { r ->
                for(resultado in r){
                    var nombreTest = resultado.get("Nombre del Test").toString()
                    var total = resultado.getLong("acumulado")!!.toInt()
                    var fechaTest = Calendar.getInstance()
                    fechaTest.time = resultado.getTimestamp("fechaRealizacion")?.toDate()
                    val respuestas = resultado.get("respuestas") as? Map<String, String> ?: emptyMap()

                    resultados.add(ResultadosTest(resultado.id,nombreTest, total, fechaTest, respuestas))
                }

                resultadosPac.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                resultadosPac.adapter = AdaptadorResultados(resultados)

            }

    }

    private fun muestraCitas() {
        var citas = mutableListOf<Cita>()
        FirebaseFirestore.getInstance().collection("Medicos")
            .document(Firebase.auth.currentUser!!.email!!)
            .collection("pacientes")
            .document(paciente.id)
            .collection("citas")
            .get()
            .addOnSuccessListener { c ->
                for (cita in c) {


                    var med_nom = cita.get("nombre").toString()
                    var email_med = cita.get("emailMedico").toString()
                    var fechaCita = Calendar.getInstance()
                    fechaCita.time = cita.getTimestamp("fechahora")?.toDate()

                    citas.add(Cita(cita.id,med_nom, email_med, fechaCita))

                }

                citasMed.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                citasMed.adapter = AdaptadorCitas(citas) { citaId ->
                    borrarCita(citaId)
                }

                if(AdaptadorCitas(citas){}.itemCount > 0){
                    textoCitas.text = "Tus próximas citas:"
                }else{
                    textoCitas.text = "Aquí se mostrarán tus próximas citas"
                }



            }

    }

    private fun guardarCita(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val emailMedico = Firebase.auth.currentUser!!.email!!
        FirebaseFirestore.getInstance().collection("Medicos")
            .document(emailMedico)
            .get()
            .addOnSuccessListener { documentMed ->
                val nombreMed = documentMed.getString("nombre")
                val apellidosMed = documentMed.getString("apellidos")

                val fecha = Calendar.getInstance()
                fecha.set(year, month, day, hour, minute)

                val citaId = fecha.time.toString()

                val citaData = hashMapOf(
                    "emailMedico" to emailMedico,
                    "fechahora" to Timestamp(fecha.time),
                    "nombre" to nombreMed,
                    "apellidos" to apellidosMed
                )

                //Si usamos .add() asigna unn ID automático, lo cual no nos permite borrar
                //al mismo tiempo en ámbas partes de la base de datos
                Firebase.firestore.collection("Medicos")
                    .document(emailMedico)
                    .collection("pacientes")
                    .document(paciente.id)
                    .collection("citas")
                    .document(citaId)
                    .set(citaData)
                    .addOnSuccessListener {
                        Alert.showAlert(requireContext(), "Cita guardada de forma correcta")
                        muestraCitas()
                    }
                    .addOnFailureListener {

                        Alert.showAlert(requireContext(), "Ha surgido un error al asignar la cita")

                    }

                // Guardar la cita en la colección "Pacientes"
                Firebase.firestore.collection("Pacientes")
                    .document(paciente.id)
                    .collection("citas")
                    .document(citaId)
                    .set(citaData)
                    .addOnFailureListener {
                        Alert.showAlert(
                            requireContext(),
                            "Ha surgido un error al asignar la cita en la colección de pacientes"
                        )
                    }
            }

    }


    private fun eliminarPaciente() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
        builder.setTitle("Vas a eliminar a este paciente")
            .setMessage("¿Estas seguro?")
            .setPositiveButton("Sí") { _, _ ->

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
            }.setNegativeButton("Cancelar", null)
            .show()

        }

    private fun borrarCita(citaId: String) {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogTheme)
        builder.setTitle("Vas a eliminar esta cita")
            .setMessage("¿Estas seguro?")
            .setPositiveButton("Sí") { _, _ ->

                Firebase.firestore.collection("Medicos")
                    .document(Firebase.auth.currentUser!!.email.toString())
                    .collection("pacientes")
                    .document(paciente.id)
                    .collection("citas")
                    .document(citaId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Cita eliminada correctamente", Toast.LENGTH_SHORT).show()
                        muestraCitas()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error al eliminar la cita: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                Firebase.firestore.collection("Pacientes")
                    .document(paciente.id)
                    .collection("citas")
                    .document(citaId)
                    .delete()
                    .addOnSuccessListener {
                        muestraCitas()
                    }
                    .addOnFailureListener{
                        Alert.showAlert(requireContext(), "Error al borrar la cita")
                    }
            }.setNegativeButton("Cancelar", null)
            .show()



            }

    inner class AdaptadorCitas(private val dataSet: List<Cita>, private val clickListener: (String)->Unit):
        RecyclerView.Adapter<AdaptadorCitas.ViewHolder>(){

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){

            val textView_fechaCita: TextView = view.findViewById(R.id.textViewfechaCitaMed)
            val DeleteCita: ImageView = view.findViewById(R.id.imageDeleteCita)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_citas_medico, parent, false)

            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            val formattedDate = dateFormat.format(dataSet[position].fecha.time)
            holder.textView_fechaCita.text = "Fecha: ${formattedDate}"

            holder.DeleteCita.setOnClickListener{clickListener(dataSet[position].id) }
        }

    }

    inner class AdaptadorResultados(private val dataSet: List<ResultadosTest>):
        RecyclerView.Adapter<AdaptadorResultados.ViewHolder>(){

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){

            val nombreTestListado: TextView = view.findViewById(R.id.textViewNombreTest)
            val resultadoTest: TextView = view.findViewById(R.id.textViewResultadoTest)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_resultados_tests, parent, false)

            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            val formattedDate = dateFormat.format(dataSet[position].fecha.time)
            holder.nombreTestListado.text = "Test ${dataSet[position].nombre} realizado el: ${formattedDate}"
            holder.resultadoTest.text = "Puntuación Obtenida: ${dataSet[position].acumulado}"
        }

    }
}

