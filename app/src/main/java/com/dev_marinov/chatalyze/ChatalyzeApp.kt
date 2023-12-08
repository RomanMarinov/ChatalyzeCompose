package com.dev_marinov.chatalyze

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatalyzeApp : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}