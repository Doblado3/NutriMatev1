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
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class HomeFragmentMed : Fragment() {

    private lateinit var numPacientes: TextView
    private lateinit var listaPacientes: RecyclerView
    private lateinit var nomMed: TextView



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
        nomMed = view.findViewById(R.id.textNombreMedHome)

        muestraPacientes()
        muestraNombreMed()


    }

    private fun muestraNombreMed() {
        //accedemos al nombre del médico registrado con su email
        val userEmail = Firebase.auth.currentUser?.email

        if (userEmail != null) {
            Firebase.firestore.collection("Medicos")
                .document(userEmail)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombreMedico = document.getString("nombre") ?: "Médico"
                        nomMed.text = "Bienvenido, $nombreMedico"
                    } else {
                        nomMed.text = "Bienvenido, Médico"
                    }
                }
                .addOnFailureListener {
                    nomMed.text = "Bienvenido, Médico"
                    Alert.showAlert(requireContext(), "Error al cargar el nombre del médico")
                }
        } else {
            nomMed.text = "Bienvenido, Médico"
        }
    }

    private fun muestraPacientes(): Unit {
        val pacientes = mutableListOf<Paciente>()
        val currentUserEmail = Firebase.auth.currentUser?.email

        if (currentUserEmail.isNullOrEmpty()) {
            Alert.showAlert(requireContext(), "Usuario no autenticado.")
            return
        }

        Firebase.firestore.collection("Medicos")
            .document(currentUserEmail)
            .collection("pacientes")
            .get()
            .addOnSuccessListener { docs ->
                val pacientesEmails = docs.mapNotNull { it.getString("email") } // Filtra nulos

                if (pacientesEmails.isEmpty()) {
                    numPacientes.text = "No hay pacientes asignados."
                    return@addOnSuccessListener
                }

                // Consultar la colección Pacientes para obtener más información
                Firebase.firestore.collection("Pacientes")
                    .whereIn(FieldPath.documentId(), pacientesEmails) // Filtrar por los correos
                    .get()
                    .addOnSuccessListener { pacientesDocs ->
                        for (d in pacientesDocs) {
                            val pacNom = d.getString("nombre") ?: "Desconocido"
                            val pacAp = d.getString("apellidos") ?: "Desconocido"


                            pacientes.add(Paciente(d.id, pacNom, pacAp))
                        }

                        // Configurar el adaptador
                        val adaptadorPacientes = AdaptadorPacientes(pacientes) { it -> onClickPaciente(it) }
                        listaPacientes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                        listaPacientes.adapter = adaptadorPacientes
                        numPacientes.text = "Hay ${adaptadorPacientes.itemCount} pacientes"
                    }
                    .addOnFailureListener {
                        Alert.showAlert(requireContext(), "Error al cargar información de pacientes.")
                    }
            }
            .addOnFailureListener {
                Alert.showAlert(requireContext(), "Error al cargar los correos de los pacientes.")
            }
    }

    fun onClickPaciente(paciente: Paciente){

        val fragment = DetallesPacFragmentMed()
        fragment.arguments = Bundle().apply {
            putString("id", paciente.id)
            putString("nombre", paciente.nombre)
            putString("apellidos", paciente.apellidos)
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
                    val textView_ap: TextView

                    init{
                        textView_nom = view.findViewById(R.id.textView_nombrePac)
                        textView_ap = view.findViewById(R.id.textView_apellidosPac)
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
            holder.textView_ap.text = dataSet[position].apellidos


            holder.itemView.setOnClickListener{clickListener(dataSet[position])}

        }

    }


}