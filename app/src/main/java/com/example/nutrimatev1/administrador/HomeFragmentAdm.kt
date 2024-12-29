package com.example.nutrimatev1.administrador

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.nutrimatev1.R


class HomeFragmentAdm : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_adm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.LLAddTest).setOnClickListener { onClickHomeAdministradores(it) }
        view.findViewById<View>(R.id.LLAsigMed).setOnClickListener { onClickHomeAdministradores(it) }


    }

    private fun onClickHomeAdministradores(v: View?) {
        when (v?.id) {
            R.id.LLAddTest -> {
                val fragment = AddTestsFragmentAdm()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            else -> {
                Toast.makeText(requireContext(), "Aún no hay activity", Toast.LENGTH_SHORT).show()
            }
        }
    }


}