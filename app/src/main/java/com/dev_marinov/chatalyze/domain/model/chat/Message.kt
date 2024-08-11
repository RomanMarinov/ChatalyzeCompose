package com.dev_marinov.chatalyze.domain.model.chat

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val sender: String,
    val recipient: String,
    val textMessage: String,
    val createdAt: String
)