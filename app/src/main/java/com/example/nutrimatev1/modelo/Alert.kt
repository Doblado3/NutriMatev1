package com.example.nutrimatev1.modelo

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.nutrimatev1.R

object Alert {

    fun showAlert(context: Context, text: String){

        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
        builder
            .setMessage(text)
            .setTitle("Información")
            .setPositiveButton("ACEPTAR", null)

        val dialog: AlertDialog = builder.create() //Lanzamos el builder
        dialog.show()

    }
}