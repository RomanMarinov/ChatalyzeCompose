package com.dev_marinov.chatalyze.presentation.ui.call_screen.model

import android.os.IBinder.DeathRecipient

data class UserCall(
    val event: String,
    val sender: String,
    val recipient: String
)
