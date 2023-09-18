package com.dev_marinov.chatalyze.data.chatalyze.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.dev_marinov.chatalyze.domain.repository.ChatalyzeRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChatalyzeRepositoryImpl @Inject constructor(val context: Context) : ChatalyzeRepository {




}