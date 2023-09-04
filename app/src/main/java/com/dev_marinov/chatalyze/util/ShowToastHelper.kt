package com.dev_marinov.chatalyze.util

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast

object ShowToastHelper {
    fun createToast(message: String, context: Context) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        val toastTextView = toast.view?.findViewById<TextView>(android.R.id.message)
        toastTextView?.gravity = Gravity.CENTER // Устанавливаем гравитацию текста в CENTER
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 130)
        toast.show()
    }
}