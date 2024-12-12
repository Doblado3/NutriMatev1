package com.example.nutrimatev1.paciente

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.nutrimatev1.R


class HomeFragmentPac : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_pac, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.LLInfo).setOnClickListener { onClickHomePacientes(it) }
        view.findViewById<View>(R.id.LLHistorial).setOnClickListener { onClickHomePacientes(it) }
        view.findViewById<View>(R.id.LLTests).setOnClickListener { onClickHomePacientes(it) }
        view.findViewById<View>(R.id.LLDiviertete).setOnClickListener { onClickHomePacientes(it) }


    }

    private fun onClickHomePacientes(v: View?) {
        when (v?.id) {
            R.id.LLTests -> {
                val intent = Intent(requireContext(), TestsActivity::class.java)
                startActivity(intent)
            }
            else -> {
                Toast.makeText(requireContext(), "Aún no hay activity", Toast.LENGTH_SHORT).show()
            }
        }
    }


}