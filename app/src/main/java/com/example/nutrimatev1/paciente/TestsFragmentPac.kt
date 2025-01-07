package com.example.nutrimatev1.paciente

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrimatev1.R
import com.example.nutrimatev1.medico.HomeFragmentMed
import com.example.nutrimatev1.modelo.Cita
import com.example.nutrimatev1.modelo.Test
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList


class TestsFragmentPac : Fragment() {


    private lateinit var recyclerViewTests: RecyclerView




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tests_pac, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        recyclerViewTests = view.findViewById(R.id.recyclerViewTests)


        cargarTests()

    }

    fun onClickTest(test: Test){

        val fragment = DoTestFragmentPac()
        fragment.arguments = Bundle().apply{
            putString("id", test.id)
            putString("descripcion", test.descripcion)
            putString("explicacion", test.explicacion)
            putStringArrayList("opciones", ArrayList(test.opciones))
            putStringArrayList("preguntas", ArrayList(test.preguntas))
            putStringArrayList("valores", ArrayList(test.valores))

        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Ajusta el ID al contenedor de tus fragments
            .addToBackStack(null) // Permite volver atrás
            .commit()
    }





    private fun cargarTests() {
        var tests = mutableListOf<Test>()
        FirebaseFirestore.getInstance().collection("Tests")
            .get()
            .addOnSuccessListener { result ->
                for (test in result) {
                    val descripcion = test.get("descripcion").toString()
                    val explicacion = test.get("explicacion").toString()

                    val opciones = test.get("opciones") as? List<String> ?: emptyList()
                    val preguntas = test.get("preguntas") as? List<String> ?: emptyList()
                    val valores = test.get("valores") as? List<String> ?: emptyList()

                    tests.add(Test(test.id,descripcion,explicacion,opciones,preguntas,valores))


                }

                val adaptadorTest = AdaptadorTest(tests){it -> onClickTest(it)}


                recyclerViewTests.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                recyclerViewTests.adapter = adaptadorTest


            }

    }

    inner class AdaptadorTest(private val dataSet: List<Test>, private val clickListener: (Test)->Unit) :
        RecyclerView.Adapter<AdaptadorTest.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val testName: TextView = itemView.findViewById(R.id.tvTestName)
            val testDescripcion: TextView = itemView.findViewById(R.id.textDescripcionTest)

            /*init {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val selectedTestTitle = tests[position]
                        val fragment = DoTestFragmentPac.newInstance(selectedTestTitle)
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit()
                    }*/


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).
            inflate(R.layout.item_test, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.testName.text = dataSet[position].id
            holder.testDescripcion.text = dataSet[position].descripcion
            holder.itemView.setOnClickListener{clickListener(dataSet[position])}

        }

        override fun getItemCount(): Int {
            return dataSet.size
        }
    }
}