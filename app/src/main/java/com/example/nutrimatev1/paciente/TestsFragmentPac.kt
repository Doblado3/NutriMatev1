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
import com.google.firebase.firestore.FirebaseFirestore


class TestsFragmentPac : Fragment() {


    private lateinit var recyclerViewTests: RecyclerView
    private lateinit var testAdapter: TestAdapter
    private val testTitles = mutableListOf<String>()
    private lateinit var tvTitleTestsPac: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tests_pac, container, false)

        // Inicializar RecyclerView
        recyclerViewTests = view.findViewById(R.id.recyclerViewTests)
        recyclerViewTests.layoutManager = LinearLayoutManager(requireContext())

        recyclerViewTests.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // Configurar el adaptador
        testAdapter = TestAdapter(testTitles)
        recyclerViewTests.adapter = testAdapter


        tvTitleTestsPac = view.findViewById(R.id.tvTitleTestsPac)

        // Obtener los títulos de los tests desde Firestore
        bucarTitulosTestBD()


        return view
    }

    private fun bucarTitulosTestBD() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Tests")
            .get()
            .addOnSuccessListener { result ->
                // Limpia la lista antes de agregar los nuevos datos
                testTitles.clear()
                for (document in result) {
                    val title = document.getString("title")
                    if (title != null) {
                        testTitles.add(title)
                    }
                }
                testAdapter.notifyDataSetChanged()

                tvTitleTestsPac.text = "Tests Disponibles = ${testTitles.size}"
            }
            .addOnFailureListener { exception ->
                // Manejar el error
            }
    }

    inner class TestAdapter(private val tests: List<String>) : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

        inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val testName: TextView = itemView.findViewById(R.id.tvTestName)

            init {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val selectedTestTitle = tests[position]
                        val fragment = DoTestFragmentPac.newInstance(selectedTestTitle)
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false)
            return TestViewHolder(view)
        }

        override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
            holder.testName.text = tests[position]
        }

        override fun getItemCount(): Int {
            return tests.size
        }
    }
}