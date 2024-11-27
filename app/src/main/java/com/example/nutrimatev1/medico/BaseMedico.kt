package com.example.nutrimatev1.medico

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrimatev1.AuthActivity
import com.example.nutrimatev1.R
import com.example.nutrimatev1.paciente.HomePacienteActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class BaseMedico: AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_medico, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.item_home_medico -> {
                startActivity(Intent(this, HomeMedicoActivity::class.java))
                true
            }
            R.id.item_log_out_medico -> {
                Firebase.auth.signOut() //Para cerrar la sesión al salir y que no se quede abierta
                startActivity(Intent(this, AuthActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}