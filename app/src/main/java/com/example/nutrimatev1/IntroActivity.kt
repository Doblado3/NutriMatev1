package com.example.nutrimatev1

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class IntroActivity : AppCompatActivity() {


    private lateinit var introBtn: Button
    private lateinit var textIntro4: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_intro)

        introBtn = findViewById(R.id.introBtn)
        textIntro4 = findViewById(R.id.textIntro4)



        introBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        //Subrayar Iniciar Sesión
        val fullText = getString(R.string.inicia_sesion)
        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf("Inicia Sesión")
        val endIndex = startIndex + "Inicia Sesión".length

        spannableString.setSpan(UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textIntro4.text = spannableString

        textIntro4.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }



}