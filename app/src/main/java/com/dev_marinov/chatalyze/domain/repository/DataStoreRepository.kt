package com.dev_marinov.chatalyze.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val getHideBottomBar: Flow<Boolean?>
    suspend fun saveHideNavigationBar(key: String, isHide: Boolean)

    suspend fun getScrollChatPosition(keyUserName: String) : Flow<Int?>
    suspend fun saveScrollChatPosition(key: String, position: Int)
}