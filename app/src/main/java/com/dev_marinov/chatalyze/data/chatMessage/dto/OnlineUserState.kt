package com.dev_marinov.chatalyze.data.chatMessage.dto

import kotlinx.serialization.Serializable

@Serializable
data class OnlineUserState(
    val userPhone: String,
    val onlineOrOffline: String
)
