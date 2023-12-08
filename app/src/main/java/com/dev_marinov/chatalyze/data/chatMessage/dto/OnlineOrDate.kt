package com.dev_marinov.chatalyze.data.chatMessage.dto

import kotlinx.serialization.Serializable

@Serializable
data class OnlineOrDate(
    val userPhone: String,
    val onlineOrDate: String
)
