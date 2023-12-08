package com.dev_marinov.chatalyze.data.socket_service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.dev_marinov.chatalyze.presentation.util.SocketBroadcastReceiver

@Composable
fun SocketBroadcastReceiver(


) {


    // val socketBroadcastReceiver = SocketBroadcastReceiver()
    val filter = IntentFilter("receiver_action")

    val context = LocalContext.current


//
//    DisposableEffect(context) {
//        val socketBroadcastReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                when(intent.action) {
//                    "SESSION_ACTION_SUCCESS" -> {
//                        Log.d("4444", " socketBroadcastReceiver SESSION_ACTION_SUCCESS")
//                    }
//                    "SESSION_ACTION_ERROR" -> {
//                        Log.d("4444", " socketBroadcastReceiver SESSION_ACTION_ERROR")
//                    }
//                    "SESSION_ACTION_ClOSE" -> {
//                        Log.d("4444", " socketBroadcastReceiver SESSION_ACTION_ClOSE")
//                    }
//                }
//            }
//        }
//        context.registerReceiver(socketBroadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
//
//        onDispose {
//            context.unregisterReceiver(socketBroadcastReceiver)
//        }
//    }
}