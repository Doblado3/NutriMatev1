package com.example.nutrimatev1.paciente

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrimatev1.R
import com.example.nutrimatev1.modelo.Paciente
import com.example.nutrimatev1.modelo.PreguntaOpciones
import com.example.nutrimatev1.modelo.Test
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DoTestFragmentPac : Fragment() {

    private lateinit var testTitle: TextView
    private lateinit var recyclerViewQuestions: RecyclerView
    private lateinit var botonFin: AppCompatButton
    private lateinit var test: Test

    private var preguntasLista = mutableListOf<PreguntaOpciones>()
    private var respuestas = mutableMapOf<String, String>()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_do_test_pac, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        testTitle = view.findViewById(R.id.tvTestTitle)
        recyclerViewQuestions = view.findViewById(R.id.recyclerViewQuestions)
        botonFin = view.findViewById(R.id.btnFinTest)

        arguments?.let { bundle ->
            val id = bundle.getString("id")
            val descripcion = bundle.getString("descripcion")
            val explicacion = bundle.getString("explicacion")
            val opciones = bundle.getStringArrayList("opciones")
            val preguntas = bundle.getStringArrayList("preguntas")
            val valores = bundle.getStringArrayList("valores")

            testTitle.text = id


            if (id != null && descripcion != null  && explicacion != null && opciones != null
                && preguntas != null && valores != null) {
                test = Test(id, descripcion, explicacion, opciones, preguntas, valores)

                for (pregunta in preguntas) {
                    preguntasLista.add(PreguntaOpciones(pregunta,opciones, valores))
                }

                /*preguntaOpciones = preguntas.mapIndexed { index, pregunta ->
                    PreguntaOpciones(
                        pregunta,
                        opciones[index].split(","),
                        valores[index].split(","))
            }*/

                recyclerViewQuestions.layoutManager = LinearLayoutManager(requireContext())
                recyclerViewQuestions.adapter = AdaptadorPreguntas(preguntasLista)



            } else {

                Toast.makeText(requireContext(), "El test no está completo", Toast.LENGTH_SHORT).show()
            }
        }


        botonFin.setOnClickListener {
            guardarRespuestas()
        }


    }



    inner class AdaptadorPreguntas(private val preguntaConOpciones: List<PreguntaOpciones>) :
        RecyclerView.Adapter<AdaptadorPreguntas.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val tituloPregunta: TextView = view.findViewById(R.id.tvQuestionTitle)
            val listaOpciones: RadioGroup = view.findViewById(R.id.rgOptions)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val preguntaOpcn = preguntaConOpciones[position]
            holder.tituloPregunta.text = preguntaOpcn.pregunta
            holder.listaOpciones.removeAllViews()

            for (opcion in preguntaOpcn.opciones) {
                val radioButton = RadioButton(holder.itemView.context)
                radioButton.text = opcion
                holder.listaOpciones.addView(radioButton)
            }

            holder.listaOpciones.setOnCheckedChangeListener { _, checkedId ->
                    val selectedOption = holder.itemView.findViewById<RadioButton>(checkedId)?.text.toString()
                    respuestas[preguntaOpcn.pregunta] = selectedOption
                }


        }


        override fun getItemCount(): Int {
            return preguntaConOpciones.size
        }
    }

    private fun guardarRespuestas() {

        val pactEmail = Firebase.auth.currentUser!!.email!!

        // Obtener los valores de las respuestas seleccionadas
        var totalScore = 0
        for ((pregunta, opcionSeleccionada) in respuestas) {
            val preguntaOpc = preguntasLista.find { it.pregunta == pregunta }
            if (preguntaOpc != null) {
                val indice = preguntaOpc.opciones.indexOf(opcionSeleccionada)
                if (indice != -1) {
                    val valor = preguntaOpc.valores[indice].toIntOrNull() ?: 0
                    totalScore += valor
                }
            }
        }

        val resultadoTest = hashMapOf(
            "Nombre del Test" to test.id,
            "acumulado" to totalScore,
            "respuestas" to respuestas,
            "fechaRealizacion" to Timestamp(Calendar.getInstance().time)
        )

        val timeStamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.getDefault())
        val formattedTime = dateFormat.format(timeStamp)
        val testId = "${test.id}_${formattedTime}"


        Firebase.firestore.collection("Pacientes")
            .document(pactEmail)
            .collection("ResultadosTests")
            .document(testId)
            .set(resultadoTest)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Resultados guardados exitosamente", Toast.LENGTH_SHORT).show()

                // Volver a la pantalla de lista de tests
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener { exception ->
                Log.e("DoTestFragmentPac", "Error al guardar resultados", exception)
                Toast.makeText(requireContext(), "Error al guardar los resultados", Toast.LENGTH_SHORT).show()
            }
    }

}