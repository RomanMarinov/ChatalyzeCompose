package com.dev_marinov.chatalyze.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesDataStoreRepository {
    val getHideBottomBar: Flow<Boolean?>
    suspend fun saveHideNavigationBar(key: String, isHide: Boolean)

    suspend fun getScrollChatPosition(keyUserName: String): Flow<Int?>
    suspend fun saveScrollChatPosition(key: String, position: Int)

    val getEmail: Flow<String>
    suspend fun saveEmail(key: String, email: String)
}