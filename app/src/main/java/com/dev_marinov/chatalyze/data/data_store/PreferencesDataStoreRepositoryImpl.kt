package com.dev_marinov.chatalyze.data.data_store

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFERENCES_NAME = "DataStore"
private val Context.dataStore by preferencesDataStore(name = PREFERENCES_NAME)

@Singleton
class PreferencesDataStoreRepositoryImpl @Inject constructor(val context: Context) :
    PreferencesDataStoreRepository {

    override val getHideBottomBar: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        val preferencesKey = booleanPreferencesKey(Constants.HIDE_BOTTOM_BAR)
        preferences[preferencesKey]
    }

    override suspend fun saveHideNavigationBar(key: String, isHide: Boolean) {
        val preferenceKey = booleanPreferencesKey(key)
        context.dataStore.edit {
            it[preferenceKey] = isHide
        }
    }

    override suspend fun getScrollChatPosition(keyUserName: String): Flow<Int?> {
        return context.dataStore.data.map { preferences ->
            val preferencesKey = intPreferencesKey(keyUserName)
            preferences[preferencesKey]
        }
    }

    override suspend fun saveScrollChatPosition(key: String, position: Int) {
        val preferencesKey = intPreferencesKey(key)
        context.dataStore.edit {
            it[preferencesKey] = position
        }
    }

    override val getEmail: Flow<String> = context.dataStore.data.map { preferences ->
        val preferencesKey = stringPreferencesKey(Constants.KEY_EMAIL)
        preferences[preferencesKey] ?: ""
    }


    override suspend fun saveEmail(key: String, email: String) {
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit {
            it[preferencesKey] = email
        }
    }


}