package com.dev_marinov.chatalyze.data.chatMessage.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageWrapper(
    val type: String,
    val payloadJson: String
)
