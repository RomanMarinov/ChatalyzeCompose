package com.dev_marinov.chatalyze.presentation.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import javax.inject.Inject

class SocketBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            "SESSION_ACTION_SUCCESS" -> {

            }
            "SESSION_ACTION_ERROR" -> {

            }
            "SESSION_ACTION_ClOSE" -> {

            }
        }
    }
}