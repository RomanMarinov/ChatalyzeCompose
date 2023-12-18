package com.dev_marinov.chatalyze.data.chatMessage.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class MessageWrapper(
    val type: String,
   // @SerializedName("payload")
    val payloadJson: String
)
