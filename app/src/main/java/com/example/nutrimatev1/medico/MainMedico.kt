package com.example.nutrimatev1.medico

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.nutrimatev1.AuthActivity
import com.example.nutrimatev1.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.activity.OnBackPressedCallback

/* Este es el activity encargado de soportar los distintos fragments que hemos creado
 para usar el Navigation Drawer*/


class MainMedico : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_medico)

        drawerLayout = findViewById(R.id.drawer_layout_medico)

        //Aunque técnicamente ya no usemos un toolbar, para abrir y cerrar
        //el NavigationDrawer se requiere implementarlo
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //Establecemos que por defecto queremos mostrar el Home
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }


        //"Mecanismo" para hacer la "experiencia" de ir hacia atrás dentro de la app
        //más fluida
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    //Esta función se encarga de registrar qué item del Navigation Drawer hemos seleccionado
    //y en funciíon de esto nos cambia las pantallas
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> replaceFragment(HomeFragment())
            R.id.nav_perfil -> replaceFragment(PerfilFragment())
            R.id.nav_logout -> showLogoutConfirmationDialog()

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }


    //Lógica del botón de cerrar sesión dentro del Navigation Drawer
    //El funcionamiento de log out de Firebase es el mismo que usábamos para el toolbar
    private fun showLogoutConfirmationDialog(){
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
        builder.setTitle("Cerrar Sesión")
            .setMessage("¿Seguro que quieres cerrar sesión?")
            .setPositiveButton("Yes"){_, _ ->
                Firebase.auth.signOut()
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()

    }
}
