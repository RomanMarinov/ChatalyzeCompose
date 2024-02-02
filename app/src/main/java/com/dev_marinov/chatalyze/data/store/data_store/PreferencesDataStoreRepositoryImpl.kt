package com.dev_marinov.chatalyze.data.store.data_store

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

    override val getOwnPhoneSender: Flow<String> = context.dataStore.data.map { preferences ->
        val preferencesKey = stringPreferencesKey(Constants.OWN_PHONE_SENDER)
        preferences[preferencesKey] ?: ""
    }

    override suspend fun saveOwnPhoneSender(key: String, ownPhoneSender: String, ) {
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = ownPhoneSender
        }
    }

//    override val isGrantedPermissions: Flow<Boolean> = context.dataStore.data.map { value: Preferences ->
//        val preferencesKey = booleanPreferencesKey(Constants.IS_GRANTED_PERMISSIONS)
//        value[preferencesKey] == true
//    }

    override val isGrantedPermissions: Flow<Boolean> = context.dataStore.data.map { value: Preferences ->
        val preferencesKeyReadPhoneNumber = booleanPreferencesKey(Constants.KEY_READ_PHONE_NUMBERS)
        val preferencesKeyReadContacts = booleanPreferencesKey(Constants.KEY_READ_CONTACTS)
        value[preferencesKeyReadPhoneNumber] == true && value[preferencesKeyReadContacts] == true
    }

    override suspend fun saveGrantedPermissions(key: String, isGranted: Boolean) {
        val preferencesKey = booleanPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = isGranted
        }
    }

    override val isTheLifecycleEventNow: Flow<String> = context.dataStore.data.map { preferences ->
        val preferencesKey = stringPreferencesKey(Constants.IS_LIFECYCLE_EVENT)
        preferences[preferencesKey] ?: ""
    }

    override suspend fun saveLifecycleEvent(eventType: String) {
        val preferencesKey = stringPreferencesKey(Constants.IS_LIFECYCLE_EVENT)
        context.dataStore.edit {
            it[preferencesKey] = eventType
        }
    }

    override val isSessionState: Flow<String> = context.dataStore.data.map {
        val preferencesKey = stringPreferencesKey(Constants.IS_SESSION_STATE)
        it[preferencesKey] ?: ""
    }

    override suspend fun saveSessionState(sessionState: String) {
        val preferencesKey = stringPreferencesKey(Constants.IS_SESSION_STATE)
        context.dataStore.edit {
            it[preferencesKey] = sessionState
        }
    }

    override val firebaseToken: Flow<String> = context.dataStore.data.map { preferences ->
        val preferencesKey = stringPreferencesKey(Constants.KEY_FIREBASE_TOKEN)
        preferences[preferencesKey] ?: ""
    }

    override suspend fun saveFirebaseToken(firebaseToken: String) {
        val preferencesKey = stringPreferencesKey(Constants.KEY_FIREBASE_TOKEN)
        context.dataStore.edit {  preferences ->
            preferences[preferencesKey] = firebaseToken
        }
    }

    override val pushTypeDisplayFlow: Flow<Int> = context.dataStore.data.map { value: Preferences ->
        val preferencesKey = intPreferencesKey(Constants.PUSH_TYPE_DISPLAY)
        value[preferencesKey] ?: -1
    }

    override suspend fun savePushTypeDisplay(selectedBoxIndex: Int) {
        val preferencesKey = intPreferencesKey(Constants.PUSH_TYPE_DISPLAY)
        context.dataStore.edit {
            it[preferencesKey] = selectedBoxIndex
        }
    }

    override val hideDialogPermissionNotificationFlow: Flow<Boolean> = context.dataStore.data.map { value: Preferences ->
        val preferencesKey = booleanPreferencesKey(Constants.HIDE_DIALOG_PERMISSION_NOTIFICATION)
        value[preferencesKey] == true
    }

    override suspend fun saveHideDialogPermissionNotification(hide: Boolean) {
        val preferencesKey = booleanPreferencesKey(Constants.HIDE_DIALOG_PERMISSION_NOTIFICATION)
        context.dataStore.edit {
            it[preferencesKey] = hide
        }

    }
}