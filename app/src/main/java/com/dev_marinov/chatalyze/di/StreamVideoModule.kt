package com.dev_marinov.chatalyze.di

import android.app.Application
import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User
import javax.inject.Named
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object ContextModule {
//    @Provides
//    fun provideContext(application: Application): Context {
//        return application
//    }
//}
//
//
//@Module
//@InstallIn(SingletonComponent::class)
//object StreamModule {
//
//
//
//
//    @Provides
//    @Singleton
//    fun provideStreamVideo(
//        context: Context,
//        user: User,
//        userToken: String
//    ): StreamVideo {
//        return StreamVideoBuilder(
//            context = context,
//            apiKey = "v8rmt3faerck", // demo API key
//            geo = GEO.GlobalEdgeNetwork,
//            user = user,
//            token = userToken
//        ).build()
//    }
//}