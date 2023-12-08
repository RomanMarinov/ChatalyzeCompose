package com.dev_marinov.chatalyze.domain.model.chats

data class Chat(
    val sender: String,
    val recipient: String,
    val textMessage: String,
    val createdAt: String
)
