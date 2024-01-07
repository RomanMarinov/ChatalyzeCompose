package com.dev_marinov.chatalyze.di

import com.dev_marinov.chatalyze.data.firebase.BroadcastReceiverNotification
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BroadcastReceiverModule {

    @Singleton
    @Provides
    fun provideBroadcastReceiver(): BroadcastReceiverNotification {
        return BroadcastReceiverNotification()
    }
}