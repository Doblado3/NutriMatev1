package com.example.nutrimatev1.paciente

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrimatev1.LoginActivity
import com.example.nutrimatev1.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class BasePaciente: AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_paciente, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.item_home -> {
                startActivity(Intent(this, HomePacienteActivity::class.java))
                true
            }
            R.id.item_log_out -> {
                Firebase.auth.signOut() //Para cerrar la sesión al salir y que no se quede abierta
                startActivity(Intent(this, LoginActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}