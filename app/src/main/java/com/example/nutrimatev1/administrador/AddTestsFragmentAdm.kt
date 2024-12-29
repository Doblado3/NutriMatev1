package com.example.nutrimatev1.administrador

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.nutrimatev1.R
import com.google.firebase.firestore.FirebaseFirestore




class AddTestsFragmentAdm : Fragment() {

    private lateinit var nombreNewTest: EditText
    private lateinit var descripcionNewTest: EditText
    private lateinit var preguntasNewTest: EditText
    private lateinit var opcionesNewTest: EditText
    private lateinit var valoresNewTest: EditText
    private lateinit var explicacionNewTest: EditText
    private lateinit var buttonAddNewTest: Button
    private lateinit var proBarAddTest: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_tests_adm, container, false)

        nombreNewTest = view.findViewById(R.id.nombreNewTest)
        descripcionNewTest = view.findViewById(R.id.descripcionNewTest)
        preguntasNewTest = view.findViewById(R.id.preguntasNewTest)
        opcionesNewTest = view.findViewById(R.id.opcionesNewTest)
        valoresNewTest = view.findViewById(R.id.valoresNewTest)
        explicacionNewTest = view.findViewById(R.id.explicacionNewTest)
        buttonAddNewTest = view.findViewById(R.id.buttonAddNewTest)


        buttonAddNewTest.setOnClickListener {
            addTestToFirestore()
        }

        return view
    }

    private fun addTestToFirestore() {
        val title = nombreNewTest.text.toString()
        val description = descripcionNewTest.text.toString()
        val questions = preguntasNewTest.text.toString().split(",").map { it.trim() }
        val options = opcionesNewTest.text.toString().split(",").map { it.trim() }
        val values = valoresNewTest.text.toString().split(",").map { it.trim() }
        val explanation = explicacionNewTest.text.toString().trim()

        //Verificar que todos los campos estén completos
        if (title.isEmpty() || description.isEmpty() || questions.isEmpty() || options.isEmpty() || values.isEmpty() || explanation.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }



        val test = hashMapOf(
            "title" to title,
            "description" to description,
            "questions" to questions,
            "options" to options,
            "values" to values,
            "explanation" to explanation
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("Tests").add(test)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Test creado con éxito", Toast.LENGTH_SHORT).show()
                //Volviendo al fragment anterior
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al crear el test", Toast.LENGTH_SHORT).show()
            }
    }
}