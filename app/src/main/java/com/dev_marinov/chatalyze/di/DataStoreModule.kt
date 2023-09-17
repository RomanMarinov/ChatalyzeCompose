package com.dev_marinov.chatalyze.di

import android.content.Context
import com.dev_marinov.chatalyze.data.chatalyze.local.ChatalyzeRepositoryImpl
import com.dev_marinov.chatalyze.data.data_store.DataStoreRepositoryImpl
import com.dev_marinov.chatalyze.domain.repository.ChatalyzeRepository
import com.dev_marinov.chatalyze.domain.repository.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Singleton
    @Provides
    fun providePreferences(@ApplicationContext context: Context): DataStoreRepository =
        DataStoreRepositoryImpl(context)
}