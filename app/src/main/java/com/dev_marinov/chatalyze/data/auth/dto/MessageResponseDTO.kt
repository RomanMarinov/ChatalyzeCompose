package com.dev_marinov.chatalyze.data.auth.dto

import com.google.gson.annotations.SerializedName

data class MessageResponseDTO(
    @SerializedName("httpStatusCode")
    val httpStatusCode: Int,
    @SerializedName("message")
    val message: String
)

