package com.dev_marinov.chatalyze.data.chatMessage.dto

import com.dev_marinov.chatalyze.domain.model.chat.Message
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.util.*

@Serializable
data class MessageDto(
    val sender: String,
    val recipient: String,
    val textMessage: String,
    val createdAt: String
) {
    fun toMessage(): Message {
        return Message(
            sender = sender,
            recipient = recipient,
            textMessage = textMessage,
            createdAt = createdAt
        )
    }
}

//@Serializable
//data class MessageDto(
//    val text: String,
//    val timestamp: Long,
//    val username: String,
//   // val id: String
//) {
//    fun toMessage(): Message {
//        val date = Date(timestamp)
//        val formattedDate = DateFormat
//            .getDateInstance(DateFormat.DEFAULT)
//            .format(date)
//        return Message(
//            text = text,
//            formattedTime = formattedDate,
//            username = username
//        )
//    }
//}

//@Serializable
//data class MessageDto(
//    val text: String,
//    val timestamp: Long,
//    val username: String,
//    val id: String
//) {
//    fun toMessage(): Message {
//        val date = Date(timestamp)
//        val formattedDate = DateFormat
//            .getDateInstance(DateFormat.DEFAULT)
//            .format(date)
//        return Message(
//            text = text,
//            formattedTime = formattedDate,
//            username = username
//        )
//    }
//}
