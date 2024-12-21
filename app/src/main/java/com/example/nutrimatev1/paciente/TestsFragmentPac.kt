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


class TestsFragmentPac : Fragment() {


    private lateinit var recyclerViewTests: RecyclerView
    private lateinit var testAdapter: TestAdapter

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
        // Simular lista de tests disponibles
        val tests = listOf(
            "Test 1",
            "Test 2",
            "Test 3",
            "Test 4"
        )

        // Configurar el adaptador
        testAdapter = TestAdapter(tests)
        recyclerViewTests.adapter = testAdapter

        // Actualizar el TextView con el número de tests
        val tvTitleTestsPac: TextView = view.findViewById(R.id.tvTitleTestsPac)
        tvTitleTestsPac.text = "Tests Disponibles = ${tests.size}"

        return view
    }

    inner class TestAdapter(private val tests: List<String>) : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

        inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val testName: TextView = itemView.findViewById(R.id.tvTestName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false)
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