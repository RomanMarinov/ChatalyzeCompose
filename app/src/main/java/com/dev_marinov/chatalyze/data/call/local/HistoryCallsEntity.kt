package com.dev_marinov.chatalyze.data.call.local

import androidx.room.*

@Entity(tableName = "history_calls")
data class HistoryCallsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "client_call_phone")
    val clientCallPhone: String,
    @ColumnInfo(name = "sender_phone")
    val senderPhone: String,
    @ColumnInfo(name = "recipient_phone")
    val recipientPhone: String,
    @ColumnInfo(name = "conversation_time")
    val conversationTime: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String
)