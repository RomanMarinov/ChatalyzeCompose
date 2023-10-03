package com.dev_marinov.chatalyze.data.data_store

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dev_marinov.chatalyze.domain.repository.DataStoreRepository
import com.dev_marinov.chatalyze.presentation.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFERENCES_NAME = "DataStore"
private val Context.dataStore by preferencesDataStore(name = PREFERENCES_NAME)

@Singleton
class DataStoreRepositoryImpl @Inject constructor(val context: Context) : DataStoreRepository {

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

    override val getTokenRegister: Flow<String> = context.dataStore.data.map { preferences ->
        val preferencesKey = stringPreferencesKey(Constants.KEY_TOKEN_REGISTER)
        preferences[preferencesKey] ?: ""
    }

    override suspend fun saveTokenRegister(keyTokenRegister: String, tokenRegister: String) {
        val preferencesKey = stringPreferencesKey(keyTokenRegister)
        context.dataStore.edit {
            it[preferencesKey] = tokenRegister
        }
    }

    override val getTokenSignIn: Flow<String> = context.dataStore.data.map { preferences ->
        val preferencesKey = stringPreferencesKey(Constants.KEY_TOKEN_SIGN_IN)
        preferences[preferencesKey] ?: ""
    }

    override suspend fun saveTokenSignIn(keyTokenSignIn: String, tokenSignIn: String) {
        val preferencesKey = stringPreferencesKey(keyTokenSignIn)
        context.dataStore.edit {
            it[preferencesKey] = tokenSignIn
        }
    }

    override suspend fun logout(keyTokenSignIn: String) {
        val preferencesKey = stringPreferencesKey(keyTokenSignIn)
        context.dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                set(preferencesKey, "")
            }
        }
    }
}