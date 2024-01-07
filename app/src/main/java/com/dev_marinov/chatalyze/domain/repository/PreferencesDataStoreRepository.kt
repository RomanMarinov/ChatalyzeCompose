package com.dev_marinov.chatalyze.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesDataStoreRepository {
    val getHideBottomBar: Flow<Boolean?>
    suspend fun saveHideNavigationBar(key: String, isHide: Boolean)

    suspend fun getScrollChatPosition(keyUserName: String): Flow<Int?>
    suspend fun saveScrollChatPosition(key: String, position: Int)

    val getEmail: Flow<String>
    suspend fun saveEmail(key: String, email: String)

    val getOwnPhoneSender: Flow<String>
    suspend fun saveOwnPhoneSender(key: String, ownPhoneSender: String)

    val isGrantedPermissions: Flow<Boolean>
    suspend fun saveGrantedPermissions(key: String, isGranted: Boolean)

    val isTheLifecycleEventNow: Flow<String>
    suspend fun saveLifecycleEvent(eventType: String)

    val isSessionState: Flow<String>
    suspend fun saveSessionState(sessionState: String)

    val firebaseToken: Flow<String>
    suspend fun saveFirebaseToken(firebaseToken: String)
}