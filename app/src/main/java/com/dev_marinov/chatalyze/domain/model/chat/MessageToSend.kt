package com.dev_marinov.chatalyze.domain.model.chat

data class MessageToSend(
    val sender: String,
    val recipient: String,
    val textMessage: String,
    val refreshToken: String
)
