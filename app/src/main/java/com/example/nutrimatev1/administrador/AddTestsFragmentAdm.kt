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

    private lateinit var nombreTest: EditText
    private lateinit var descripcionTest: EditText
    private lateinit var preguntasTest: EditText
    private lateinit var opcionesTest: EditText
    private lateinit var valoresTest: EditText
    private lateinit var explicacionTest: EditText
    private lateinit var buttonAddTest: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_tests_adm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        nombreTest = view.findViewById(R.id.nombreNewTest)
        descripcionTest = view.findViewById(R.id.descripcionNewTest)
        preguntasTest = view.findViewById(R.id.preguntasNewTest)
        opcionesTest = view.findViewById(R.id.opcionesNewTest)
        valoresTest = view.findViewById(R.id.valoresNewTest)
        explicacionTest = view.findViewById(R.id.explicacionNewTest)
        buttonAddTest = view.findViewById(R.id.buttonAddNewTest)


        buttonAddTest.setOnClickListener {
            añadirTests()
        }



    }

    private fun añadirTests() {

        val titulo = nombreTest.text.toString()
        val descripcion = descripcionTest.text.toString()
        val preguntas = preguntasTest.text.toString().split(",").map { it.trim() }
        val opciones = opcionesTest.text.toString().split(",").map { it.trim() }
        val valores = valoresTest.text.toString().split(",").map { it.trim() }
        val explicacion = explicacionTest.text.toString().trim()

        //Verificar que todos los campos estén completos
        if (titulo.isEmpty() || descripcion.isEmpty() || preguntas.isEmpty() || opciones.isEmpty() || valores.isEmpty() || explicacion.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar que el número de opciones y valores sea el mismo
        if (opciones.size != valores.size) {
            Toast.makeText(requireContext(), "El número de opciones y valores debe ser el mismo", Toast.LENGTH_SHORT).show()
            return
        }



        val test = hashMapOf(
            "descripcion" to descripcion,
            "preguntas" to preguntas,
            "opciones" to opciones,
            "valores" to valores,
            "explicacion" to explicacion
        )

        FirebaseFirestore.getInstance().collection("Tests")
            .document(titulo)
            .set(test)
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