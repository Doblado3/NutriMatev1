package com.example.nutrimatev1.medico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nutrimatev1.AuthActivity
import com.example.nutrimatev1.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeMedicoActivity : BaseMedico() {

    private lateinit var medLogOut: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_medico)
        setSupportActionBar(findViewById(R.id.toolbar))

        medLogOut = findViewById(R.id.medLogOut)


        medLogOut.setOnClickListener{
            Firebase.auth.signOut()
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }
}