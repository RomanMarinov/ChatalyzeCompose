package com.dev_marinov.chatalyze.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val getHideBottomBar: Flow<Boolean?>
    suspend fun saveHideNavigationBar(key: String, isHide: Boolean)

    suspend fun getScrollChatPosition(keyUserName: String) : Flow<Int?>
    suspend fun saveScrollChatPosition(key: String, position: Int)

    val getTokenRegister: Flow<String>
    suspend fun saveTokenRegister(keyTokenRegister: String, tokenRegister: String)

    val getTokenSignIn: Flow<String>
    suspend fun saveTokenSignIn(keyTokenSignIn: String, tokenSignIn: String)
    suspend fun logout(keyTokenSignIn: String)
}