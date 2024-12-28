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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrimatev1.R
import com.example.nutrimatev1.modelo.Question
import com.google.firebase.firestore.FirebaseFirestore


class DoTestFragmentPac : Fragment() {
    private lateinit var testTitle: String
    private lateinit var recyclerViewQuestions: RecyclerView
    private lateinit var questionAdapter: QuestionAdapter
    private val questions = mutableListOf<Question>()
    private val answers = mutableMapOf<String, String>() // Guarda respuestas por pregunta


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_do_test_pac, container, false)

        // Obtener el título del test desde los argumentos
        testTitle = arguments?.getString("testTitle") ?: "Test"

        // Configurar la interfaz con el título del test
        val tvTestTitle: TextView = view.findViewById(R.id.tvTestTitle)
        tvTestTitle.text = testTitle

        // Inicializar RecyclerView
        recyclerViewQuestions = view.findViewById(R.id.recyclerViewQuestions)
        recyclerViewQuestions.layoutManager = LinearLayoutManager(requireContext())
        questionAdapter = QuestionAdapter(questions)
        recyclerViewQuestions.adapter = questionAdapter

        // Cargar preguntas y opciones desde Firestore
        loadQuestionsFromFirestore()

        // Configurar el botón "Finalizar"
        val btnFinishTest: Button = view.findViewById(R.id.btnFinTest)
        btnFinishTest.setOnClickListener {
            saveTestResultsToFirestore() // Llamar la función para guardar respuestas
        }

        return view
    }

    private fun loadQuestionsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Tests")
            .whereEqualTo("title", testTitle)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val questionsList = document.get("questions") as? List<String> ?: emptyList()
                    val optionsList = document.get("options") as? List<String> ?: emptyList()

                    for (question in questionsList) {
                        // Asigna todas las opciones a cada pregunta
                        questions.add(Question(question, optionsList))
                    }
                }
                questionAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("DoTestFragmentPac", "Error loading questions", exception)
            }
    }


    companion object {
        fun newInstance(testTitle: String): DoTestFragmentPac {
            val fragment = DoTestFragmentPac()
            val args = Bundle()
            args.putString("testTitle", testTitle)
            fragment.arguments = args
            return fragment
        }
    }

    inner class QuestionAdapter(private val questions: List<Question>) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

        inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val questionTitle: TextView = itemView.findViewById(R.id.tvQuestionTitle)
            val optionsGroup: RadioGroup = itemView.findViewById(R.id.rgOptions)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
            return QuestionViewHolder(view)
        }

        override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
            val question = questions[position]
            holder.questionTitle.text = question.title
            holder.optionsGroup.removeAllViews()

            for (option in question.options) {
                val radioButton = RadioButton(holder.itemView.context)
                radioButton.text = option
                holder.optionsGroup.addView(radioButton)

                // Escuchar los cambios de selección
                holder.optionsGroup.setOnCheckedChangeListener { _, checkedId ->
                    val selectedOption = holder.itemView.findViewById<RadioButton>(checkedId)?.text.toString()
                    answers[question.title] = selectedOption
                }
            }
        }


        override fun getItemCount(): Int {
            return questions.size
        }
    }

    private fun saveTestResultsToFirestore() {
        val db = FirebaseFirestore.getInstance()

        // Crear el objeto a guardar
        val testResult = hashMapOf(
            "testTitle" to testTitle,
            "patientId" to "ID_DEL_PACIENTE", // Reemplaza con un identificador único del paciente
            "answers" to answers,
            "timestamp" to System.currentTimeMillis()
        )

        // Guardar en la colección ResultadosTests
        db.collection("ResultadosTests")
            .add(testResult)
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