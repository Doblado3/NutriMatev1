package com.example.nutrimatev1.paciente

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
import com.example.nutrimatev1.modelo.Cita
import com.example.nutrimatev1.modelo.Paciente
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar


class CitasFragmentPac : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_citas_pac, container, false)
    }

    private lateinit var CitasRecycler: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CitasRecycler = view.findViewById(R.id.recycleCitasPac)

        muestraCitas()

    }

    private fun muestraCitas() {

        var citas = mutableListOf<Cita>()
        Firebase.firestore.collection("Pacientes")
            .document(Firebase.auth.currentUser!!.email.toString())
            .collection("citas")
            .get()
            .addOnSuccessListener { c ->
                for (cita in c) {


                    var med_nom = cita.get("nombre").toString()
                    var email_med = cita.get("emailMedico").toString()
                    var fechaCita = Calendar.getInstance()
                    fechaCita.time = cita.getTimestamp("fechahora")?.toDate()

                    citas.add(Cita(cita.id,med_nom, email_med, fechaCita))

                }


                CitasRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                CitasRecycler.adapter = AdaptadorCitas(citas)


            }
            .addOnFailureListener{
                Alert.showAlert(requireContext(), "Ha habido un error")
            }

    }

    inner class AdaptadorCitas(private val dataSet: List<Cita>):
        RecyclerView.Adapter<AdaptadorCitas.ViewHolder>(){

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
            val textView_nomMed: TextView = view.findViewById(R.id.textViewNombreMedCita)
            val textView_fechaCita: TextView = view.findViewById(R.id.textViewfechaCitaPac)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_citas, parent, false)

            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView_nomMed.text = "Médico: ${dataSet[position].medico}"
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            val formattedDate = dateFormat.format(dataSet[position].fecha.time)
            holder.textView_fechaCita.text = "Fecha: ${formattedDate}"
            }

        }



}
