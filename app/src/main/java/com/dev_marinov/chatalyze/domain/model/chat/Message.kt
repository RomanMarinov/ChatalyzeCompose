package com.dev_marinov.chatalyze.domain.model.chat

data class Message(
    val sender: String,
    val recipient: String,
    val textMessage: String,
    val createdAt: String
)


//data class Message(
//    val text: String,
//    val formattedTime: String,
//    val username: String
//)
