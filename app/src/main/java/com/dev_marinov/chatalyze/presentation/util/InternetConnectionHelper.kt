package com.dev_marinov.chatalyze.presentation.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object InternetConnectionHelper {
    fun execute(context: Context): Boolean {
        var result = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.run {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> { false }
                }
            }
        }
        return result
    }
}