package com.dev_marinov.chatalyze.di

import com.dev_marinov.chatalyze.data.auth.AuthRepositoryImpl
import com.dev_marinov.chatalyze.data.chatalyze.local.ChatalyzeRepositoryImpl
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.ChatalyzeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatalyzeRepository(chatalyzeRepositoryImpl: ChatalyzeRepositoryImpl): ChatalyzeRepository
}