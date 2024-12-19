package com.example.nutrimatev1.medico

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrimatev1.R
import com.example.nutrimatev1.modelo.Alert
import com.example.nutrimatev1.modelo.Paciente
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

        muestraPacientes()


    }

    private fun muestraPacientes(): Unit {
        var pacientes = mutableListOf<Paciente>()
        Firebase.firestore.collection("Medicos")
            .document(Firebase.auth.currentUser!!.email.toString())
            .collection("pacientes")
            .get()
            .addOnSuccessListener { docs ->
                for (d in docs){

                    var pac_nom = d.get("Nombre").toString()
                    var fechaNac = Calendar.getInstance()
                    fechaNac.time = d.getTimestamp("fecha de nacimiento")?.toDate()

                    pacientes.add(Paciente(d.id,pac_nom, fechaNac))



                }

                val adaptadorPacientes = AdaptadorPacientes(pacientes){it -> onClickPaciente(it)}

                listaPacientes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                listaPacientes.adapter = adaptadorPacientes




                numPacientes.text = "Hay " + adaptadorPacientes.itemCount + " pacientes"
            }
            .addOnFailureListener{

                Alert.showAlert(requireContext(), "Hay un error")
            }

    }

    fun onClickPaciente(paciente: Paciente){

        val fragment = DetallesPacFragmentMed()
        fragment.arguments = Bundle().apply {
            putString("id", paciente.id)
            putString("nombre", paciente.nombre)
            putSerializable("fechaNacimiento", paciente.fechanac)
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Ajusta el ID al contenedor de tus fragments
            .addToBackStack(null) // Permite volver atrás
            .commit()


    }

    inner class AdaptadorPacientes(private val dataSet: List<Paciente>, private val clickListener: (Paciente)->Unit):
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
            holder.itemView.setOnClickListener{clickListener(dataSet[position])}

        }

    }


}