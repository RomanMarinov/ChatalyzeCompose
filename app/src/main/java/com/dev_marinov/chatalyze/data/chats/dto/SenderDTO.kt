package com.dev_marinov.chatalyze.data.chats.dto

import kotlinx.serialization.Serializable

@Serializable
data class SenderDTO(
    val sender: String,
    val refreshToken: String
)
