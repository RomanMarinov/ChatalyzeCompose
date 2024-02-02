package com.dev_marinov.chatalyze.domain.model.call

data class HistoryCallWithName(
    val clientCallPhone: String,
    val senderPhone: String,
    val recipientPhone: String,
    val senderPhoneName: String,
    val recipientPhoneName: String,
    val conversationTime: String,
    val createdAt: String
)
