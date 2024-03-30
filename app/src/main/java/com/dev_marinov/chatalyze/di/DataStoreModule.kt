package com.dev_marinov.chatalyze.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.dev_marinov.chatalyze.data.auth.dataStorePairToken
import com.dev_marinov.chatalyze.data.auth.dto.PairTokensDTO
import com.dev_marinov.chatalyze.data.store.data_store.PreferencesDataStoreRepositoryImpl
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
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
    fun providePreferences(@ApplicationContext context: Context): PreferencesDataStoreRepository =
        PreferencesDataStoreRepositoryImpl(context)

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context) : DataStore<PairTokensDTO> {
        return context.dataStorePairToken
    }
}