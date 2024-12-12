package com.example.nutrimatev1.medico

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.nutrimatev1.R
import com.example.nutrimatev1.modelo.Paciente
import java.util.Calendar

class HomeFragmentMed : Fragment() {

    private lateinit var numPacientes: TextView
    private lateinit var listaPacientes: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_med, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        numPacientes = view.findViewById(R.id.textView_numPacientes)
        listaPacientes = view.findViewById(R.id.recyclerView_Pacientes)

        val adaptadorPacientes = AdaptadorPacientes(leePacientes())

        listaPacientes.layoutManager = LinearLayoutManager(requireContext())
        listaPacientes.adapter = adaptadorPacientes

        numPacientes.text = "Tienes " + adaptadorPacientes.itemCount + " pacientes asignados"


    }

    private fun leePacientes(): List<Paciente> {
        return listOf(Paciente("Pepito", Calendar.getInstance()),
            Paciente("Pepita", Calendar.getInstance()),
            Paciente("Pepon", Calendar.getInstance()))

    }

    inner class AdaptadorPacientes(private val dataSet: List<Paciente>):
            RecyclerView.Adapter<AdaptadorPacientes.ViewHolder>(){

                inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
                    val textView_nom: TextView
                    val textView_fch: TextView

                    init{
                        textView_nom = view.findViewById(R.id.textView_nombrePac)
                        textView_fch = view.findViewById(R.id.textView_fechanacPac)
                    }
                }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_pacientes, parent, false)

            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView_nom.text = dataSet[position].nombre
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            val formattedDate = dateFormat.format(dataSet[position].fechanac.time)
            holder.textView_fch.text = formattedDate
        }

    }


}