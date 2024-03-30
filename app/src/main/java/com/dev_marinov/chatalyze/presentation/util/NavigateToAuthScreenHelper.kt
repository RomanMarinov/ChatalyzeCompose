package com.dev_marinov.chatalyze.presentation.util

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.net.Uri

object NavigateToAuthScreenHelper {
    fun execute(context: Context) {
        val deepLink = Uri.parse("auth_screen")
        val taskDetailIntent = Intent(
            Intent.ACTION_VIEW,
            deepLink,
//                    this,
//                    MainScreensActivity::class.java
            //MainActivity::class.java
        )

        val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(taskDetailIntent)
            // addParentStack(MainScreensActivity::class.java)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
        pendingIntent.send()
    }
}